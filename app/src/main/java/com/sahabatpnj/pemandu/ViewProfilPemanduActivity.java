package com.sahabatpnj.pemandu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewProfilPemanduActivity extends AppCompatActivity {

    private static final String TAG = "ProfileViewActivity";
    private ImageView mImageProfile;
    private TextView mNama,mEmail, mAbout;
    private ProgressBar mProgressBar;
    private Button mButtonAddDestination, mBtnMessage;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profil_pemandu);

        mImageProfile = findViewById(R.id.imgProfile);
        mNama = findViewById(R.id.textProfileNama);
        mEmail = findViewById(R.id.textProfileEmail);
        mAbout = findViewById(R.id.textProfileAbout);
        mProgressBar = findViewById(R.id.progressBarProfile);
        mButtonAddDestination = findViewById(R.id.buttonProfileAddDestination);
        mBtnMessage = findViewById(R.id.btnMessageTourguide);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mButtonAddDestination.bringToFront();
        displayData();
        initToolbar();
        initButton();
    }

    private void initButton(){
        mButtonAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfilPemanduActivity.this, TripListAllActivity.class);
                Intent intentData = getIntent();
                String message = intentData.getStringExtra("keyPemandu");
                intent.putExtra("keyPemandu",message);
                startActivity(intent);
                Log.d(TAG, "onClick: to destination" + message);
            }
        });

        mBtnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentData = getIntent();
                String message = intentData.getStringExtra("keyPemandu");
                Log.d(TAG, "onClick: a " + message);
                if (user == null){
                    Toast.makeText(getApplicationContext(),"Please login First!", Toast.LENGTH_SHORT).show();
                } else if(user != null) {
                    String id = user.getUid();
                    if (id.equals(message)) {
                        Toast.makeText(getApplicationContext(), "You Can't Chat With Yourself", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onClick: " + id);
                    } else {
                        Intent intent = new Intent(ViewProfilPemanduActivity.this, ChatActivity.class);
                        intent.putExtra("USER_ID", message);
                        startActivity(intent);
                        Log.d(TAG, "onClick: to destination " + message);
                    }
                }
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

                Intent intent = getIntent();
                String message = intent.getStringExtra("keyPemandu");
                String nama = dataSnapshot.child(message).child("name").getValue(String.class);
                String img = dataSnapshot.child(message).child("image").getValue(String.class);
                String email = dataSnapshot.child(message).child("email").getValue(String.class);
                String about = dataSnapshot.child(message).child("about").getValue(String.class);

                mNama.setText(nama);
                mEmail.setText(email);
                mAbout.setText(about);



                Glide.with(getApplicationContext()).asBitmap().load(img).into(mImageProfile);

                Log.d(TAG, "Value is: " + nama +  email + about + img);

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
