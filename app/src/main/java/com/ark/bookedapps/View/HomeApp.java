package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.ark.bookedapps.Adapter.AdapterHomeApp;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.Model.ModelUser;
import com.ark.bookedapps.Utility.BrokenConnection;
import com.ark.bookedapps.Utility.Constant;
import com.ark.bookedapps.Utility.Utilities;
import com.ark.bookedapps.databinding.ActivityHomeAppBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.List;

public class HomeApp extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();

    private ActivityHomeAppBinding binding;
    private List<ModelPackage> listPackages;
    private AdapterHomeApp adapterHomeApp;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private int max_load_data = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        if (!Utilities.checkConnection(this)){
            updateUI(BrokenConnection.class);
            finish();

        }else {
            checkUserLogin();
        }

        binding.cardMyTrans.setVisibility(View.GONE);
        binding.cardOrdered.setVisibility(View.GONE);
        binding.cardAdmin.setVisibility(View.GONE);
        binding.cardProfile.setVisibility(View.GONE);

        binding.cardAdmin.setOnClickListener(view -> updateUI(AdministratorMenu.class));
        binding.cardInformation.setOnClickListener(view -> updateUI(InformationSalon.class));
        binding.cardMyTrans.setOnClickListener(view -> updateUI(MyOrder.class));
        binding.cardOrdered.setOnClickListener(view -> updateUI(ManageOrderCustomer.class));

        binding.cardChat.setOnClickListener(view -> {
            Intent intent = new Intent(HomeApp.this, Chat.class);
            intent.putExtra("role", binding.nameUser.getText().toString());
            startActivity(intent);
        });

        binding.cardProfile.setOnClickListener(view -> {
            Intent intent = new Intent(HomeApp.this, ProfileUser.class);
            intent.putExtra("uid", mUser.getUid());
            startActivity(intent);
        });

        binding.cardLogout.setOnClickListener(view -> {
            mAuth.signOut();
            updateUI(Login.class);
            finish();
        });

        binding.swipeRefreshHomeApp.setOnRefreshListener(() -> {
            max_load_data += 5;
            setDataPackage();
            binding.swipeRefreshHomeApp.setRefreshing(false);
        });
    }

    private void setDataPackage() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("package_salon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPackages = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelPackage modelPackage = ds.getValue(ModelPackage.class);
                    modelPackage.setKey(ds.getKey());
                    if (listPackages.size() < 5){
                        listPackages.add(modelPackage);
                    }
                }

                adapterHomeApp = new AdapterHomeApp(listPackages,HomeApp.this);
                binding.recycleHomeAppItem.setAdapter(adapterHomeApp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI(Class activity){
        startActivity(new Intent(HomeApp.this, activity));
    }

    private void checkUserLogin(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null || !firebaseUser.isEmailVerified()){
            updateUI(Login.class);
            finish();
        }else {

            setLayoutRole();

            RecyclerView.LayoutManager mLayout = new LinearLayoutManager(this);
            binding.recycleHomeAppItem.setLayoutManager(mLayout);
            binding.recycleHomeAppItem.setItemAnimator(new DefaultItemAnimator());

            setDataPackage();
        }
    }

    private void setLayoutRole(){
        reference.child("users").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);

                if (user.getRole().equals("Admin")){
                    binding.cardAdmin.setVisibility(View.VISIBLE);
                    binding.cardOrdered.setVisibility(View.VISIBLE);
                    binding.nameUser.setText("Administrator");
                    binding.textLight.setText("Bagaimana kabarmu hari ini");

                }else{
                    binding.nameUser.setText(user.getUsername());
                    binding.cardMyTrans.setVisibility(View.VISIBLE);
                    binding.cardProfile.setVisibility(View.VISIBLE);
                    setChattingId();
                }

                Constant.ROLE_USER = user.getRole();
                getToken();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setChattingId(){
        Task<DataSnapshot> userChatId;
        if (!binding.nameUser.getText().toString().equals("Administrator")){
            userChatId = reference.child("chat").child(mUser.getUid()).get();
            if (userChatId != null){
                reference.child("chat").child(mUser.getUid()).child("uid").setValue(mUser.getUid());
            }
        }
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (binding.nameUser.getText().toString().equals("Administrator")){
            databaseReference.child("token_user").child("admin").child("token").setValue(token);
        }else {
            databaseReference.child("token_user").child(mUser.getUid()).child("token").setValue(token);
        }
    }

}