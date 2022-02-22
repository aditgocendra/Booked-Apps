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

import com.ark.bookedapps.Model.ModelInformation;
import com.ark.bookedapps.Model.ModelPackage;
import com.ark.bookedapps.R;
import com.ark.bookedapps.databinding.ActivityPackageEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PackageEdit extends AppCompatActivity {

    private ActivityPackageEditBinding binding;
    private String key, package_name, price, detail, urlPhoto;

    private int PICK_MEDIA_GALLERY = 8888;
    private Uri fileUri;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPackageEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();



        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(ManagePackage.class);
                finish();
            }
        });

        binding.selectImageBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageOnGalery();
            }
        });

        binding.editPaketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.namePackageTiEdit.getText().toString().isEmpty()){
                    binding.namePackageTiEdit.setError("Nama paket harus diisi");
                }else if (binding.priceTiEdit.getText().toString().isEmpty()){
                    binding.priceTiEdit.setError("Harga tidak boleh kosong");
                }else if (binding.detailTiEdit.getText().toString().isEmpty()){
                    binding.detailTiEdit.setError("Detail tidak boleh kosong");
                }else {
                    if (fileUri != null){
                        savePackageImage();
                    }else {
                        savePackage(urlPhoto);
                    }
                }
            }
        });

        key = getIntent().getStringExtra("key");
        package_name = getIntent().getStringExtra("package_name");
        price = getIntent().getStringExtra("price");
        detail = getIntent().getStringExtra("detail");
        urlPhoto = getIntent().getStringExtra("urlPhoto");

        setDataEdit();


    }

    private void setDataEdit(){
        Picasso.get().load(urlPhoto).into(binding.thumbsPaketIvEdit);
        binding.namePackageTiEdit.setText(package_name);
        binding.priceTiEdit.setText(price);
        binding.detailTiEdit.setText(detail);
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
                    binding.thumbsPaketIvEdit.setImageBitmap(bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void savePackageImage(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String fileName = dateFormat.format(now);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("package_image/"+fileName);

        Bitmap bitmap = ((BitmapDrawable) binding.thumbsPaketIvEdit.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PackageEdit.this, "Photo gagal diupload", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        deleteOldPhoto();
                        savePackage(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PackageEdit.this, "Url photo gagal didownload", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void savePackage(String urlPhotoNew){
        String package_name_new = binding.namePackageTiEdit.getText().toString();
        String price_new = binding.priceTiEdit.getText().toString();
        String detail_new = binding.detailTiEdit.getText().toString();

        ModelPackage modelPackage = new ModelPackage(package_name_new, price_new, detail_new, urlPhotoNew);
        databaseReference.child("package_salon").child(key).setValue(modelPackage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateUI(ManagePackage.class);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PackageEdit.this, "Gagal mengubah data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteOldPhoto(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String name_photo = storage.getReferenceFromUrl(urlPhoto).getName();
        StorageReference deleteRef = storage.getReference("package_image/"+name_photo);

        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateUI(ManagePackage.class);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PackageEdit.this, "Gagal menghapus photo lama", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void updateUI(Class activity){
        startActivity(new Intent(PackageEdit.this, activity));
    }
}