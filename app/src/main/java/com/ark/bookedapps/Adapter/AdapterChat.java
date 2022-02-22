package com.ark.bookedapps.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelMessage;
import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.R;
import com.ark.bookedapps.View.PersonalChat;
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

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyViewHolder> {

    private ArrayList<String> listChat;
    private Context mContext;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();



    public AdapterChat(ArrayList<String> listChat, Context mContext){
        this.listChat = listChat;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View viewItem = layoutInflater.inflate(R.layout.layout_chat_user, parent, false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChat.MyViewHolder holder, int position) {
        String keyList = listChat.get(position);

        databaseReference.child("users").child(keyList).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    ModelUser modelUser = task.getResult().getValue(ModelUser.class);

                    if (fUser.getUid().equals(keyList)){
                        holder.usernameTv.setText("Administrator");
                    }else {
                        holder.usernameTv.setText(modelUser.getUsername());
                    }

                    holder.roleUser.setText(modelUser.getRole());
                    setLastChat(keyList, holder);

                }else {
                    Toast.makeText(mContext, "Data gagal diambil", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.cardChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PersonalChat.class);

                if (fUser.getUid().equals(keyList)){
                    intent.putExtra("uid", keyList);
                    intent.putExtra("role_sender", "Customer");
                }else {
                    intent.putExtra("uid", keyList);
                    intent.putExtra("role_sender", "Admin");
                }

                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardChat;
        TextView usernameTv, lastChatTv, roleUser;
        ImageView notif;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTv = itemView.findViewById(R.id.username_chat);
            lastChatTv = itemView.findViewById(R.id.last_chat_tv);
            notif = itemView.findViewById(R.id.notif_read);
            cardChat = itemView.findViewById(R.id.card_chat_user);
            roleUser = itemView.findViewById(R.id.role_user);
        }
    }



    private void setLastChat(String key, MyViewHolder holder){
        databaseReference.child("message").child(key).orderByChild("timeStamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelMessage modelMessage = null;
                for (DataSnapshot ds : snapshot.getChildren()){
                    modelMessage = ds.getValue(ModelMessage.class);
                }

                if (modelMessage != null){
                    holder.lastChatTv.setText(modelMessage.getMessage());

                    if (!modelMessage.isRead() && !fUser.getUid().equals(modelMessage.getUidSender())){
                        holder.notif.setColorFilter(Color.argb(100,21, 102, 224));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
