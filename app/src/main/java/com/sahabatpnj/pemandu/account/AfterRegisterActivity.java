package com.sahabatpnj.pemandu.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sahabatpnj.pemandu.R;
import com.sahabatpnj.pemandu.model.User;

public class AfterRegisterActivity extends AppCompatActivity {

    private static final String TAG = "AfterRegisterActivity";

    private Button mTourist, mTourguide;
    private FirebaseAuth mAuth;
    private User mUser;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_register);

        mUser = new User();
        mTourist = findViewById(R.id.btnAfterRegistTourist);
        mTourguide = findViewById(R.id.btnAfterRegistTourguide);

        mTourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");

                String userID = currentUser.getUid();


                myRef.child(userID).child("role").setValue("tourist");

                Intent intent = new Intent(AfterRegisterActivity.this, RegisterTouristActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mTourguide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");

                String userID = currentUser.getUid();


                myRef.child(userID).child("role").setValue("tourguide");
                Intent intent = new Intent(AfterRegisterActivity.this, RegisterTourguideActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Please Complete Your Data", Toast.LENGTH_SHORT).show();
    }
}
