package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ark.bookedapps.Adapter.AdapterManageWalletPayment;
import com.ark.bookedapps.Model.ModelWallet;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityManageWalletPaymentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageWalletPayment extends AppCompatActivity {

    private ActivityManageWalletPaymentBinding binding;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private AdapterManageWalletPayment adapterManageWalletPayment;
    private List<ModelWallet> listWallet;

    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageWalletPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.addFloatBtn.setOnClickListener(view -> {
            bottomSheetDialog = new BottomSheetDialog(this);
            setBottomDialogAdd();
            bottomSheetDialog.show();
        });

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recyclerEWallet.setLayoutManager(mLayout);
        binding.recyclerEWallet.setItemAnimator(new DefaultItemAnimator());

        setDataWallet();


    }

    private void setDataWallet(){
        reference.child("e_wallet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listWallet = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelWallet modelWallet = dataSnapshot.getValue(ModelWallet.class);
                    if (modelWallet != null){
                        modelWallet.setKey(dataSnapshot.getKey());
                        listWallet.add(modelWallet);
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (listWallet.size() != 0){
                        adapterManageWalletPayment = new AdapterManageWalletPayment(listWallet, ManageWalletPayment.this);
                        binding.recyclerEWallet.setAdapter(adapterManageWalletPayment);
                    }else {
                        Toast.makeText(ManageWalletPayment.this, "Belum ada data e wallet yang ditambahkan", Toast.LENGTH_SHORT).show();
                    }

                }, 300);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageWalletPayment.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBottomDialogAdd(){
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_add_e_wallet, null, false);
        TextInputEditText nameWallet = viewBottomDialog.findViewById(R.id.name_e_wallet);
        TextInputEditText numberWallet = viewBottomDialog.findViewById(R.id.number_e_wallet);
        TextView headerWallet = viewBottomDialog.findViewById(R.id.header_wallet);
        Button finishBtn = viewBottomDialog.findViewById(R.id.finish_btn);

        headerWallet.setText("Tambah E Wallet");
        finishBtn.setText("Tambah");

        finishBtn.setOnClickListener(view -> {
            String name_wallet = nameWallet.getText().toString();
            String number_wallet = numberWallet.getText().toString();

            if (name_wallet.isEmpty()){
                Toast.makeText(this, "Nama e wallet kosong", Toast.LENGTH_SHORT).show();
            }else if (number_wallet.isEmpty()){
                Toast.makeText(this, "Nomor e wallet kosong", Toast.LENGTH_SHORT).show();
            }else {
                saveWallet(name_wallet, number_wallet);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(viewBottomDialog);
    }




    private void saveWallet(String nameWallet, String numberWallet){
        ModelWallet modelWallet = new ModelWallet(nameWallet, numberWallet);
        reference.child("e_wallet").push().setValue(modelWallet)
                .addOnSuccessListener(unused -> Toast.makeText(ManageWalletPayment.this, "Berhasil menambahkan e wallet", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ManageWalletPayment.this, "Gagal menambahkan e wallet", Toast.LENGTH_SHORT).show());
    }



}