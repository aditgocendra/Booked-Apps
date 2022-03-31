package com.ark.bookedapps.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelWallet;
import com.ark.bookedapps.R;

import java.util.List;

public class AdapterPaymentWallet extends RecyclerView.Adapter<AdapterPaymentWallet.PaymentWallet> {

    private List<ModelWallet> listWallet;
    private Context mContext;

    public AdapterPaymentWallet(List<ModelWallet> listWallet, Context mContext) {
        this.listWallet = listWallet;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public PaymentWallet onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_card_e_wallet, parent, false);
        return new PaymentWallet(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentWallet holder, int position) {
        ModelWallet modelWallet = listWallet.get(position);
        holder.cardEdit.setVisibility(View.GONE);
        holder.cardDelete.setVisibility(View.GONE);

        holder.nameWallet.setText(modelWallet.getName_wallet());
        holder.numberWallet.setText(modelWallet.getNumber_wallet());
    }

    @Override
    public int getItemCount() {
        return listWallet.size();
    }

    public static class PaymentWallet extends RecyclerView.ViewHolder {
        TextView nameWallet, numberWallet;
        CardView cardEdit, cardDelete;
        public PaymentWallet(@NonNull View itemView) {
            super(itemView);

            nameWallet = itemView.findViewById(R.id.name_wallet_tv);
            numberWallet = itemView.findViewById(R.id.wallet_number_tv);
            cardEdit = itemView.findViewById(R.id.card_edit_wallet);
            cardDelete = itemView.findViewById(R.id.card_delete_wallet);
        }
    }
}
