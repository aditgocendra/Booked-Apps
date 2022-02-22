package com.ark.bookedapps.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ManageOrderAllUser extends AppCompatActivity {

    List<ModelOrder> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order_all_user);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("order_salon").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    for (DataSnapshot ds1 : ds.getChildren()){
                        ModelOrder modelOrder = ds1.getValue(ModelOrder.class);
                        list.add(modelOrder);

                    }
                }


            }
        });
    }
}