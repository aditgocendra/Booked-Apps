package com.ark.bookedapps.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ark.bookedapps.Model.ModelInformation;
import com.ark.bookedapps.databinding.ActivityManageInformationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
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

public class ManageInformation extends AppCompatActivity {

    private int PICK_MEDIA_GALLERY = 8888;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private Uri fileUri;
    private String urlPhoto;

    private ActivityManageInformationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(AdministratorMenu.class);
                finish();
            }
        });


        binding.selectImageOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(ManageInformation.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    pickImageOnGalery();

                }else{
                    ActivityCompat.requestPermissions(ManageInformation.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_MEDIA_GALLERY);
                }

            }
        });

        getDataInformationSalon();


        binding.saveInformationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.salonName.getText().toString().isEmpty()){
                    binding.salonName.setError("Field tidak boleh kosong");
                }

                else if (binding.locationSalon.getText().toString().isEmpty()){
                    binding.locationSalon.setError("Field tidak boleh kosong");
                }

                else if (binding.aboutSalon.getText().toString().isEmpty()){
                    binding.aboutSalon.setError("Field tidak boleh kosong");
                }

                else if (binding.ordered.getText().toString().isEmpty()){
                    binding.ordered.setError("Field tidak boleh kosong");
                }

                else if (binding.nameOwnerSalon.getText().toString().isEmpty()){
                    binding.nameOwnerSalon.setError("Field tidak boleh kosong");
                }

                else if (binding.emailOwnerSalon.getText().toString().isEmpty()){
                    binding.emailOwnerSalon.setError("Field tidak boleh kosong");
                }

                else{
                    if (fileUri != null){
                        saveOwnerImage();
                    }else {
                        saveInformationSalon(urlPhoto);
                    }
                }
            }
        });
    }

    private void getDataInformationSalon(){
        databaseReference.child("information_salon").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    ModelInformation modelInformation = task.getResult().getValue(ModelInformation.class);

                    assert modelInformation != null;
                    binding.salonName.setText(modelInformation.getSalon_name());
                    binding.locationSalon.setText(modelInformation.getSalon_location());
                    binding.aboutSalon.setText(modelInformation.getSalon_about());
                    binding.ordered.setText(modelInformation.getOrdered());
                    binding.nameOwnerSalon.setText(modelInformation.getOwner_salon());
                    binding.emailOwnerSalon.setText(modelInformation.getEmail_owner_salon());

                    Picasso.get().load(modelInformation.getUrl_owner_image()).resize(50,50).centerCrop().into(binding.thumbsOwnerImage);
                    urlPhoto = modelInformation.getUrl_owner_image();

                }else{
                    Toast.makeText(ManageInformation.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
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

    private void saveOwnerImage(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String fileName = dateFormat.format(now);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("owner_image/"+fileName);

        Bitmap bitmap = ((BitmapDrawable) binding.thumbsOwnerImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ManageInformation.this, "Photo gagal diupload", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        saveInformationSalon(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageInformation.this, "Url photo gagal didownload", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void saveInformationSalon(String urlPhoto){
        String salon_name = binding.salonName.getText().toString();
        String salon_location = binding.locationSalon.getText().toString();
        String salon_about = binding.aboutSalon.getText().toString();
        String ordered = binding.ordered.getText().toString();
        String name_owner = binding.nameOwnerSalon.getText().toString();
        String email_owner = binding.emailOwnerSalon.getText().toString();

        ModelInformation modelInformation = new ModelInformation(salon_name, salon_location, salon_about, ordered, name_owner, email_owner, urlPhoto);
        databaseReference.child("information_salon").setValue(modelInformation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (fileUri != null){
                    deleteOldPhotoOwner();
                }else{
                    updateUI(InformationSalon.class);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ManageInformation.this, "Gagal mengubah data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteOldPhotoOwner(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String name_photo = storage.getReferenceFromUrl(urlPhoto).getName();
        StorageReference deleteRef = storage.getReference("owner_image/"+name_photo);

        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateUI(InformationSalon.class);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ManageInformation.this, "Gagal menghapus photo lama owner", Toast.LENGTH_SHORT).show();
            }
        });

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
                    binding.thumbsOwnerImage.setImageBitmap(bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateUI(Class activity){
        startActivity(new Intent(ManageInformation.this, activity));
    }

}