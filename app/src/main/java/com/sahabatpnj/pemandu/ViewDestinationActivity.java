package com.sahabatpnj.pemandu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.pemandu.model.Daerah;

public class ViewDestinationActivity extends AppCompatActivity {

    private static final String TAG = "ViewDaerahActivity";

    private ImageView mImageViewDaerah;
    private ImageButton  mButtonEdit;
    private TextView mTextTitle, mTextDesc, mTextAppbar;
    private DatabaseReference mDatabase;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_destination);

        Log.d(TAG, "onCreate: inisialisasi");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mImageViewDaerah = findViewById(R.id.imgViewDaerah);
        mTextDesc = findViewById(R.id.txtViewDaerahAbout);
        mTextTitle = findViewById(R.id.txtViewDaerahTitle);
        mTextAppbar = findViewById(R.id.tvViewDaerah);
        mToolbar = findViewById(R.id.toolbarViewDaerah);
        mProgressBar = findViewById(R.id.pbViewDaerah);
        mButtonEdit = findViewById(R.id.buttonViewDestinationEdit);

        mButtonEdit.bringToFront();
        mButtonEdit.setEnabled(false);
        mButtonEdit.setVisibility(View.INVISIBLE);

        setSupportActionBar(mToolbar);

        initToolbar();

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            System.out.println("Exception occurred");
        }

        mTextDesc.setMovementMethod(new ScrollingMovementMethod());

//        mButtonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ViewDestinationActivity.this);
//                View mView = getLayoutInflater().inflate(R.layout.layout_dialog_delete,null);
//                mBuilder.setView(mView);
//
////                TextView text = mView.findViewById(R.id.sureDelete);
////                text.setText(R.string.exit);
//
//                final AlertDialog dialog = mBuilder.create();
//                dialog.show();
//
//                Button no = mView.findViewById(R.id.btnDialogNo);
//                no.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//                Button yes = mView.findViewById(R.id.btnDialogYes);
//                yes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference myRef = database.getReference("users");
//                        mAuth = FirebaseAuth.getInstance();
//                        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//                        Intent intent = getIntent();
//                        String message = intent.getStringExtra("namaDestinasi");
//
//                        String userID = currentUser.getUid();
//                        myRef.child(userID).child("destinationList").child(message).removeValue();
//
//                    }
//                });
//            }
//        });

        mProgressBar.setVisibility(View.INVISIBLE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()){
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    String userID = currentUser.getUid();

                    Intent intent = getIntent();
                    String message = intent.getStringExtra("namaDestinasi");

                    Daerah value = dataSnapshot.getValue(Daerah.class);
                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.logo_pemandu)
                            .error(R.drawable.logo_pemandu)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH);

                    Glide.with(getApplicationContext()).load(dataSnapshot.child("users").child(userID).child("destinationList").child(message).child("imgDestinasi").getValue())
                            .apply(options)
                            .into(mImageViewDaerah);


                    mTextAppbar.setText(dataSnapshot.child("users").child(userID).child("destinationList").child(message).child("namaDestinasi").getValue().toString());
                    mTextTitle.setText(dataSnapshot.child("users").child(userID).child("destinationList").child(message).child("namaDestinasi").getValue().toString());
                    mTextDesc.setText(dataSnapshot.child("users").child(userID).child("destinationList").child(message).child("descDestinasi").getValue().toString());
                    Log.d(TAG, "Value is: " + value);
                } else {
                    Log.d(TAG, "onDataChange: gagal maning");
                }

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
