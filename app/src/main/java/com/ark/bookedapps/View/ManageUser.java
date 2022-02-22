package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ark.bookedapps.Adapter.AdapterManageUser;
import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityManageUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageUser extends AppCompatActivity {

    private ActivityManageUserBinding binding;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private AdapterManageUser adapterManageUser;
    private ArrayList<ModelUser> listUsers;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(AdministratorMenu.class);
            }
        });

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recycleManageUser.setLayoutManager(mLayout);
        binding.recycleManageUser.setItemAnimator(new DefaultItemAnimator());

        setDataAllUser();
    }

    private void setDataAllUser() {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                    modelUser.setKey(dataSnapshot.getKey());

                    if (!firebaseUser.getUid().equals(modelUser.getKey())){
                        listUsers.add(modelUser);
                    }


                }

                adapterManageUser = new AdapterManageUser(listUsers, ManageUser.this);
                binding.recycleManageUser.setAdapter(adapterManageUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(ManageUser.this, activity));
    }
}