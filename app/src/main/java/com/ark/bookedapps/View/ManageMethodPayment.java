package com.ark.bookedapps.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ark.bookedapps.databinding.ActivityManageMethodPaymentBinding;
import com.ark.bookedapps.databinding.ActivityMethodPaymentBinding;

public class ManageMethodPayment extends AppCompatActivity {

    private ActivityManageMethodPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageMethodPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backBtn.setOnClickListener(view -> finish());
        binding.transferBank.setOnClickListener(view -> updateUI(ManageRekening.class));
        binding.eWallet.setOnClickListener(view -> updateUI(ManageWalletPayment.class));

    }

    private void updateUI(Class activity){
        startActivity(new Intent(ManageMethodPayment.this, activity));
    }
}