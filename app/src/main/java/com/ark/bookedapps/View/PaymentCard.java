package com.ark.bookedapps.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.ark.bookedapps.Adapter.AdapterPaymentCard;
import com.ark.bookedapps.Model.ModelRekening;
import com.ark.bookedapps.databinding.ActivityPaymentCardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class PaymentCard extends AppCompatActivity {

    private ActivityPaymentCardBinding binding;
    private List<ModelRekening> listRekening;
    private AdapterPaymentCard adapterPaymentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backBtn.setOnClickListener(view ->finish());

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recycleCardPay.setLayoutManager(mLayout);
        binding.recycleCardPay.setItemAnimator(new DefaultItemAnimator());

        setDataCardPayment();

    }

    private void setDataCardPayment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("rekening_salon").get().addOnSuccessListener(dataSnapshot -> {
            listRekening = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()){
                ModelRekening modelRekening = ds.getValue(ModelRekening.class);
                listRekening.add(modelRekening);
            }
            adapterPaymentCard = new AdapterPaymentCard(listRekening, PaymentCard.this);
            binding.recycleCardPay.setAdapter(adapterPaymentCard);
        }).addOnFailureListener(e -> Toast.makeText(PaymentCard.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show());
    }

    private void updateUI(Class activity){
        startActivity(new Intent(PaymentCard.this, activity));
    }
}