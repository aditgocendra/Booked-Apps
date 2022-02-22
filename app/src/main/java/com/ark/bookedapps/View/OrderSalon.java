package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.Notification.ClientApi;
import com.ark.bookedapps.Notification.ServiceApi;
import com.ark.bookedapps.Notification.ServiceNotification;
import com.ark.bookedapps.R;
import com.ark.bookedapps.Utility.Constant;
import com.ark.bookedapps.Utility.Utilities;
import com.ark.bookedapps.databinding.ActivityOrderSalonBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderSalon extends AppCompatActivity {

    private ActivityOrderSalonBinding binding;
    private int hour, minute;
    private String namePackage, price, urlThumbs, detail, key;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSalonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        
        namePackage = getIntent().getStringExtra("name_package");
        price = getIntent().getStringExtra("price");
        urlThumbs = getIntent().getStringExtra("url_thumbs");
        detail = getIntent().getStringExtra("detail");
        key = getIntent().getStringExtra("key");
        
        binding.namePackageDetail.setText(namePackage);
        binding.priceDetail.setText("Rp. "+formatCurrenryRp(price));
        Picasso.get().load(urlThumbs).into(binding.thumbsPackage);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderSalon.this, PackageDetail.class);
                intent.putExtra("name_package", namePackage);
                intent.putExtra("price", price);
                intent.putExtra("url_thumbs", urlThumbs);
                intent.putExtra("detail", detail);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

        datePicker();

        binding.timePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker();
            }
        });

        binding.saveOrderSalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.dateOrder.getText().toString().isEmpty()){
                    binding.dateOrder.setError("Field tidak boleh kosong");

                }else if (binding.timeOrder.getText().toString().isEmpty()) {
                    binding.timeOrder.setError("Field tidak boleh kosong");
                }else {
                    orderPackage();
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    binding.saveOrderSalon.setEnabled(false);
                }
            }
        });
    }


    private void orderPackage() {
        String time_order = binding.timeOrder.getText().toString();
        String date_order = binding.dateOrder.getText().toString();
        ModelOrder modelOrder = new ModelOrder(key, user.getUid(), date_order, time_order, "Belum dibayar", "Menunggu Konfirmasi Admin","-");

        reference.child("order_salon").child(user.getUid()).push().setValue(modelOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                binding.progressCircular.setVisibility(View.INVISIBLE);
                binding.saveOrderSalon.setEnabled(false);
                Toast.makeText(OrderSalon.this, "Order berhasil", Toast.LENGTH_SHORT).show();

                reference.child("token_user").child("admin").child("token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            sendCloud(task.getResult().getValue().toString());
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.progressCircular.setVisibility(View.INVISIBLE);
                binding.saveOrderSalon.setEnabled(false);
                Toast.makeText(OrderSalon.this, "Order gagal", Toast.LENGTH_SHORT).show();
                updateUI(PackageDetail.class);
                finish();
            }
        });



        updateUI(HomeApp.class);
        finish();

    }

    private void sendCloud(String tokenUser){
        try {
            JSONArray token = new JSONArray();
            token.put(tokenUser);

            JSONObject data = new JSONObject();
            data.put("uid_receiver", "-");
            data.put("receiver", "Admin");
            data.put("title", "Pesanan baru");
            data.put("message", "Haloo admin ada pesanan baru nih yuk cek");


            JSONObject body = new JSONObject();
            body.put(Constant.REMOTE_MSG_DATA, data);
            body.put(Constant.REMOTE_MSG_REGISTRATION_IDS, token);

            Utilities.sendNotification(body.toString(), OrderSalon.this);
        }catch (Exception e){
            Toast.makeText(OrderSalon.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void datePicker(){
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = builder.build();

        binding.datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                binding.dateOrder.setText(materialDatePicker.getHeaderText());
            }
        });
    }

    private void timePicker() {
        TimePickerDialog timePickerDialog;
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(OrderSalon.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfday, int minuteOfDay) {

                hour = hourOfday;
                minute = minuteOfDay;

                calendar.set(0,0,0, hour, minute);
                binding.timeOrder.setText(DateFormat.format("hh:mm aa", calendar));
            }
        }, 24, 0, true);


        timePickerDialog.show();


    }

    private void updateUI(Class activity){
        startActivity(new Intent(OrderSalon.this, activity));
    }

    private String formatCurrenryRp(String number){

        double decimalNumber = Double.parseDouble(number);

        Locale localeID = new Locale("in", "ID");
        NumberFormat rupiahFormat = NumberFormat.getNumberInstance(localeID);
        return rupiahFormat.format(decimalNumber);
    }
}