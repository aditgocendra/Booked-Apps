package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ark.bookedapps.Adapter.AdapterManagePackage;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityManagePackageBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagePackage extends AppCompatActivity {

    private ActivityManagePackageBinding binding;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private AdapterManagePackage adapterManagePackage;
    private ArrayList<ModelPackage> listPackages;

    private int max_load_data = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagePackageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.backBtn.setOnClickListener(view -> finish());

        binding.addFloatBtn.setOnClickListener(view -> updateUI(PackageAdd.class));

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recycleManagePackage.setLayoutManager(mLayout);
        binding.recycleManagePackage.setItemAnimator(new DefaultItemAnimator());

        setDataPackage();

        binding.swipeRefreshManagePackage.setOnRefreshListener(() -> {
            max_load_data += 5;
            setDataPackage();
            binding.swipeRefreshManagePackage.setRefreshing(false);
        });

    }

    private void setDataPackage() {
        databaseReference.child("package_salon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 listPackages = new ArrayList<>();

                 for (DataSnapshot item : snapshot.getChildren()){
                        ModelPackage modelPackage = item.getValue(ModelPackage.class);
                        modelPackage.setKey(item.getKey());
                        if (listPackages.size() < max_load_data){
                            listPackages.add(modelPackage);
                        }
                 }

                 adapterManagePackage = new AdapterManagePackage(listPackages, ManagePackage.this);
                 binding.recycleManagePackage.setAdapter(adapterManagePackage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(ManagePackage.this, activity));
    }
}