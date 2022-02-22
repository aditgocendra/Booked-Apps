package com.ark.bookedapps.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelMessage;
import com.ark.bookedapps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AdapterPersonalChat extends RecyclerView.Adapter {

    private List<ModelMessage> messageList;
    private String roleSender;
    private Context mContext;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public AdapterPersonalChat(List<ModelMessage> messageList, String roleSender,Context mContext){
        this.messageList = messageList;
        this.roleSender = roleSender;
        this.mContext = mContext;
    }


    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getUidSender().equals(user.getUid())){
            return 0;
        }else {
            if (messageList.get(position).getUidSender().equals(roleSender)){
                return 0;
            }
            return 1;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == 0){
            view = layoutInflater.inflate(R.layout.layout_chat_blue, parent, false);
            return new ViewHolderBlue(view);
        }else {
            view = layoutInflater.inflate(R.layout.layout_chat_light, parent, false);
            return new ViewHolderLight(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelMessage modelMessage = messageList.get(position);

        if (modelMessage.getUidSender().equals(user.getUid())){
            ViewHolderBlue holderBlue = (ViewHolderBlue) holder;
            holderBlue.chatTextBlueTv.setText(modelMessage.getMessage());
        }else {
            if (messageList.get(position).getUidSender().equals(roleSender)){
                ViewHolderBlue holderBlue = (ViewHolderBlue) holder;
                holderBlue.chatTextBlueTv.setText(modelMessage.getMessage());
            }else {
                ViewHolderLight holderLight = (ViewHolderLight) holder;
                holderLight.chatTextLightTv.setText(modelMessage.getMessage());
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ViewHolderBlue extends RecyclerView.ViewHolder {
        TextView chatTextBlueTv;
        public ViewHolderBlue(@NonNull View itemView) {
            super(itemView);
            chatTextBlueTv = itemView.findViewById(R.id.text_chat_blue);
        }
    }

    class ViewHolderLight extends RecyclerView.ViewHolder {
        TextView chatTextLightTv;
        public ViewHolderLight(@NonNull View itemView) {
            super(itemView);
            chatTextLightTv = itemView.findViewById(R.id.text_chat_light);
        }
    }
}
