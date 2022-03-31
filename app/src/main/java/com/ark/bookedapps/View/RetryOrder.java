package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.Utility.BrokenConnection;
import com.ark.bookedapps.Utility.Constant;
import com.ark.bookedapps.Utility.Utilities;
import com.ark.bookedapps.databinding.ActivityRetryOrderBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class RetryOrder extends AppCompatActivity {

    private ActivityRetryOrderBinding binding;
    private String namePackage, price, thumbs, dateOrder, timeOrder, keyOrder, keyPackage;
    private int hour, minute;
    private String tokenUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRetryOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        namePackage = getIntent().getStringExtra("name_package");
        price = getIntent().getStringExtra("price");
        thumbs = getIntent().getStringExtra("thumbs_image");
        dateOrder = getIntent().getStringExtra("date_order");
        timeOrder = getIntent().getStringExtra("time_order");
        keyOrder = getIntent().getStringExtra("key_order");
        keyPackage = getIntent().getStringExtra("key_package");

        if (!Utilities.checkConnection(this)){
            updateUI(BrokenConnection.class);
            finish();
        }else {
            setDataOrder();
            getAdminToken();
        }

        datePicker();
        binding.timePick.setOnClickListener(view -> timePicker());
        binding.backBtn.setOnClickListener(view -> finish());
        binding.saveRetryOrderSalon.setOnClickListener(view -> updateDataOrder());
    }

    private void getAdminToken(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("token_user").child("admin").child("token").get().addOnCompleteListener(task -> tokenUser = task.getResult().getValue().toString());
    }

    private void updateDataOrder(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ModelOrder modelOrder = new ModelOrder(
                keyPackage,
                user.getUid(),
                binding.dateOrder.getText().toString(),
                binding.timeOrder.getText().toString(),
                "Belum dibayar",
                "Menunggu Konfirmasi Admin",
                "-");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("order_salon").child(user.getUid()).child(keyOrder).setValue(modelOrder).addOnSuccessListener(unused -> {
            Toast.makeText(RetryOrder.this, "Pengajuan ulang berhasil", Toast.LENGTH_SHORT).show();

            try {
                JSONArray token = new JSONArray();
                token.put(tokenUser);

                JSONObject data = new JSONObject();
                data.put("uid_receiver", "-");
                data.put("receiver", "Admin");
                data.put("title", "Pengajuan ulang pesanan");
                data.put("message", "Haloo admin ada yang melakukan pengajuan ulang pesanan nih");

                JSONObject body = new JSONObject();
                body.put(Constant.REMOTE_MSG_DATA, data);
                body.put(Constant.REMOTE_MSG_REGISTRATION_IDS, token);

                Utilities.sendNotification(body.toString(), RetryOrder.this);
            }catch (Exception e){
                Toast.makeText(RetryOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            updateUI(MyOrder.class);
            finish();
        }).addOnFailureListener(e -> Toast.makeText(RetryOrder.this, "Gagal mengajukan ulang pesanan", Toast.LENGTH_SHORT).show());
    }


    private void setDataOrder(){
        Picasso.get().load(thumbs).into(binding.thumbsPackage);

        binding.namePackageRetry.setText(namePackage);
        binding.priceRetry.setText(price);
        binding.dateOrder.setText(dateOrder);
        binding.timeOrder.setText(timeOrder);
    }

    private void datePicker(){
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("SELECT A DATE");
        final MaterialDatePicker<Long> materialDatePicker = builder.build();

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
        timePickerDialog = new TimePickerDialog(RetryOrder.this, (timePicker, hourOfday, minuteOfDay) -> {

            hour = hourOfday;
            minute = minuteOfDay;

            calendar.set(0,0,0, hour, minute);
            binding.timeOrder.setText(DateFormat.format("hh:mm aa", calendar));
        }, 24, 0, true);

        timePickerDialog.show();
    }

    private void updateUI(Class activity){
        startActivity(new Intent(RetryOrder.this, activity));
    }


}