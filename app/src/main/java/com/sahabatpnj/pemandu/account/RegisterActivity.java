package com.sahabatpnj.pemandu.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sahabatpnj.pemandu.R;
import com.sahabatpnj.pemandu.model.User;
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //Firebase
    private FirebaseAuth mAuth;

    //widgets
    private EditText mEmail, mName,mPassword, mConfirmPassword;
    private Button mRegister;
    private TextView mToLogin;
    private ProgressBar mProgressBar;

    //vars
    private Context mContext;
    private String email, name, password, confirmPassword;
    private User mUser;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRegister = findViewById(R.id.btnRegister);
        mEmail = findViewById(R.id.etRegisterEmail);
        mPassword = findViewById(R.id.etRegisterPassword);
        mConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        mName = findViewById(R.id.etRegisterName);
        mContext = RegisterActivity.this;
        mUser = new User();
        mProgressBar = findViewById(R.id.pbRegister);
        mToLogin = findViewById(R.id.txtRegisterToLogin);

        mToolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "onCreate: started");

        email = mEmail.getText().toString();
        name = mName.getText().toString();
        password = mPassword.getText().toString();
        confirmPassword = mConfirmPassword.getText().toString();

        mProgressBar.setVisibility(View.INVISIBLE);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();
                name = mName.getText().toString();
                password = mPassword.getText().toString();
                confirmPassword = mConfirmPassword.getText().toString();

                if(email.equals("")||name.equals("")||password.equals("")||confirmPassword.equals("")){
                    Toast.makeText(RegisterActivity.this, "All fields must be filled !!",
                            Toast.LENGTH_SHORT).show();
                } else if (password.equals(confirmPassword)){
                    register();
                } else {
                    Toast.makeText(RegisterActivity.this, "Password not same",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
    }



    public void register(){
        email = mEmail.getText().toString();
        name = mName.getText().toString();
        password = mPassword.getText().toString();
        confirmPassword = mConfirmPassword.getText().toString();

        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            // Write a message to the database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference myRef = database.getReference("users");

                            final String userID = user.getUid();

                            mUser.setEmail(email);
                            mUser.setName(name);
                            mUser.setPassword(password);

                            myRef.child(userID).setValue(mUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    myRef.child(userID).child("deviceToken").setValue(deviceToken);
                                }
                            });
                            Toast.makeText(mContext, "Register Succesfull",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, AfterRegisterActivity.class);
                            startActivity(intent);
                            finish();

                            mProgressBar.setVisibility(View.INVISIBLE);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();

                            Toast.makeText(mContext, "Someone with that email already exists",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);

                            mProgressBar.setVisibility(View.INVISIBLE);
                        }

                        // ...
                    }
                });
    }



}