package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ark.bookedapps.Model.ModelRekening;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityRekeningAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RekeningAdd extends AppCompatActivity {

    private ActivityRekeningAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRekeningAddBinding.inflate(getLayoutInflater());
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
                if (binding.nameRekBankTi.getText().toString().isEmpty()){
                    binding.nameRekBankTi.setError("Rekening Bank tidak boleh kosong");
                }else if (binding.numberRekBankTi.getText().toString().isEmpty()){
                    binding.numberRekBankTi.setError("Nomor rekening bank tidak boleh kosong");
                }else {
                    saveRekening();
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    binding.saveRek.setEnabled(false);
                }
            }
        });
    }

    private void saveRekening() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String rekBank = binding.nameRekBankTi.getText().toString();
        String numberRek = binding.numberRekBankTi.getText().toString();

        ModelRekening modelRekening = new ModelRekening(rekBank, numberRek);

        databaseReference.child("rekening_salon").push().setValue(modelRekening).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateUI(ManageRekening.class);
                binding.progressCircular.setVisibility(View.INVISIBLE);
                binding.saveRek.setEnabled(true);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RekeningAdd.this, "Gagal menambahkan rekening", Toast.LENGTH_SHORT).show();
                binding.progressCircular.setVisibility(View.INVISIBLE);
                binding.saveRek.setEnabled(true);
                updateUI(ManageRekening.class);
                finish();
            }
        });

    }

    private void updateUI(Class activity){
        startActivity(new Intent(RekeningAdd.this, activity));
    }
}