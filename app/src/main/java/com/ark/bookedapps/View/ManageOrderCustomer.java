package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ark.bookedapps.Adapter.AdapterManageOrder;
import com.ark.bookedapps.Adapter.AdapterMyOrder;
import com.ark.bookedapps.Model.ModelOrder;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityManageOrderCustomerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageOrderCustomer extends AppCompatActivity {

    private ActivityManageOrderCustomerBinding binding;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<ModelOrder> listOrder;
    private AdapterManageOrder adapterManageOrder;

    private int max_load_data = 5;

    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageOrderCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backBtn.setOnClickListener(view -> finish());

        bottomSheetDialog = new BottomSheetDialog(this);
        setBottomFilter();
        binding.filterBtn.setOnClickListener(view -> bottomSheetDialog.show());

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
        binding.recycleManageOrder.setLayoutManager(mLayout);
        binding.recycleManageOrder.setItemAnimator(new DefaultItemAnimator());

        setDataOrder("-");

        binding.swipeRefresh.setOnRefreshListener(() -> {
            max_load_data += 5;
            setDataOrder("-");

            binding.swipeRefresh.setRefreshing(false);
        });
    }


    private void setDataOrder(String filter) {
        databaseReference.child("order_salon").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listOrder = new ArrayList<>();

                    for (DataSnapshot ds : snapshot.getChildren()){
                        for (DataSnapshot ds1 : ds.getChildren()){
                            ModelOrder modelOrder = ds1.getValue(ModelOrder.class);
                            if (modelOrder != null){
                                modelOrder.setKey(ds1.getKey());
                                if (listOrder.size() < max_load_data){
                                    if (filter.equals("-")){
                                        listOrder.add(modelOrder);
                                    }else if (modelOrder.getStatus().equals(filter)){
                                        listOrder.add(modelOrder);
                                    }

                                }
                            }
                        }
                    }

                    adapterManageOrder = new AdapterManageOrder(listOrder, ManageOrderCustomer.this);
                    binding.recycleManageOrder.setAdapter(adapterManageOrder);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ManageOrderCustomer.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void updateUI(Class activity){
        startActivity(new Intent(ManageOrderCustomer.this, activity));
    }

    String filterStatus;
    private void setBottomFilter(){
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_filter_order, null, false);
        MaterialButton allBtn = viewBottomDialog.findViewById(R.id.all_filter_btn);
        MaterialButton waitConfirm = viewBottomDialog.findViewById(R.id.wait_confirm_btn);
        MaterialButton rejectOrder = viewBottomDialog.findViewById(R.id.reject_confirm_btn);
        MaterialButton waitPay = viewBottomDialog.findViewById(R.id.wait_pay_btn);
        MaterialButton confirmPay = viewBottomDialog.findViewById(R.id.confirm_pay_btn);
        Button finishButton = viewBottomDialog.findViewById(R.id.finish_btn);

        waitConfirm.setOnClickListener(view -> {
            waitPay.setTextColor(getResources().getColor(R.color.light_text));
            rejectOrder.setTextColor(getResources().getColor(R.color.light_text));
            confirmPay.setTextColor(getResources().getColor(R.color.light_text));

            waitConfirm.setTextColor(getResources().getColor(R.color.blue_light));
            filterStatus = "Menunggu Konfirmasi Admin";
        });

        allBtn.setOnClickListener(view -> {
            waitConfirm.setTextColor(getResources().getColor(R.color.light_text));
            waitPay.setTextColor(getResources().getColor(R.color.light_text));
            confirmPay.setTextColor(getResources().getColor(R.color.light_text));
            rejectOrder.setTextColor(getResources().getColor(R.color.light_text));

            allBtn.setTextColor(getResources().getColor(R.color.blue_light));
            filterStatus = "-";
        });

        rejectOrder.setOnClickListener(view -> {
            waitConfirm.setTextColor(getResources().getColor(R.color.light_text));
            waitPay.setTextColor(getResources().getColor(R.color.light_text));
            confirmPay.setTextColor(getResources().getColor(R.color.light_text));
            allBtn.setTextColor(getResources().getColor(R.color.light_text));

            rejectOrder.setTextColor(getResources().getColor(R.color.blue_light));
            filterStatus = "Dibatalkan";
        });

        waitPay.setOnClickListener(view -> {
            waitConfirm.setTextColor(getResources().getColor(R.color.light_text));
            rejectOrder.setTextColor(getResources().getColor(R.color.light_text));
            confirmPay.setTextColor(getResources().getColor(R.color.light_text));
            allBtn.setTextColor(getResources().getColor(R.color.light_text));

            waitPay.setTextColor(getResources().getColor(R.color.blue_light));
            filterStatus = "(Disetejui) Menunggu Pembayaran";
        });

        confirmPay.setOnClickListener(view -> {
            waitConfirm.setTextColor(getResources().getColor(R.color.light_text));
            rejectOrder.setTextColor(getResources().getColor(R.color.light_text));
            waitPay.setTextColor(getResources().getColor(R.color.light_text));
            allBtn.setTextColor(getResources().getColor(R.color.light_text));

            confirmPay.setTextColor(getResources().getColor(R.color.blue_light));
            filterStatus = "Pembayaran DP dikonfirmasi";
        });


        finishButton.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            setDataOrder(filterStatus);
        });

        bottomSheetDialog.setContentView(viewBottomDialog);

    }
}