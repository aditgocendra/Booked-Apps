package com.ark.bookedapps.Utility;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ark.bookedapps.databinding.ActivityBrokenConnectionBinding;

public class BrokenConnection extends AppCompatActivity {


    private ActivityBrokenConnectionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrokenConnectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.closeAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }
}