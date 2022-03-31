package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ark.bookedapps.Adapter.AdapterManageRekening;
import com.ark.bookedapps.Model.ModelRekening;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityManageRekeningBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageRekening extends AppCompatActivity {

    private ActivityManageRekeningBinding binding;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<ModelRekening> listRekening;
    private AdapterManageRekening adapterManageRekening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageRekeningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backBtn.setOnClickListener(view -> finish());

        binding.addFloatBtn.setOnClickListener(view -> updateUI(RekeningAdd.class));

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recycleManageRekening.setLayoutManager(mLayout);
        binding.recycleManageRekening.setItemAnimator(new DefaultItemAnimator());

        setDataRekening();
    }

    private void setDataRekening() {
        databaseReference.child("rekening_salon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listRekening = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelRekening modelRekening = ds.getValue(ModelRekening.class);
                    modelRekening.setKey(ds.getKey());
                    listRekening.add(modelRekening);
                }

                adapterManageRekening = new AdapterManageRekening(listRekening, ManageRekening.this);
                binding.recycleManageRekening.setAdapter(adapterManageRekening);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(ManageRekening.this, activity));
    }
}