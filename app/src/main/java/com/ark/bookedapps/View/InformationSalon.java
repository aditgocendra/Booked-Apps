package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ark.bookedapps.Model.ModelInformation;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityInformationSalonBinding;
import com.ark.bookedapps.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class InformationSalon extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ActivityInformationSalonBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformationSalonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        setDataInformation();

        ImageView backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(HomeApp.class);
                finish();
            }
        });
    }

    private void setDataInformation(){
        databaseReference.child("information_salon").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    ModelInformation modelInformation = task.getResult().getValue(ModelInformation.class);
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
            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(InformationSalon.this, activity));
    }
}