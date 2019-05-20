package com.sahabatpnj.pemandu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewActivity extends AppCompatActivity {

    private static final String TAG = "ProfileViewActivity";
    private ImageView mImageProfile, mLogoLocation, mLogoStatus;
    private ImageButton mLogoEdit;
    private TextView mNama, mRole, mEmail, mAbout, mLocation;
    private ProgressBar mProgressBar;
    private Button mButtonAddDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        mImageProfile = findViewById(R.id.imgProfile);
        mNama = findViewById(R.id.textProfileNama);
        mRole = findViewById(R.id.textProfileRole);
        mEmail = findViewById(R.id.textProfileEmail);
        mAbout = findViewById(R.id.textProfileAbout);
        mLocation = findViewById(R.id.textProfileLocation);
        mProgressBar = findViewById(R.id.progressBarProfile);
        mLogoLocation = findViewById(R.id.logoProfileLocation);
        mLogoStatus = findViewById(R.id.logoProfileStatus);
        mLogoEdit = findViewById(R.id.logoProfileEdit);
        mButtonAddDestination = findViewById(R.id.buttonProfileAddDestination);

        mLogoEdit.bringToFront();
        mButtonAddDestination.bringToFront();
        displayData();
        initToolbar();
        initButton();
    }

    private void initButton(){
        mLogoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileViewActivity.this, EditProfileActivity.class);
                startActivity(intent);
                Log.d(TAG, "onClick: logoedit");
            }
        });

        mButtonAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileViewActivity.this, TripListAddActivity.class);
                startActivity(intent);
                Log.d(TAG, "onClick: to manage destination");
            }
        });
    }

    private void initToolbar() {
        Toolbar mToolbar;
        mToolbar = findViewById(R.id.toolbarProfileView);
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

                mNama.setText(nama);
                mRole.setText(role);
                mEmail.setText(email);
                mAbout.setText(about);
                mLocation.setText(location);

                if (role.equals("tourist")){
                    mLocation.setVisibility(View.INVISIBLE);
                    mAbout.setVisibility(View.INVISIBLE);
                    mLogoLocation.setVisibility(View.INVISIBLE);
                    mLogoStatus.setVisibility(View.INVISIBLE);
                    mButtonAddDestination.setVisibility(View.INVISIBLE);
                    mButtonAddDestination.setEnabled(false);
                }

                Glide.with(getApplicationContext()).asBitmap().load(img).into(mImageProfile);

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
}
