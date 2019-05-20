package com.sahabatpnj.pemandu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filepath;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText mName, mAbout;
    private ImageView mPhoto;
    private Button mDoneEdit;
    private MaterialSpinner mSpinner;
    private android.support.v7.widget.Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private TextView mLocation, mTxtAbout;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        user.getUid();

        mName = findViewById(R.id.etEditName);
        mAbout = findViewById(R.id.etEditAboutYou);
        mPhoto = findViewById(R.id.imgEditphoto);
        mDoneEdit = findViewById(R.id.btnEditDone);
        mSpinner = findViewById(R.id.spinnerEditList);
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBarEdit);
        mLocation = findViewById(R.id.textEditLocation);
        mTxtAbout = findViewById(R.id.textEditAbout);

        setSupportActionBar(mToolbar);

        spinnerList();
        initToolbar();
        displayData();
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

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void spinnerList() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("daerah").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> areas = new ArrayList<String>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("namaDaerah").getValue(String.class);
                    areas.add(areaName);
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        displayData();
        super.onStart();
    }

    private void displayData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        mProgressBar.setVisibility(View.VISIBLE);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String nama = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                String img = dataSnapshot.child(user.getUid()).child("image").getValue(String.class);
                String role = dataSnapshot.child(user.getUid()).child("role").getValue(String.class);
                String email = dataSnapshot.child(user.getUid()).child("email").getValue(String.class);
                String about = dataSnapshot.child(user.getUid()).child("about").getValue(String.class);
                String location = dataSnapshot.child(user.getUid()).child("location").getValue(String.class);
                //String value = dataSnapshot.getValue(String.class);

                mName.setText(nama);
                mAbout.setText(about);
                mSpinner.setText(location);

                if (role.equals("tourist")) {
                    mSpinner.setVisibility(View.INVISIBLE);
                    mAbout.setVisibility(View.INVISIBLE);
                    mAbout.setHint(" ");
                    mAbout.setText(" ");
                    mTxtAbout.setVisibility(View.INVISIBLE);
                    mLocation.setVisibility(View.INVISIBLE);
                    mAbout.setEnabled(false);
                    mSpinner.setEnabled(false);
                }
                validasi(role);

                Glide.with(getApplicationContext()).asBitmap().load(img).into(mPhoto);

                Log.d(TAG, "Value is: " + nama + role + email + about + location + img);

                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void validasi(final String role) {
        mDoneEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = mName.getText().toString();
                String about = mAbout.getText().toString();
                String daerah = mSpinner.getText().toString();
                if (nama.equals("") || about.equals("")) {
                    Toast.makeText(EditProfileActivity.this, "All fields must be filled !!",
                            Toast.LENGTH_SHORT).show();
                } else if (role.equals("tourguide")) {
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("users");

                        String userID = currentUser.getUid();

                        myRef.child(userID).child("location").setValue(daerah);
                        myRef.child(userID).child("about").setValue(about);
                        myRef.child(userID).child("name").setValue(nama);
                        Log.d(TAG, "Data:  " + daerah + about);
                        if (filepath != null) {
                            uploadImage();
                        }
                        onBackPressed();
                        Toast.makeText(getApplicationContext(),"Profile Updated", Toast.LENGTH_SHORT).show();
                    } else if (role.equals("tourist")) {
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users");

                    String userID = currentUser.getUid();

                    myRef.child(userID).child("name").setValue(nama);
                    Log.d(TAG, "Data:  " + daerah + about);

                    if (filepath != null) {
                        uploadImage();
                    }
                    onBackPressed();
                    Toast.makeText(getApplicationContext(),"Profile Updated", Toast.LENGTH_SHORT).show();
                }
                }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
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
                                    mAuth = FirebaseAuth.getInstance();
                                    FirebaseUser currentUser = mAuth.getCurrentUser();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("users");

                                    String userID = currentUser.getUid();

                                    myRef.child(userID).child("image").setValue(uri.toString());
                                    Log.d(TAG, "MyDownloadLink:  " + uri.toString());
                                }
                            });
                            Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                            //progressDialog.dismiss();
                            onBackPressed();
//                            Intent intent = new Intent(EditProfileActivity.this, ProfileViewActivity.class);
//                            startActivity(intent);
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
                mPhoto.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
