package com.ark.bookedapps.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityUserEditBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UserEdit extends AppCompatActivity {

    private ActivityUserEditBinding binding;
    private String username, email, number, role, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_dropdown, getResources().getStringArray(R.array.user_role));
        binding.userRoleAc.setAdapter(adapter);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        number = getIntent().getStringExtra("number");
        role = getIntent().getStringExtra("role");
        key = getIntent().getStringExtra("key");

        binding.nameUserTiEdit.setText(username);
        binding.emailUserTiEdit.setText(email);
        binding.numberUserTiEdit.setText(number);

        binding.backBtn.setOnClickListener(view -> {
            updateUI(ManageUser.class);
            finish();
        });


        binding.saveChange.setOnClickListener(view -> {
            if (binding.nameUserTiEdit.getText().toString().isEmpty()){
                binding.nameUserTiEdit.setError("Nama harus diisi");
            }else if (binding.emailUserTiEdit.getText().toString().isEmpty()){
                binding.emailUserTiEdit.setError("Email harus diisi");
            }else if (binding.numberUserTiEdit.getText().toString().isEmpty()){
                binding.numberUserTiEdit.setError("Nomor harus diisi");
            }else if (binding.userRoleAc.getText().toString().isEmpty()){
                binding.userRoleAc.setError("Role Pengguna harus diisi");
            }else {
                saveChanges();
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.saveChange.setEnabled(false);
            }
        });
    }

    private void saveChanges(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String role_new = binding.userRoleAc.getText().toString();

        ModelUser modelUser = new ModelUser(email, username, number, role_new);

        databaseReference.child("users").child(key).setValue(modelUser).addOnSuccessListener(unused -> {
            updateUI(ManageUser.class);
            binding.progressCircular.setVisibility(View.INVISIBLE);
            binding.saveChange.setEnabled(true);

            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(UserEdit.this, "Gagal mengubah data", Toast.LENGTH_SHORT).show();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            binding.saveChange.setEnabled(true);
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(UserEdit.this, activity));
    }
}