package com.ark.bookedapps.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelRekening;
import com.ark.bookedapps.R;
import com.ark.bookedapps.View.RekeningEdit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterManageRekening extends RecyclerView.Adapter<AdapterManageRekening.MyViewHolder> {

    private List<ModelRekening> listRekening;
    private Context mContext;

    public AdapterManageRekening(List<ModelRekening> listRekening, Context mContext){
        this.listRekening = listRekening;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdapterManageRekening.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View viewItem = layoutInflater.inflate(R.layout.layout_rekening_salon, parent, false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterManageRekening.MyViewHolder holder, int position) {
        ModelRekening modelRekening = listRekening.get(position);

        holder.rekeningBankTv.setText("Rekening Bank : "+modelRekening.getRekBank());
        holder.noRekBankTv.setText("No Rekening : "+modelRekening.getNoRek());

        holder.cardDeleteRek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle("Hapus Paket");
                builder.setMessage("Apakah anda yakin menghapus paket ini ?");
                builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRekBank(modelRekening.getKey());
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

        holder.cardEditRek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RekeningEdit.class);
                intent.putExtra("rek_bank", modelRekening.getRekBank());
                intent.putExtra("number_rek", modelRekening.getNoRek());
                intent.putExtra("key", modelRekening.getKey());
                mContext.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return listRekening.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView rekeningBankTv, noRekBankTv;
        CardView cardEditRek, cardDeleteRek;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rekeningBankTv = itemView.findViewById(R.id.rek_bank_tv);
            noRekBankTv = itemView.findViewById(R.id.no_rek_bank_tv);
            cardEditRek = itemView.findViewById(R.id.card_edit_rekening);
            cardDeleteRek = itemView.findViewById(R.id.card_delete_rekening);
        }
    }

    private void deleteRekBank(String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("rekening_salon").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(mContext, "Data rekening berhasil dihapus", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext, "Data gagal dihapus", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
