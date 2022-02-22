package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ark.bookedapps.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        listenerClick();

    }

    private void listenerClick(){
        binding.resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailUserReset.getText().toString();

                if (email.isEmpty()){
                    binding.emailUserReset.setError("Email masih kosong");
                }else if (!ValidateEmail(email)){
                    binding.emailUserReset.setError("Format email tidak valid");
                }else {
                    resetPass(email);
                    binding.resetPassBtn.setEnabled(false);
                }
            }
        });

        binding.redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(Login.class);
                finish();
            }
        });
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

    private void updateUI(Class activity_class){
        startActivity(new Intent(ForgotPassword.this, activity_class));
        finish();
    }



    private void resetPass(String email){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Kami telah mengirimkan bantuan lewat email anda", Toast.LENGTH_SHORT).show();
                    binding.resetPassBtn.setEnabled(true);
                }else {
                    Toast.makeText(ForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.resetPassBtn.setEnabled(true);
                }
            }
        });
    }
}