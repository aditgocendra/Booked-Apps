package com.ark.bookedapps.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.R;
import com.ark.bookedapps.View.PackageDetail;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterHomeApp extends RecyclerView.Adapter<AdapterHomeApp.MyViewHolder> {
    private List<ModelPackage> lisPackages;
    private Context mContext;

    public AdapterHomeApp(List<ModelPackage> lisPackages, Context mContext){
        this.lisPackages = lisPackages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View viewItem = layoutInflater.inflate(R.layout.layout_item_home_app, parent, false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHomeApp.MyViewHolder holder, int position) {
        ModelPackage modelPackage = lisPackages.get(position);

        Picasso.get().load(modelPackage.getUrl_photo_package()).into(holder.thumbsPackage);

        holder.textNamePackage.setText(modelPackage.getPackage_name());
        holder.textPricePackage.setText("Rp. "+formatCurrenryRp(modelPackage.getPrice()));

        holder.cardDetailPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PackageDetail.class);
                intent.putExtra("name_package", modelPackage.getPackage_name());
                intent.putExtra("price", modelPackage.getPrice());
                intent.putExtra("url_thumbs", modelPackage.getUrl_photo_package());
                intent.putExtra("detail", modelPackage.getDetail());
                intent.putExtra("key", modelPackage.getKey());
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return lisPackages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardDetailPackage;
        ImageView thumbsPackage;
        TextView textNamePackage, textPricePackage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardDetailPackage = itemView.findViewById(R.id.card_package_detail_home);
            thumbsPackage = itemView.findViewById(R.id.thumbs_image_pakage_home);
            textNamePackage = itemView.findViewById(R.id.name_package_tv_home);
            textPricePackage = itemView.findViewById(R.id.price_package_tv_home);
        }
    }

    private String formatCurrenryRp(String number){

        double decimalNumber = Double.parseDouble(number);

        Locale localeID = new Locale("in", "ID");
        NumberFormat rupiahFormat = NumberFormat.getNumberInstance(localeID);
        return rupiahFormat.format(decimalNumber);
    }
}
