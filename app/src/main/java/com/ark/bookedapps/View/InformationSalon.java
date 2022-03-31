package com.ark.bookedapps.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import com.ark.bookedapps.Model.ModelInformation;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityInformationSalonBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class InformationSalon extends AppCompatActivity {

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ActivityInformationSalonBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformationSalonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        setDataInformation();

        ImageView backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(view -> finish());
    }

    private void setDataInformation(){
        databaseReference.child("information_salon").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelInformation modelInformation = task.getResult().getValue(ModelInformation.class);
                assert modelInformation != null;
                binding.nameSalonTv.setText(modelInformation.getSalon_name());
                binding.locationSalonTv.setText(modelInformation.getSalon_location());
                binding.aboutSalonTv.setText(modelInformation.getSalon_about());
                binding.orderedTv.setText(modelInformation.getOrdered());
                binding.nameOwnerTv.setText(modelInformation.getOwner_salon());
                binding.emailOwnerTv.setText(modelInformation.getEmail_owner_salon());

                Picasso.get().load(modelInformation.getUrl_owner_image()).resize(50, 50).centerCrop().into(binding.photoOwnerIv);

            }else {
                Toast.makeText(InformationSalon.this, "Gagal mengunduh data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(InformationSalon.this, activity));
    }
}