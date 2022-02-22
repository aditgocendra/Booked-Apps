package com.ark.bookedapps.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelMessage;
import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.Notification.ClientApi;
import com.ark.bookedapps.Notification.ServiceApi;
import com.ark.bookedapps.R;
import com.ark.bookedapps.Utility.Constant;
import com.ark.bookedapps.Utility.Utilities;
import com.ark.bookedapps.View.ManageOrderCustomer;
import com.ark.bookedapps.View.OrderSalon;
import com.ark.bookedapps.View.PersonalChat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterManageOrder extends RecyclerView.Adapter<AdapterManageOrder.MyViewHolder> {

    private List<ModelOrder> listOrder;
    private Context mContext;


    public AdapterManageOrder(List<ModelOrder> listOrder,Context mContext){
        this.listOrder = listOrder;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View viewItem = layoutInflater.inflate(R.layout.layout_manage_order, parent, false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelOrder modelOrder = listOrder.get(position);

        holder.statusTv.setText(modelOrder.getStatus());
        holder.dateOrderTv.setText(modelOrder.getDate_order());
        holder.timeOrderTv.setText(modelOrder.getTime_order());

        if (modelOrder.getStatus().equals("Dibatalkan")){
            holder.statusTv.setTextColor(Color.RED);
            holder.alertCancel.setVisibility(View.VISIBLE);
            holder.btnConfirm.setEnabled(false);
            holder.btnCancel.setEnabled(false);
        }

        if (modelOrder.getStatus().equals("(Disetejui) Menunggu Pembayaran")){
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnPayConfirm.setVisibility(View.VISIBLE);

        }

        if (modelOrder.getStatus().equals("Pembayaran DP dikonfirmasi")){
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnPayConfirm.setVisibility(View.GONE);
            holder.btnOrderFinish.setVisibility(View.VISIBLE);
        }

        // set package data
        setDataPackage(modelOrder.getKey_package(), holder);

        // set username order
        setUserOrderData(modelOrder.getUser_id(), holder);

        // set token user
        setTokenUser(modelOrder.getUser_id(), holder);


        holder.alertCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle("Alasan pembatalan");
                builder.setMessage(modelOrder.getReason_cancel());
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle("Alasan Penolakan");

                final EditText reasonEditText = new EditText(mContext);
                reasonEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                builder.setView(reasonEditText);

                builder.setPositiveButton("Lanjutkan",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (reasonEditText.getText().toString().isEmpty()){
                            Toast.makeText(mContext, "Alasan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }else {

                            ModelOrder order = new ModelOrder(
                                    modelOrder.getKey_package(),
                                    modelOrder.getUser_id(),
                                    modelOrder.getDate_order(),
                                    modelOrder.getTime_order(),
                                    modelOrder.getPay_order(),
                                    "Dibatalkan",
                                    reasonEditText.getText().toString());

                            cancelOrderSalon(order, modelOrder.getKey());
                        }
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ModelOrder order = new ModelOrder(
                        modelOrder.getKey_package(),
                        modelOrder.getUser_id(),
                        modelOrder.getDate_order(),
                        modelOrder.getTime_order(),
                        modelOrder.getPay_order(),
                        "(Disetejui) Menunggu Pembayaran",
                        "-");

                confirmOrderSalon(order, modelOrder.getKey(), holder);
            }
        });

        holder.btnPayConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ModelOrder order = new ModelOrder(
                        modelOrder.getKey_package(),
                        modelOrder.getUser_id(),
                        modelOrder.getDate_order(),
                        modelOrder.getTime_order(),
                        "Telah dibayar",
                        "Pembayaran DP dikonfirmasi",
                        "-");

                confirmPayOrder(order, modelOrder.getKey());
            }
        });

        holder.btnOrderFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ModelOrder order = new ModelOrder(
                        modelOrder.getKey_package(),
                        modelOrder.getUser_id(),
                        modelOrder.getDate_order(),
                        modelOrder.getTime_order(),
                        "Telah dibayar",
                        "Pesanan selesai",
                        "-");

                orderFinish(order, modelOrder.getKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView namePackageTv, pricePackageTv, statusTv, dateOrderTv, timeOrderTv, nameOrderTv, tokenUser;
        Button btnCancel, btnConfirm, btnPayConfirm, btnOrderFinish;
        ImageView thumbsImage, alertCancel;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            namePackageTv = itemView.findViewById(R.id.name_package_tv_manage_order);
            pricePackageTv = itemView.findViewById(R.id.price_package_tv_manage_order);
            statusTv = itemView.findViewById(R.id.status_tv_manage);
            dateOrderTv = itemView.findViewById(R.id.date_tv_manage_order);
            timeOrderTv = itemView.findViewById(R.id.time_tv_manage_order);
            nameOrderTv = itemView.findViewById(R.id.user_order);
            thumbsImage = itemView.findViewById(R.id.thumbs_image_pakage_order);
            alertCancel = itemView.findViewById(R.id.alert_cancel);
            btnCancel = itemView.findViewById(R.id.cancel_manage);
            btnConfirm = itemView.findViewById(R.id.confirm_manage);
            btnPayConfirm = itemView.findViewById(R.id.confirm_pay);
            btnOrderFinish = itemView.findViewById(R.id.order_finish);

            tokenUser = itemView.findViewById(R.id.token_user);


        }
    }

    private void setUserOrderData(String user_id, MyViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(user_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                holder.nameOrderTv.setText(modelUser.getUsername());
            }
        });

    }

    private void setTokenUser(String uid, MyViewHolder holder){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("token_user").child(uid).child("token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                holder.tokenUser.setText(task.getResult().getValue().toString());
            }
        });
    }

    private String formatCurrenryRp(String number){
        double decimalNumber = Double.parseDouble(number);

        Locale localeID = new Locale("in", "ID");
        NumberFormat rupiahFormat = NumberFormat.getNumberInstance(localeID);
        return rupiahFormat.format(decimalNumber);
    }

    private void cancelOrderSalon(ModelOrder newOrder, String keyOrder){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("order_salon").child(newOrder.getUser_id()).child(keyOrder).setValue(newOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(mContext, "Berhasil menolak pesanan", Toast.LENGTH_SHORT).show();

                databaseReference.child("token_user").child(newOrder.getUser_id()).child("token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            try {
                                JSONArray token = new JSONArray();
                                token.put(task.getResult().getValue().toString());

                                JSONObject data = new JSONObject();
                                data.put("uid_receiver", newOrder.getUser_id());
                                data.put("receiver", "Customer");
                                data.put("title", "Pesanan ditolak");
                                data.put("message", "Haloo kak, nampaknya pesanan kakak ditolak nih");


                                JSONObject body = new JSONObject();
                                body.put(Constant.REMOTE_MSG_DATA, data);
                                body.put(Constant.REMOTE_MSG_REGISTRATION_IDS, token);

                                Utilities.sendNotification(body.toString(), mContext);
                            }catch (Exception e){
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, String.valueOf(e), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void confirmOrderSalon(ModelOrder newOrder, String keyOrder, MyViewHolder holder){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("order_salon").child(newOrder.getUser_id()).child(keyOrder).setValue(newOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                sendMessage(newOrder, holder);

                databaseReference.child("token_user").child(newOrder.getUser_id()).child("token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            try {
                                JSONArray token = new JSONArray();
                                token.put(task.getResult().getValue().toString());

                                JSONObject data = new JSONObject();
                                data.put("uid_receiver", newOrder.getUser_id());
                                data.put("receiver", "Customer");
                                data.put("title", "Pesanan diterima");
                                data.put("message", "Haloo kak, pesanan kakak diterima nih, yuk cek");


                                JSONObject body = new JSONObject();
                                body.put(Constant.REMOTE_MSG_DATA, data);
                                body.put(Constant.REMOTE_MSG_REGISTRATION_IDS, token);

                                Utilities.sendNotification(body.toString(), mContext);
                            }catch (Exception e){
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, String.valueOf(e), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void confirmPayOrder(ModelOrder modelOrder, String key) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("order_salon").child(modelOrder.getUser_id()).child(key).setValue(modelOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(mContext, "Pembayaran dikonfirmasi", Toast.LENGTH_SHORT).show();

                    reference.child("token_user").child(modelOrder.getUser_id()).child("token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                try {
                                    JSONArray token = new JSONArray();
                                    token.put(task.getResult().getValue().toString());

                                    JSONObject data = new JSONObject();
                                    data.put("uid_receiver", modelOrder.getUser_id());
                                    data.put("receiver", "Customer");
                                    data.put("title", "Pembayaran dikonfirmasi");
                                    data.put("message", "Haloo kak, pembayaran kakak sudah kami konfirmasi, silahkan datang pada hari yang telah ditentukan");


                                    JSONObject body = new JSONObject();
                                    body.put(Constant.REMOTE_MSG_DATA, data);
                                    body.put(Constant.REMOTE_MSG_REGISTRATION_IDS, token);

                                    Utilities.sendNotification(body.toString(), mContext);
                                }catch (Exception e){
                                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                }else {
                    Toast.makeText(mContext, "Pembayaran gagal dikonfirmasi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void orderFinish(ModelOrder modelOrder, String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("history_order").child(modelOrder.getUser_id()).child(key).setValue(modelOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    databaseReference.child("order_salon").child(modelOrder.getUser_id()).child(key).removeValue();
                }else {
                    Toast.makeText(mContext, "Gagal menyelesaikan order", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(ModelOrder modelOrder, MyViewHolder holder){
        long tsLong = System.currentTimeMillis()/1000;
        String timeStamp = Long.toString(tsLong);

        ModelMessage modelChat;

        modelChat = new ModelMessage(
                "Admin",
                "Hai kak terimakasih telah memesan\nPesanan paket : "
                        +holder.namePackageTv.getText().toString()+
                        "\nAtas nama : "+holder.nameOrderTv.getText().toString()+
                        "\ntelah kami konfirmasi, silahkan lakukan pembyaran DP.",
                timeStamp,
                false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("message").child(modelOrder.getUser_id()).push().setValue(modelChat);

    }

    private void setDataPackage(String keyPackage, MyViewHolder holder){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("package_salon").child(keyPackage).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    ModelPackage modelPackage = task.getResult().getValue(ModelPackage.class);
                    holder.namePackageTv.setText(modelPackage.getPackage_name());
                    holder.pricePackageTv.setText("Rp. "+formatCurrenryRp(modelPackage.getPrice()));
                    Picasso.get().load(modelPackage.getUrl_photo_package()).into(holder.thumbsImage);

                }else {
                    Toast.makeText(mContext, "Kesalahan pengambilan data", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
