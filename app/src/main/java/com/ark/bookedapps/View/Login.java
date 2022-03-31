package com.ark.bookedapps.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.ark.bookedapps.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.redirectRegister.setOnClickListener(view -> updateUI(Register.class));

        binding.loginBtn.setOnClickListener(view -> {
            String email = binding.emailUser.getText().toString();
            String pass = binding.passUser.getText().toString();

            if (email.isEmpty()){
                binding.emailUser.setError("Email tidak boleh kosong");
            }else if(!ValidateEmail(email)){
                Toast.makeText(Login.this, "Format email tidak cocok", Toast.LENGTH_SHORT).show();
            } else if(pass.isEmpty()){
                binding.passUser.setError("Password tidak boleh kosong");
            }else {
                Login(email, pass);
                binding.loginBtn.setEnabled(false);
            }
        });

        binding.redirectForgotPass.setOnClickListener(view -> {
            updateUI(ForgotPassword.class);
            finish();
        });
    }

    private void Login(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (task.isSuccessful()){
                assert user != null;
                if (user.isEmailVerified()){
                    binding.loginBtn.setEnabled(true);
                    updateUI(HomeApp.class);
                    finish();
                }else {
                    binding.loginBtn.setEnabled(true);
                    Toast.makeText(Login.this, "Email anda belum diverifikasi", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                binding.loginBtn.setEnabled(true);
            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(Login.this, activity));
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


}