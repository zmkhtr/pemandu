package com.sahabatpnj.pemandu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.pemandu.model.Destination;

public class TripListAddActivity extends AppCompatActivity {

    private static final String TAG = "TripListAddActivity";
    private FloatingActionButton mFabAdd;
    private RecyclerView mReacyclerDestination;
    private android.support.v7.widget.Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private Query mQuery;
    private FirebaseAuth mAuth;
    private TextView mEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list_add);

        mFabAdd = findViewById(R.id.fabTripListAddTrip);

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripListAddActivity.this, AddDestinationActivity.class);
                startActivity(intent);
            }
        });
        mProgressBar = findViewById(R.id.progressBarTripList);
        mReacyclerDestination = findViewById(R.id.listTripList);
        mToolbar = findViewById(R.id.appbarTripList);
        mEmpty = findViewById(R.id.textTripListEmpty);

        initToolbar();

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            System.out.println("Exception occurred");
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String userID = currentUser.getUid();

        mQuery = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("destinationList").orderByChild("namaDestinasi");
        mQuery.keepSynced(true);

     

        setSupportActionBar(mToolbar);


        mReacyclerDestination = findViewById(R.id.listTripList);
        mReacyclerDestination.setHasFixedSize(true);
        mReacyclerDestination.setLayoutManager(new LinearLayoutManager(this));
        initToolbar();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mEmpty.setVisibility(View.INVISIBLE);
                    initRecyclerView();
                }
                else{
                    mEmpty.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mEmpty.setVisibility(View.INVISIBLE);
                    initRecyclerView();
                }
                else{
                    mEmpty.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mEmpty.setVisibility(View.INVISIBLE);
                    initRecyclerView();
                }
                else{
                    mEmpty.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initToolbar() {
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //@Override
    //protected void onStart() {
    //super.onStart();
    private void initRecyclerView() {
        Log.d(TAG, "onStart: Menambahkan Data ke RecyclerView");

        FirebaseRecyclerAdapter<Destination, TripListAddActivity.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Destination, TripListAddActivity.ViewHolder>
                (Destination.class, R.layout.layout_list_pemandu, TripListAddActivity.ViewHolder.class, mQuery) {
            @Override
            protected void populateViewHolder(TripListAddActivity.ViewHolder viewHolder, Destination model, int position) {
               mProgressBar.setVisibility(View.VISIBLE);
                viewHolder.setNamaDestinasi(model.getNamaDestinasi(), model.getImgDestinasi());
              mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public TripListAddActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final TripListAddActivity.ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new TripListAddActivity.ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final String key = getRef(position).getKey().toString();


                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TripListAddActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.layout_dialog_delete,null);
                        mBuilder.setView(mView);

                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        Button no = mView.findViewById(R.id.btnDialogNo);
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                Intent intent = new Intent(TripListAddActivity.this, ViewDestinationActivity.class);
                                intent.putExtra("namaDestinasi", key);
                                startActivity(intent);
                            }
                        });
                        Button yes = mView.findViewById(R.id.btnDialogYes);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("users");
                                mAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                Intent intent = getIntent();
                                String message = intent.getStringExtra("namaDestinasi");

                                String userID = currentUser.getUid();
                                myRef.child(userID).child("destinationList").child(key).removeValue();
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Delete Succesfull",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
                return viewHolder;
            }
        };
        mReacyclerDestination.setAdapter(firebaseRecyclerAdapter);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView namaDestinasiList;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }

        public void setNamaDestinasi(String namaDestinasi, String imgUser) {
            TextView namaUserText = mView.findViewById(R.id.textListPemanduNama);
            ImageView imgUserText = mView.findViewById(R.id.imageListPemandu);
            namaUserText.setText(namaDestinasi);

            Glide.with(mView).asBitmap().load(imgUser).into(imgUserText);
        }

        private TripListAddActivity.ViewHolder.ClickListener mClickListener;

        public interface ClickListener {
            void onItemClick(View view, int position);
        }

        public void setOnClickListener(TripListAddActivity.ViewHolder.ClickListener clickListener) {
            mClickListener = clickListener;
        }
    }

}
