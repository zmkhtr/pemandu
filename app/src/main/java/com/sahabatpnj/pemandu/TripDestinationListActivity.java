package com.sahabatpnj.pemandu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sahabatpnj.pemandu.model.Daerah;

public class TripDestinationListActivity extends AppCompatActivity {

    private static final String TAG = "TripDestinationListActi";

    private RecyclerView mListDaerah;
    private ProgressBar mProgressBar;
    private Query mQuery;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_destination_list);

        Log.d(TAG, "onCreate: Create List Destination");

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            System.out.println("Exception occurred");
        }
        mQuery = FirebaseDatabase.getInstance().getReference().child("daerah").orderByChild("namaDaerah");
        mQuery.keepSynced(true);

        mProgressBar = findViewById(R.id.listDaerahProgressBar);

        mToolbar = findViewById(R.id.appbarListDaerah);
        setSupportActionBar(mToolbar);


        mListDaerah = findViewById(R.id.RvDaerah);
        mListDaerah.setHasFixedSize(true);
        mListDaerah.setLayoutManager(new LinearLayoutManager(this));

        initToolbar();
    }


    public void initToolbar(){
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: Menambahkan Data ke RecyclerView");

        FirebaseRecyclerAdapter<Daerah, ViewHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Daerah, ViewHolder>
                (Daerah.class, R.layout.layout_list_daerah, ViewHolder.class, mQuery) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Daerah model, int position) {
                mProgressBar.setVisibility(View.VISIBLE);
                viewHolder.setNamaDaerah(model.getNamaDaerah());
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String key = getRef(position).getKey().toString();
                        Intent intent = new Intent(TripDestinationListActivity.this, ViewDaerahActivity.class);
                        intent.putExtra("namaDaerah",key);
                        startActivity(intent);
                    }
                });
                return viewHolder;
            }
        };
        mListDaerah.setAdapter(firebaseRecyclerAdapter);
    }





    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        TextView namaDaerahList;

        public ViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
        public void setNamaDaerah(String namaDaerah){
            namaDaerahList = mView.findViewById(R.id.namaDaerahList);
            namaDaerahList.setText(namaDaerah);
        }

        private ViewHolder.ClickListener mClickListener;

        public interface ClickListener{
            void onItemClick(View view, int position);
        }

        public void setOnClickListener(ViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }
    }
}
