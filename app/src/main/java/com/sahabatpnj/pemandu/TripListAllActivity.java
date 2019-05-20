package com.sahabatpnj.pemandu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.pemandu.model.Destination;

public class TripListAllActivity extends AppCompatActivity {

    private static final String TAG = "TripListAllActivity";
    private RecyclerView mReacyclerDestination;
    private android.support.v7.widget.Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private Query mQuery;
    private FirebaseAuth mAuth;
    private TextView mEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list_all);

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

        Intent intent = getIntent();
        String message = intent.getStringExtra("keyPemandu");
        mQuery = FirebaseDatabase.getInstance().getReference().child("users").child(message).child("destinationList").orderByChild("namaDestinasi");
        mQuery.keepSynced(true);


        setSupportActionBar(mToolbar);


        mReacyclerDestination = findViewById(R.id.listTripList);
        mReacyclerDestination.setHasFixedSize(true);
        mReacyclerDestination.setLayoutManager(new LinearLayoutManager(this));
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    protected void initRecyclerView() {

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
                        String key = getRef(position).getKey().toString();
                        Intent intent = new Intent(TripListAllActivity.this, ViewDestinationAllActivity.class);
                        intent.putExtra("namaDestinasi", key);
                        Intent intent1 = getIntent();
                        String message = intent1.getStringExtra("keyPemandu");
                        intent.putExtra("keyPemandu", message);
                        startActivity(intent);
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
