package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.ark.bookedapps.Adapter.AdapterMyOrder;
import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.databinding.ActivityMyOrderBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyOrder extends AppCompatActivity {

    private ActivityMyOrderBinding binding;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<ModelOrder> listOrder;
    private ArrayList<ModelPackage> listPackage;
    private AdapterMyOrder adapterMyOrder;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backBtn.setOnClickListener(view -> finish());

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recycleMyOrder.setLayoutManager(mLayout);
        binding.recycleMyOrder.setItemAnimator(new DefaultItemAnimator());

        setDataMyOrder();

    }

    private void setDataMyOrder() {
        databaseReference.child("order_salon").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOrder = new ArrayList<>();
                listPackage = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelOrder modelOrder = ds.getValue(ModelOrder.class);
                    modelOrder.setKey(ds.getKey());
                    listOrder.add(modelOrder);
                    getDataPackage(modelOrder.getKey_package());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyOrder.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataPackage(String keyPackage){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("package_salon").child(keyPackage).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelPackage modelPackage = task.getResult().getValue(ModelPackage.class);
                listPackage.add(modelPackage);

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    adapterMyOrder = new AdapterMyOrder(listOrder, listPackage,MyOrder.this);
                    binding.recycleMyOrder.setAdapter(adapterMyOrder);
                }, 500);
            }else {
                Toast.makeText(MyOrder.this, "Kesalahan pengambilan data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(MyOrder.this, activity));
    }
}