package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.Utility.BrokenConnection;
import com.ark.bookedapps.Utility.Utilities;
import com.ark.bookedapps.databinding.ActivityProfileUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileUser extends AppCompatActivity {

    private ActivityProfileUserBinding binding;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        uid = getIntent().getStringExtra("uid");
        if (!Utilities.checkConnection(this)){
            updateUI(BrokenConnection.class);
            finish();
        }else {
            setDataProfile(uid);
        }


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(HomeApp.class);
                finish();
            }
        });

        binding.saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.nameUserTiEdit.getText().toString().isEmpty()){
                    binding.nameUserTiEdit.setError("Tidak boleh kosong");
                }else if (binding.numberUserTiEdit.getText().toString().isEmpty()){
                    binding.numberUserTiEdit.setError("Tidak boleh kosong");
                }else {
                    changeProfile();
                }
            }
        });
    }

    private void setDataProfile(String uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                    binding.nameUserTiEdit.setText(modelUser.getUsername());
                    binding.numberUserTiEdit.setText(modelUser.getNo_telp());
                }
            }
        });
    }

    private void changeProfile(){
        String newUsername = binding.nameUserTiEdit.getText().toString();
        String newNumber = binding.numberUserTiEdit.getText().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        ModelUser modelUser = new ModelUser(fUser.getEmail(), newUsername, newNumber,"Customer");

        databaseReference.child("users").child(fUser.getUid()).setValue(modelUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ProfileUser.this, "Berhasil mengubah data", Toast.LENGTH_SHORT).show();
                updateUI(HomeApp.class);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(ProfileUser.this, activity));
    }
}