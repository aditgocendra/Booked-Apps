package com.ark.bookedapps.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelWallet;
import com.ark.bookedapps.R;
import com.ark.bookedapps.View.ManageWalletPayment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterManageWalletPayment extends RecyclerView.Adapter<AdapterManageWalletPayment.ManageWallet> {

    private List<ModelWallet> listWallet;
    private Context mContext;
    private BottomSheetDialog bottomSheetDialog;

    public AdapterManageWalletPayment(List<ModelWallet> listWallet, Context mContext) {
        this.listWallet = listWallet;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ManageWallet onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_card_e_wallet, parent, false);
        return new ManageWallet(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageWallet holder, int position) {
        ModelWallet modelWallet = listWallet.get(position);
        holder.nameWallet.setText(modelWallet.getName_wallet());
        holder.numberWallet.setText(modelWallet.getNumber_wallet());

        holder.cardEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(mContext);
                setBottomDialogEdit(modelWallet.getKey(), modelWallet.getName_wallet(), modelWallet.getNumber_wallet());
                bottomSheetDialog.show();
            }
        });

        holder.cardDelete.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle("Hapus E Wallet");
            builder.setMessage("Apakah anda yakin menghapus e wallet ini ?");
            builder.setPositiveButton("Lanjutkan", (dialog, which) -> deleteWallet(modelWallet.getKey()));
            builder.setNegativeButton("Batal", (dialog, which) -> {
                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return listWallet.size();
    }

    public static class ManageWallet extends RecyclerView.ViewHolder {
        TextView nameWallet, numberWallet;
        CardView cardEdit, cardDelete;
        public ManageWallet(@NonNull View itemView) {
            super(itemView);

            nameWallet = itemView.findViewById(R.id.name_wallet_tv);
            numberWallet = itemView.findViewById(R.id.wallet_number_tv);
            cardEdit = itemView.findViewById(R.id.card_edit_wallet);
            cardDelete = itemView.findViewById(R.id.card_delete_wallet);

        }
    }

    private void deleteWallet(String key){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("e_wallet").child(key).removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(mContext, "Berhasil menghapus e wallet", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setBottomDialogEdit(String key, String nameOldWallet, String numberOldWallet){
        LayoutInflater li = LayoutInflater.from(mContext);
        View viewBottomDialog = li.inflate(R.layout.layout_dialog_add_e_wallet, null, false);
        TextInputEditText nameWallet = viewBottomDialog.findViewById(R.id.name_e_wallet);
        TextInputEditText numberWallet = viewBottomDialog.findViewById(R.id.number_e_wallet);
        TextView headerWallet = viewBottomDialog.findViewById(R.id.header_wallet);
        Button finishBtn = viewBottomDialog.findViewById(R.id.finish_btn);

        headerWallet.setText("Ubah E Wallet");
        nameWallet.setText(nameOldWallet);
        numberWallet.setText(numberOldWallet);
        finishBtn.setText("Ubah");


        finishBtn.setOnClickListener(view -> {
            String name_wallet = nameWallet.getText().toString();
            String number_wallet = numberWallet.getText().toString();

            if (name_wallet.isEmpty()){
                Toast.makeText(mContext, "Nama e wallet kosong", Toast.LENGTH_SHORT).show();
            }else if (number_wallet.isEmpty()){
                Toast.makeText(mContext, "Nomor e wallet kosong", Toast.LENGTH_SHORT).show();
            }else {

                updateWallet(name_wallet, number_wallet, key);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(viewBottomDialog);
    }

    private void updateWallet(String nameWallet, String numberWallet, String key){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        ModelWallet modelWallet = new ModelWallet(nameWallet, numberWallet);
        reference.child("e_wallet").child(key).setValue(modelWallet)
                .addOnSuccessListener(unused -> Toast.makeText(mContext, "Berhasil mengubah e wallet", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(mContext, "Gagal mengubah e wallet", Toast.LENGTH_SHORT).show());
    }
}
