package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ark.bookedapps.Model.ModelRekening;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityRekeningEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RekeningEdit extends AppCompatActivity {

    private ActivityRekeningEditBinding binding;

    private String rekBank, noRekBank, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRekeningEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(ManageRekening.class);
                finish();
            }
        });

        binding.saveRek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.nameRekBankTiEdit.getText().toString().isEmpty()){
                    binding.nameRekBankTiEdit.setError("Tidak boleh kosong");
                }else if (binding.numberRekBankTiEdit.getText().toString().isEmpty()){
                    binding.numberRekBankTiEdit.setError("Tidak boleh kosong");
                }else {
                    saveChangeRek();
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    binding.saveRek.setEnabled(false);
                }
            }
        });

        rekBank = getIntent().getStringExtra("rek_bank");
        noRekBank = getIntent().getStringExtra("number_rek");
        key = getIntent().getStringExtra("key");

        setDataEditRek();
    }

    private void saveChangeRek() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String rekBankNew = binding.nameRekBankTiEdit.getText().toString();
        String numberRekNew = binding.numberRekBankTiEdit.getText().toString();

        ModelRekening modelRekening = new ModelRekening(rekBankNew, numberRekNew);
        databaseReference.child("rekening_salon").child(key).setValue(modelRekening).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                binding.progressCircular.setVisibility(View.INVISIBLE);
                binding.saveRek.setEnabled(true);
                updateUI(ManageRekening.class);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RekeningEdit.this, "Gagal mengubah data rekening", Toast.LENGTH_SHORT).show();
                binding.progressCircular.setVisibility(View.INVISIBLE);
                binding.saveRek.setEnabled(true);
                updateUI(ManageRekening.class);
                finish();
            }
        });
    }

    private void setDataEditRek() {
        binding.nameRekBankTiEdit.setText(rekBank);
        binding.numberRekBankTiEdit.setText(noRekBank);
    }

    private void updateUI(Class activity){
        startActivity(new Intent(RekeningEdit.this, activity));
    }
}