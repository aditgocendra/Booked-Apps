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

import com.ark.bookedapps.Adapter.AdapterPersonalChat;
import com.ark.bookedapps.Model.ModelMessage;
import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.R;
import com.ark.bookedapps.Utility.BrokenConnection;
import com.ark.bookedapps.Utility.Constant;
import com.ark.bookedapps.Utility.Utilities;
import com.ark.bookedapps.databinding.ActivityPersonalChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonalChat extends AppCompatActivity {


    private ActivityPersonalChatBinding binding;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String uidReceiver, roleSender;

    private AdapterPersonalChat adapterPersonalChat;
    private List<ModelMessage> messageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        uidReceiver = getIntent().getStringExtra("uid");
        roleSender = getIntent().getStringExtra("role_sender");

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalChat.this, Chat.class);
                intent.putExtra("role", roleSender);
                startActivity(intent);
            }
        });

        binding.btnSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.chatSend.getText().toString().isEmpty()){
                    binding.btnSendChat.setEnabled(false);
                    sendMessage();
                }
            }
        });

        setUsername(uidReceiver);

        binding.recycleChat.setHasFixedSize(true);
        LinearLayoutManager mLayout = new LinearLayoutManager(this);
        mLayout.setStackFromEnd(true);
        binding.recycleChat.setLayoutManager(mLayout);
        binding.recycleChat.setItemAnimator(new DefaultItemAnimator());

        if (!Utilities.checkConnection(this)){
            updateUI(BrokenConnection.class);
            finish();
        }else {
            setChatUser(uidReceiver);
        }

    }

    private void setChatUser(String uid) {
        databaseReference.child("message").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelMessage modelMessage = ds.getValue(ModelMessage.class);
                    messageList.add(modelMessage);
                }

                adapterPersonalChat = new AdapterPersonalChat(messageList, roleSender, PersonalChat.this);
                binding.recycleChat.setAdapter(adapterPersonalChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void setChattingId(){
//
//        Task<DataSnapshot> userChatId;
//
//        if (!roleSender.equals("Admin")){
//            userChatId = databaseReference.child("chat").child(user.getUid()).get();
//
//            if (userChatId != null){
//                databaseReference.child("chat").child(user.getUid()).child("uid").setValue(user.getUid());
//            }
//
//        }else {
//            userChatId = databaseReference.child("chat").child(uidReceiver).get();
//
//            if (userChatId != null){
//                databaseReference.child("chat").child(uidReceiver).child("uid").setValue(uidReceiver);
//            }
//        }
//    }


    private void sendMessage(){
        long tsLong = System.currentTimeMillis()/1000;
        String timeStamp = Long.toString(tsLong);

        ModelMessage modelChat;

        if (roleSender.equals("Admin")) {
            modelChat = new ModelMessage(
                    "Admin",
                    binding.chatSend.getText().toString(),
                    timeStamp,
                    false);
        }else {
            modelChat = new ModelMessage(
                    user.getUid(),
                    binding.chatSend.getText().toString(),
                    timeStamp,
                    false);
        }

        databaseReference.child("message").child(uidReceiver).push().setValue(modelChat).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PersonalChat.this, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show();
                binding.chatSend.setText("");
                binding.btnSendChat.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (roleSender.equals("Admin")){
                    databaseReference.child("token_user").child(uidReceiver).child("token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                sendingCloud(task.getResult().getValue().toString());
                            }
                        }
                    });
                }else {
                    databaseReference.child("token_user").child("admin").child("token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                sendingCloud(task.getResult().getValue().toString());
                            }
                        }
                    });
                }
            }
        });

    }

    private void sendingCloud(String tokenUser){
        try {
            JSONArray token = new JSONArray();
            token.put(tokenUser);

            JSONObject data = new JSONObject();
            if (roleSender.equals("Admin")){
                data.put("uid_receiver", uidReceiver);
                data.put("receiver", "Customer");
                data.put("title", "Admin");
            }else {
                data.put("uid_receiver", "-");
                data.put("receiver", "Admin");
                data.put("title", "Customer");

            }
            data.put("message", binding.chatSend.getText().toString());

            JSONObject body = new JSONObject();
            body.put(Constant.REMOTE_MSG_DATA, data);
            body.put(Constant.REMOTE_MSG_REGISTRATION_IDS, token);

            Utilities.sendNotification(body.toString(), PersonalChat.this);

        }catch (Exception e){
            Toast.makeText(PersonalChat.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        binding.chatSend.setText("");
        binding.btnSendChat.setEnabled(true);
    }


    private void setUsername(String uid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (!uid.equals(user.getUid())){
            databaseReference.child("users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                        binding.usernameTvPersonal.setText(modelUser.getUsername());
                    }else {
                        Toast.makeText(PersonalChat.this, "Data gagal diambil", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            binding.usernameTvPersonal.setText("Administrator");
        }
    }

    private void updateUI(Class activity){
        startActivity(new Intent(PersonalChat.this, activity));
    }
}