package com.ark.bookedapps.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ark.bookedapps.databinding.ActivityMethodPaymentBinding;

public class MethodPayment extends AppCompatActivity {

    private ActivityMethodPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMethodPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backBtn.setOnClickListener(view -> finish());
        binding.transferBank.setOnClickListener(view -> updateUI(PaymentCard.class));
        binding.eWallet.setOnClickListener(view -> updateUI(PaymentWallet.class));



    }

    private void updateUI(Class activity){
        startActivity(new Intent(MethodPayment.this, activity));
    }
}