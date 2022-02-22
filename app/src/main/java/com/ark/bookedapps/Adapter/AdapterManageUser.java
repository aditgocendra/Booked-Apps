package com.ark.bookedapps.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.R;
import com.ark.bookedapps.View.UserEdit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterManageUser extends RecyclerView.Adapter<AdapterManageUser.MyViewHolder> {

    private List<ModelUser> listUsers;
    private Context mContext;



    public AdapterManageUser(List<ModelUser> listUsers, Context mContext){
        this.listUsers = listUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdapterManageUser.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View viewItem = layoutInflater.inflate(R.layout.layout_manage_user, parent, false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterManageUser.MyViewHolder holder, int position) {
        final ModelUser modelUser = listUsers.get(position);
        holder.usernameTv.setText(modelUser.getUsername());
        holder.emailTv.setText(modelUser.getEmail());
        holder.numberTv.setText(modelUser.getNo_telp());
        holder.roleTv.setText(modelUser.getRole());

        holder.cardEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserEdit.class);
                intent.putExtra("username", modelUser.getUsername());
                intent.putExtra("email", modelUser.getEmail());
                intent.putExtra("number", modelUser.getNo_telp());
                intent.putExtra("role", modelUser.getRole());
                intent.putExtra("key", modelUser.getKey());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTv, emailTv, numberTv, roleTv;
        CardView cardEditUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTv = itemView.findViewById(R.id.username_tv);
            emailTv = itemView.findViewById(R.id.email_tv);
            numberTv = itemView.findViewById(R.id.phone_number_tv);
            roleTv = itemView.findViewById(R.id.role_user_tv);
            cardEditUser = itemView.findViewById(R.id.card_user_edit);


        }
    }

}
