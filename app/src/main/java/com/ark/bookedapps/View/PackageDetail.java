package com.ark.bookedapps.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ark.bookedapps.R;
import com.ark.bookedapps.Utility.Constant;
import com.ark.bookedapps.databinding.ActivityPackagesDetailBinding;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class PackageDetail extends AppCompatActivity {

    private ActivityPackagesDetailBinding binding;
    private String namePackage, price, detail, urlThumbs, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPackagesDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        namePackage = getIntent().getStringExtra("name_package");
        price = getIntent().getStringExtra("price");
        detail = getIntent().getStringExtra("detail");
        urlThumbs = getIntent().getStringExtra("url_thumbs");
        key = getIntent().getStringExtra("key");

        binding.backBtn.setOnClickListener(view -> finish());

        if (Constant.ROLE_USER.equals("Admin")){
            binding.orderNow.setEnabled(false);
        }

        binding.orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PackageDetail.this, OrderSalon.class);
                intent.putExtra("name_package", namePackage);
                intent.putExtra("price", price);
                intent.putExtra("url_thumbs", urlThumbs);
                intent.putExtra("detail", detail);
                intent.putExtra("key", key);
                startActivity(intent);

            }
        });

        setDataDetail();
    }

    private void setDataDetail() {
        Picasso.get().load(urlThumbs).into(binding.thumbsDetailPackage);
        binding.namePackageDetail.setText(namePackage);
        binding.priceDetail.setText("Rp. "+formatCurrenryRp(price));
        binding.detailPackage.setText(detail);
    }


    private void updateUI(Class activity){
        startActivity(new Intent(PackageDetail.this, activity));
    }

    private String formatCurrenryRp(String number){

        double decimalNumber = Double.parseDouble(number);

        Locale localeID = new Locale("in", "ID");
        NumberFormat rupiahFormat = NumberFormat.getNumberInstance(localeID);
        return rupiahFormat.format(decimalNumber);
    }
}