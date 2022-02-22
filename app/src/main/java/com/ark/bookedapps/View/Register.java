package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String email = binding.emailUserRegister.getText().toString();
               String pass = binding.passUserRegister.getText().toString();
               String re_pass = binding.rePassUserRegister.getText().toString();

               if(email.isEmpty()){
                   binding.emailUserRegister.setError("Email tidak boleh kosong");
               }else if(!ValidateEmail(email)){
                   Toast.makeText(Register.this, "Format email salah", Toast.LENGTH_SHORT).show();
               }
               else if (pass.isEmpty()){
                   binding.passUserRegister.setError("Password tidak boleh kosong");
               }else if(re_pass.isEmpty()){
                   binding.rePassUserRegister.setError("Konfirmasi password tidak boleh kosong");
               }else if(!pass.equals(re_pass)) {
                   Toast.makeText(Register.this, "Password tidak sama dengan konfirmasi pass", Toast.LENGTH_SHORT).show();
               }else {
                   registerUser(email, pass);
               }

            }
        });

        TextView loginRedirect = findViewById(R.id.redirect_login);
        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }

    private void registerUser(String email, String pass) {
        binding.registerUserBtn.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                insertDataUser(user);
                                updateUI(Login.class);

                                binding.registerUserBtn.setEnabled(true);
                            }else{
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                binding.registerUserBtn.setEnabled(true);
                            }
                        }
                    });
                }else{
                    Toast.makeText(Register.this, "Authentication gagal", Toast.LENGTH_SHORT).show();
                    binding.registerUserBtn.setEnabled(true);
                }
            }
        });
    }

    private void updateUI(Class activity_class){
        startActivity(new Intent(Register.this, activity_class));
        finish();
    }

    public static boolean ValidateEmail(String email){
        boolean validate;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";

        if (email.matches(emailPattern) || email.matches(emailPattern2) && email.length() > 0)
        {
            validate = true;
        }else{
            validate = false;
        }

        return validate;
    }

    private void insertDataUser(FirebaseUser user){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        ModelUser userModel = new ModelUser(user.getEmail(), usernameFromEmail(user.getEmail()), "Belum diisi", "Customer");

        database.child("users").child(user.getUid()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Register.this, "Daftar berhasil silahkan lakukan verifikasi melalui email anda.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Pendaftaran gagal silahkan hubungi admin.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}