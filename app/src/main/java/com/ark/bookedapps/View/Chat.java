package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ark.bookedapps.Adapter.AdapterChat;
import com.ark.bookedapps.Adapter.AdapterHomeApp;
import com.ark.bookedapps.Utility.BrokenConnection;
import com.ark.bookedapps.Utility.Utilities;
import com.ark.bookedapps.databinding.ActivityChatBinding;
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

public class Chat extends AppCompatActivity {

    private ActivityChatBinding binding;

    private AdapterChat adapterChat;
    private ArrayList<String> listChat;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        role = getIntent().getStringExtra("role");

        if (!Utilities.checkConnection(this)){
            updateUI(BrokenConnection.class);
            finish();
        }


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(HomeApp.class);
            }
        });

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recyleChatUser.setLayoutManager(mLayout);
        binding.recyleChatUser.setItemAnimator(new DefaultItemAnimator());

        setDataChat();

    }


    private void setDataChat() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (role.equals("Administrator") || role.equals("Admin")){
            databaseReference.child("chat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listChat = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                        listChat.add(dataSnapshot.getKey());
                    }

                    adapterChat = new AdapterChat(listChat,Chat.this);
                    binding.recyleChatUser.setAdapter(adapterChat);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            databaseReference.child("chat").child(firebaseUser.getUid()).child("uid").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    listChat = new ArrayList<>();
                    if (task.isSuccessful()){
                        listChat.add(task.getResult().getValue().toString());
                    }
                    adapterChat = new AdapterChat(listChat,Chat.this);
                    binding.recyleChatUser.setAdapter(adapterChat);
                }
            });
        }



    }


    private void updateUI(Class activity){
        startActivity(new Intent(Chat.this, activity));
    }



}