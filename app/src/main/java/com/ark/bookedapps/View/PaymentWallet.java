package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.ark.bookedapps.Adapter.AdapterPaymentWallet;
import com.ark.bookedapps.Model.ModelWallet;
import com.ark.bookedapps.databinding.ActivityPaymentWalletBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PaymentWallet extends AppCompatActivity {

    private ActivityPaymentWalletBinding binding;
    private AdapterPaymentWallet adapterPaymentWallet;
    private List<ModelWallet> listWallet;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recyclerPaymentWallet.setLayoutManager(mLayout);
        binding.recyclerPaymentWallet.setItemAnimator(new DefaultItemAnimator());

        setDataWallet();

    }

    private void setDataWallet() {
        reference.child("e_wallet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listWallet = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelWallet modelWallet = dataSnapshot.getValue(ModelWallet.class);
                    listWallet.add(modelWallet);
                }

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    adapterPaymentWallet = new AdapterPaymentWallet(listWallet, PaymentWallet.this);
                    binding.recyclerPaymentWallet.setAdapter(adapterPaymentWallet);
                }, 300);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentWallet.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}