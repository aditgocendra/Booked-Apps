package com.ark.bookedapps.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.R;
import com.ark.bookedapps.View.MethodPayment;
import com.ark.bookedapps.View.PaymentCard;
import com.ark.bookedapps.View.RetryOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.Console;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterMyOrder extends RecyclerView.Adapter<AdapterMyOrder.MyViewHolder> {

    private List<ModelOrder> listOrder;
    private List<ModelPackage> listPackage;
    private Context mContext;

    public AdapterMyOrder(List<ModelOrder> listOrder, List<ModelPackage> listPackage, Context mContext){
        this.listOrder = listOrder;
        this.listPackage = listPackage;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View viewItem = layoutInflater.inflate(R.layout.layout_my_order, parent, false);
        return new MyViewHolder(viewItem);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterMyOrder.MyViewHolder holder, int position) {
        ModelOrder modelOrder = listOrder.get(position);
        ModelPackage modelPackage = listPackage.get(position);

        holder.namePackageTv.setText(modelPackage.getPackage_name());
        holder.pricePackageTv.setText("Rp. "+formatCurrenryRp(modelPackage.getPrice()));
        holder.statusTv.setText(modelOrder.getStatus());
        holder.dateOrderTv.setText(modelOrder.getDate_order());
        holder.timeOrderTv.setText(modelOrder.getTime_order());
        holder.payTv.setText(modelOrder.getPay_order());
        Picasso.get().load(modelPackage.getUrl_photo_package()).into(holder.thumbsImage);

        if (modelOrder.getStatus().equals("Dibatalkan")){
            holder.statusTv.setTextColor(Color.RED);
            holder.alertCancel.setVisibility(View.VISIBLE);
            holder.btnPayDP.setVisibility(View.GONE);
            holder.btnRetry.setVisibility(View.VISIBLE);
        }
        if (modelOrder.getStatus().equals("(Disetejui) Menunggu Pembayaran")){
            holder.btnPayDP.setEnabled(true);
        }

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

        holder.btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RetryOrder.class);
                intent.putExtra("thumbs_image", modelPackage.getUrl_photo_package());
                intent.putExtra("name_package", modelPackage.getPackage_name());
                intent.putExtra("price", "Rp. "+formatCurrenryRp(modelPackage.getPrice()));
                intent.putExtra("date_order", modelOrder.getDate_order());
                intent.putExtra("time_order", modelOrder.getTime_order());
                intent.putExtra("key_order", modelOrder.getKey());
                intent.putExtra("key_package", modelOrder.getKey_package());
                mContext.startActivity(intent);
            }
        });

        holder.btnPayDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, MethodPayment.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView namePackageTv, pricePackageTv, statusTv, payTv, dateOrderTv, timeOrderTv;
        Button btnPayDP, btnRetry;
        ImageView thumbsImage, alertCancel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            namePackageTv = itemView.findViewById(R.id.name_package_tv_order);
            pricePackageTv = itemView.findViewById(R.id.price_package_tv_order);
            statusTv = itemView.findViewById(R.id.status_tv);
            payTv = itemView.findViewById(R.id.pembayaran_tv);
            dateOrderTv = itemView.findViewById(R.id.date_tv_order);
            timeOrderTv = itemView.findViewById(R.id.time_tv_order);
            btnPayDP = itemView.findViewById(R.id.pembayaran_dp_btn);
            btnRetry = itemView.findViewById(R.id.pengajuan_ulang);
            thumbsImage = itemView.findViewById(R.id.thumbs_image_pakage_order);
            alertCancel = itemView.findViewById(R.id.alert_cancel);

        }
    }

    private String formatCurrenryRp(String number){
        double decimalNumber = Double.parseDouble(number);

        Locale localeID = new Locale("in", "ID");
        NumberFormat rupiahFormat = NumberFormat.getNumberInstance(localeID);
        return rupiahFormat.format(decimalNumber);
    }


}
