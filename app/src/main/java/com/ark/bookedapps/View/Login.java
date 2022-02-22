package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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


        binding.redirectRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(Register.class);
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        binding.redirectForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(ForgotPassword.class);
                finish();
            }
        });
    }

    private void Login(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    updateUI(HomeApp.class);
                    binding.loginBtn.setEnabled(true);
                    finish();
                }else {
                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.loginBtn.setEnabled(true);
                }
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