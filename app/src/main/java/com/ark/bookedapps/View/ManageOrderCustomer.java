package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ark.bookedapps.Adapter.AdapterManageOrder;
import com.ark.bookedapps.Adapter.AdapterMyOrder;
import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.databinding.ActivityManageOrderCustomerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageOrderCustomer extends AppCompatActivity {

    private ActivityManageOrderCustomerBinding binding;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<ModelOrder> listOrder;
    private AdapterManageOrder adapterManageOrder;

    private int max_load_data = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageOrderCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();



        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(HomeApp.class);
                finish();
            }
        });

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recycleManageOrder.setLayoutManager(mLayout);
        binding.recycleManageOrder.setItemAnimator(new DefaultItemAnimator());

        setDataOrder();

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                max_load_data += 5;
                setDataOrder();

                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }


    private void setDataOrder() {
        databaseReference.child("order_salon").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listOrder = new ArrayList<>();

                    for (DataSnapshot ds : snapshot.getChildren()){
                        for (DataSnapshot ds1 : ds.getChildren()){
                            ModelOrder modelOrder = ds1.getValue(ModelOrder.class);
                            modelOrder.setKey(ds1.getKey());
                            if (listOrder.size() < max_load_data){
                                listOrder.add(modelOrder);
                            }

                        }
                    }

                    adapterManageOrder = new AdapterManageOrder(listOrder, ManageOrderCustomer.this);
                    binding.recycleManageOrder.setAdapter(adapterManageOrder);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ManageOrderCustomer.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void updateUI(Class activity){
        startActivity(new Intent(ManageOrderCustomer.this, activity));
    }
}