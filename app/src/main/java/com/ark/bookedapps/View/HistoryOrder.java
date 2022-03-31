package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ark.bookedapps.Adapter.AdapterHistoryOrder;
import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.databinding.ActivityHistoryOrderBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryOrder extends AppCompatActivity {

    private ActivityHistoryOrderBinding binding;

    private AdapterHistoryOrder adapterHistoryOrder;
    private List<ModelOrder> listOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backBtn.setOnClickListener(view -> finish());

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recycleHisotryOrder.setLayoutManager(mLayout);
        binding.recycleHisotryOrder.setItemAnimator(new DefaultItemAnimator());

        setDataHistory();

    }

    private void setDataHistory(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("history_order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOrder = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    for (DataSnapshot ds1 : ds.getChildren()){
                        ModelOrder modelOrder = ds1.getValue(ModelOrder.class);
                        modelOrder.setKey(ds1.getKey());
                        listOrder.add(modelOrder);
                    }
                    adapterHistoryOrder = new AdapterHistoryOrder(listOrder, HistoryOrder.this);
                    binding.recycleHisotryOrder.setAdapter(adapterHistoryOrder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(HistoryOrder.this, activity));
    }
}