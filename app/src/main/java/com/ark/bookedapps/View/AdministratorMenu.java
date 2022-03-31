package com.ark.bookedapps.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ark.bookedapps.databinding.ActivityAdministratorMenuBinding;

public class AdministratorMenu extends AppCompatActivity {

    private ActivityAdministratorMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministratorMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.cardManageInformation.setOnClickListener(view -> updateUI(ManageInformation.class));
        binding.cardPackageSalon.setOnClickListener(view -> updateUI(ManagePackage.class));
        binding.cardMethodPay.setOnClickListener(view -> updateUI(ManageMethodPayment.class));
        binding.cardHistoryOrder.setOnClickListener(view -> updateUI(HistoryOrder.class));
        binding.backBtn.setOnClickListener(view -> finish());


    }

    private void updateUI(Class activity){
        startActivity(new Intent(AdministratorMenu.this, activity));
    }
}