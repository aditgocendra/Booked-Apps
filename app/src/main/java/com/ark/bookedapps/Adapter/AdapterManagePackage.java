package com.ark.bookedapps.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.R;
import com.ark.bookedapps.View.PackageEdit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterManagePackage extends RecyclerView.Adapter<AdapterManagePackage.MyViewHolder> {

    private List<ModelPackage> listPackages;
    private Context mContext;



    public AdapterManagePackage(List<ModelPackage> listPackages, Context mContext){
        this.listPackages = listPackages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View viewItem = layoutInflater.inflate(R.layout.layout_manage_package, parent, false);
        return new MyViewHolder(viewItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ModelPackage modelPackage = listPackages.get(position);
        holder.packageNameTv.setText(modelPackage.getPackage_name());
        holder.pricePackageTv.setText("Rp. "+formatCurrenryRp(modelPackage.getPrice()));

        Picasso.get().load(modelPackage.getUrl_photo_package()).into(holder.thumbsPackage);

        holder.cardDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle("Hapus Paket");
                builder.setMessage("Apakah anda yakin menghapus paket ini ?");
                builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePackage(modelPackage.getKey(), modelPackage.getUrl_photo_package());

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

        holder.cardEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PackageEdit.class);
                intent.putExtra("key", modelPackage.getKey());
                intent.putExtra("urlPhoto", modelPackage.getUrl_photo_package());
                intent.putExtra("package_name", modelPackage.getPackage_name());
                intent.putExtra("price", modelPackage.getPrice());
                intent.putExtra("detail", modelPackage.getDetail());

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listPackages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardEdit, cardDelete;
        ImageView thumbsPackage;
        TextView packageNameTv, pricePackageTv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEdit = itemView.findViewById(R.id.card_edit_package);
            cardDelete = itemView.findViewById(R.id.card_delete_package);
            thumbsPackage = itemView.findViewById(R.id.thumbs_image_package);
            packageNameTv = itemView.findViewById(R.id.name_package_tv);
            pricePackageTv = itemView.findViewById(R.id.price_tv);


        }

    }

    private void deletePackage(String key, String urlPhoto){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        String name_photo = storage.getReferenceFromUrl(urlPhoto).getName();
        StorageReference deleteRef = storage.getReference("package_image/"+name_photo);

        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                databaseReference.child("package_salon").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "Berhasil menghapus data", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Gagal menghapus photo lama owner", Toast.LENGTH_SHORT).show();
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
