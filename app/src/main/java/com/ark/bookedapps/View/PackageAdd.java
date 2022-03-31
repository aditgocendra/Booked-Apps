package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.databinding.ActivityPackageAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PackageAdd extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private int PICK_MEDIA_GALLERY = 8888;
    private Uri fileUri;

    private ActivityPackageAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPackageAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backBtn.setOnClickListener(view -> finish());

        binding.selectImageBtn.setOnClickListener(view -> pickImageOnGalery());

        binding.savePaketBtn.setOnClickListener(view -> {
            if (binding.namePackageTi.getText().toString().isEmpty()){
                binding.namePackageTi.setError("Nama paket harus diisi");
            }
            else if (binding.priceTi.getText().toString().isEmpty()){
                binding.namePackageTi.setError("Harga paket harus diisi");
            }
            else if (binding.detailTi.getText().toString().isEmpty()){
                binding.namePackageTi.setError("Detail paket harus diisi");
            }else {
                if (fileUri != null){
                    saveImagePackage();
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    binding.savePaketBtn.setEnabled(false);
                }else {
                    Toast.makeText(PackageAdd.this, "Anda belum mengupload photo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pickImageOnGalery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_MEDIA_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MEDIA_GALLERY && resultCode == RESULT_OK){
            if (data.getData() != null){
                fileUri = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(fileUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    binding.thumbsPaketIv.setImageBitmap(bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveImagePackage(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String fileName = dateFormat.format(now);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("package_image/"+fileName);

        Bitmap bitmap = ((BitmapDrawable) binding.thumbsPaketIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PackageAdd.this, "Photo gagal diupload", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                saveDataPackage(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PackageAdd.this, "Url photo gagal didownload", Toast.LENGTH_SHORT).show();

                binding.progressCircular.setVisibility(View.INVISIBLE);
                binding.savePaketBtn.setEnabled(true);
            }
        }));

    }

    private void saveDataPackage(String urlPhoto){
        String package_name = binding.namePackageTi.getText().toString();
        String price = binding.priceTi.getText().toString();
        String detail = binding.detailTi.getText().toString();

        ModelPackage modelPackage = new ModelPackage(package_name, price, detail, urlPhoto);
        databaseReference.child("package_salon").push().setValue(modelPackage).addOnSuccessListener(unused -> {
            Toast.makeText(PackageAdd.this, "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
            updateUI(ManagePackage.class);

            binding.progressCircular.setVisibility(View.INVISIBLE);
            binding.savePaketBtn.setEnabled(true);
            finish();

        }).addOnFailureListener(e -> {
            Toast.makeText(PackageAdd.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            binding.savePaketBtn.setEnabled(true);
        });

    }


    private void updateUI(Class activity){
        startActivity(new Intent(PackageAdd.this, activity));
    }
}