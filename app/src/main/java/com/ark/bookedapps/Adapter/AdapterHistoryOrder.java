package com.ark.bookedapps.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterHistoryOrder extends RecyclerView.Adapter<AdapterHistoryOrder.MyViewHolder> {

    private List<ModelOrder> listOrder;
    private Context mContext;

    public AdapterHistoryOrder(List<ModelOrder> listOrder, Context mContext){
        this.listOrder = listOrder;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_history_order, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistoryOrder.MyViewHolder holder, int position) {
        ModelOrder modelOrder = listOrder.get(position);

        holder.statusOrderTv.setText(modelOrder.getStatus());
        holder.dateOrderTv.setText(modelOrder.getDate_order());
        holder.timeOrderTv.setText(modelOrder.getTime_order());

        // set package data
        setDataPackage(modelOrder.getKey_package(), holder);

        // set username order
        setUserOrderData(modelOrder.getUser_id(), holder);

        holder.cardDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle("Hapus History");
                builder.setMessage("Apakah anda yakin menghapus history ini ?");
                builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteHistory(modelOrder.getKey(), modelOrder.getUser_id());

                    }
                });

                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView usernameOrderTv, statusOrderTv, packageNameTv, priceTv, dateOrderTv, timeOrderTv, payTv;
        ImageView thumbsIv;
        CardView cardDeleteHistory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameOrderTv = itemView.findViewById(R.id.username_order);
            statusOrderTv = itemView.findViewById(R.id.status_tv_history);
            packageNameTv = itemView.findViewById(R.id.name_package_tv_history);
            priceTv = itemView.findViewById(R.id.price_package_tv_history);
            dateOrderTv = itemView.findViewById(R.id.date_tv_history);
            timeOrderTv = itemView.findViewById(R.id.time_tv_history);
            payTv = itemView.findViewById(R.id.pay_tv_history);
            thumbsIv = itemView.findViewById(R.id.thumbs_image_pakage_history);
            cardDeleteHistory = itemView.findViewById(R.id.card_delete_history);
        }
    }


    private void setUserOrderData(String user_id, MyViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(user_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                holder.usernameOrderTv.setText(modelUser.getUsername());
            }
        });

    }

    private void setDataPackage(String keyPackage, MyViewHolder holder){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("package_salon").child(keyPackage).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    ModelPackage modelPackage = task.getResult().getValue(ModelPackage.class);
                    holder.packageNameTv.setText(modelPackage.getPackage_name());
                    holder.priceTv.setText("Rp. "+formatCurrenryRp(modelPackage.getPrice()));
                    Picasso.get().load(modelPackage.getUrl_photo_package()).into(holder.thumbsIv);

                }else {
                    Toast.makeText(mContext, "Kesalahan pengambilan data", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteHistory(String key, String uid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("history_order").child(uid).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(mContext, "History berhasil dihapus", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String formatCurrenryRp(String number){
        double decimalNumber = Double.parseDouble(number);

        Locale localeID = new Locale("in", "ID");
        NumberFormat rupiahFormat = NumberFormat.getNumberInstance(localeID);
        return rupiahFormat.format(decimalNumber);
    }

}
