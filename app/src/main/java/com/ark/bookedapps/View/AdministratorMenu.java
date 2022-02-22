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

        binding.cardManageInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(ManageInformation.class);
            }
        });

        binding.cardPackageSalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(ManagePackage.class);
            }
        });



        binding.cardRekPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(ManageRekening.class);
            }
        });

        binding.cardHistoryOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(HistoryOrder.class);
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(HomeApp.class);
                finish();
            }
        });


    }

    private void updateUI(Class activity){
        startActivity(new Intent(AdministratorMenu.this, activity));
    }
}