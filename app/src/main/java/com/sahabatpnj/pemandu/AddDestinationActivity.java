package com.sahabatpnj.pemandu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class AddDestinationActivity extends AppCompatActivity {
    private static final String TAG = "AddDestinationActivity";
    private ImageView mImgDestination;
    private EditText mDestinationName, mDestinationDesc;
    private Button mButtonAdd;


    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filepath;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_destination);

        mImgDestination = findViewById(R.id.imgAddDestination);
        mDestinationName = findViewById(R.id.editTextAddDestinationName);
        mDestinationDesc = findViewById(R.id.editTextAddDestinationDesc);
        mButtonAdd = findViewById(R.id.buttonAddDestinationAdd);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mToolbar = findViewById(R.id.toolbar);
        mImgDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });


        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mDestinationName.getText().toString();
                String desc = mDestinationDesc.getText().toString();
                if(name.equals("") || desc.equals("")){
                    Toast.makeText(AddDestinationActivity.this, "All fields must be filled !!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                    getData();
                }
            }
        });


        setSupportActionBar(mToolbar);

        initToolbar();

    }

    public void initToolbar() {
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getData(){
        Log.d(TAG, "getData: getdata");
        String destiName = mDestinationName.getText().toString();
        String destiDesc = mDestinationDesc.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String userID = currentUser.getUid();
        DatabaseReference myRef = database.getReference("users").child(userID).child("destinationList").child(destiName);

        myRef.child("namaDestinasi").setValue(destiName);
        myRef.child("descDestinasi").setValue(destiDesc);
        Log.d(TAG, "Data:  " + destiName + destiDesc);
    }


    private void uploadImage(){
        if(filepath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading...");
            progressDialog.show();

            final StorageReference ref =  mStorageRef.child("images/"+ UUID.randomUUID().toString()+".jpg");



            ref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String destiName = mDestinationName.getText().toString();
                                    mAuth = FirebaseAuth.getInstance();
                                    FirebaseUser currentUser = mAuth.getCurrentUser();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();


                                    String userID = currentUser.getUid();
                                    DatabaseReference myRef = database.getReference("users").child(userID).child("destinationList").child(destiName);

                                    myRef.child("imgDestinasi").setValue(uri.toString());
                                    Log.d(TAG, "MyDownloadLink:  " + uri.toString());
                                }
                            });
                            Toast.makeText(getApplicationContext(), "Trip Added", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            onBackPressed();
//                            Intent intent = new Intent(AddDestinationActivity.this, TripListAddActivity.class);
//                            startActivity(intent);
//                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " +  (int)progress +"%");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please Add Your Photo !!", Toast.LENGTH_SHORT).show();
        }
    }


    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            filepath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                mImgDestination.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
