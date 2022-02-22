package com.ark.bookedapps.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.bookedapps.Model.ModelRekening;
import com.ark.bookedapps.R;

import java.util.List;

public class AdapterPaymentCard extends RecyclerView.Adapter<AdapterPaymentCard.MyViewHolder> {

    private List<ModelRekening> listRek;
    private Context mContext;

    public AdapterPaymentCard(List<ModelRekening> listRek, Context mContext){
        this.listRek = listRek;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View viewItem = layoutInflater.inflate(R.layout.layout_card_payment, parent, false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPaymentCard.MyViewHolder holder, int position) {
        ModelRekening modelRekening = listRek.get(position);
    }

    @Override
    public int getItemCount() {
        return listRek.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameBank, noRek;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameBank = itemView.findViewById(R.id.name_bank_tv);
            noRek = itemView.findViewById(R.id.rek_no_tv);
        }
    }
}
