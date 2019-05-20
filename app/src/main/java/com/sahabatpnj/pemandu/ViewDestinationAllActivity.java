package com.sahabatpnj.pemandu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.pemandu.model.Daerah;
import com.sahabatpnj.pemandu.model.Destination;

public class ViewDestinationAllActivity extends AppCompatActivity {

    private static final String TAG = "ViewDestinationAllActiv";
    private ImageView mImageViewDaerah;
    private TextView mTextTitle, mTextDesc, mTextAppbar;
    private DatabaseReference mDatabase;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_destination_all);

        Log.d(TAG, "onCreate: inisialisasi");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mImageViewDaerah = findViewById(R.id.imgViewDaerah);
        mTextDesc = findViewById(R.id.txtViewDaerahAbout);
        mTextTitle = findViewById(R.id.txtViewDaerahTitle);
        mTextAppbar = findViewById(R.id.tvViewDaerah);
        mToolbar = findViewById(R.id.toolbarViewDaerah);
        mProgressBar = findViewById(R.id.pbViewDaerah);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            System.out.println("Exception occurred");
        }


        mTextDesc.setMovementMethod(new ScrollingMovementMethod());

        initToolbar();
        mProgressBar.setVisibility(View.INVISIBLE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mAuth = FirebaseAuth.getInstance();
//                FirebaseUser currentUser = mAuth.getCurrentUser();
//
//                String userID = currentUser.getUid();

                Intent intent = getIntent();
                String message = intent.getStringExtra("namaDestinasi");
                String messageId = intent.getStringExtra("keyPemandu");
                Daerah value = dataSnapshot.getValue(Daerah.class);
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(dataSnapshot.child("users").child(messageId).child("destinationList").child(message).getValue(Destination.class).getImgDestinasi())
                        .into(mImageViewDaerah);
                mTextAppbar.setText(dataSnapshot.child("users").child(messageId).child("destinationList").child(message).getValue(Destination.class).getNamaDestinasi());
                mTextTitle.setText(dataSnapshot.child("users").child(messageId).child("destinationList").child(message).getValue(Destination.class).getNamaDestinasi());
                mTextDesc.setText(dataSnapshot.child("users").child(messageId).child("destinationList").child(message).getValue(Destination.class).getDescDestinasi());
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        Intent intent = getIntent();
        String message = intent.getStringExtra("namaDaerah");
        Log.d(TAG, "onCreate: inisialisasi");
        //String key = FirebaseDatabase.getInstance().getReference().child("users").getKey();
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
}
