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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.pemandu.model.Daerah;
import com.sahabatpnj.pemandu.model.User;

public class ViewDaerahActivity extends AppCompatActivity {

    private static final String TAG = "ViewDaerahActivity";

    private ImageView mImageViewDaerah;
    private TextView mTextTitle, mTextDesc, mTextAppbar;
    private DatabaseReference mDatabase;
    private RecyclerView mListPemandu;
    private ProgressBar mProgressBar;
    private Query mQuery;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_daerah);

        Log.d(TAG, "onCreate: inisialisasi");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mImageViewDaerah = findViewById(R.id.imgViewDaerah);
        mTextDesc = findViewById(R.id.txtViewDaerahAbout);
        mTextTitle = findViewById(R.id.txtViewDaerahTitle);
        mTextAppbar = findViewById(R.id.tvViewDaerah);
        mToolbar = findViewById(R.id.toolbarViewDaerah);
        mProgressBar =findViewById(R.id.pbViewDaerah);
        mListPemandu = findViewById(R.id.rcViewDaerahListPemandu);
        mListPemandu.setHasFixedSize(true);
        mListPemandu.setLayoutManager(new LinearLayoutManager(this));

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            System.out.println("Exception occurred");
        }



        initToolbar();
        mProgressBar.setVisibility(View.INVISIBLE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Intent intent = getIntent();
                String message = intent.getStringExtra("namaDaerah");
                Daerah value = dataSnapshot.getValue(Daerah.class);
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(dataSnapshot.child("daerah").child(message).getValue(Daerah.class).getImgDaerah())
                        .into(mImageViewDaerah);
                mTextAppbar.setText(dataSnapshot.child("daerah").child(message).getValue(Daerah.class).getNamaDaerah());
                mTextTitle.setText(dataSnapshot.child("daerah").child(message).getValue(Daerah.class).getNamaDaerah());
                mTextDesc.setText(dataSnapshot.child("daerah").child(message).getValue(Daerah.class).getDescDaerah());
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

        mQuery = FirebaseDatabase.getInstance().getReference().child("users")
                .orderByChild("location").equalTo(message);
        Log.d(TAG, "onCreate: " );
        mQuery.keepSynced(true);
    }

    public void initToolbar(){
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        FirebaseRecyclerAdapter<User, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, ViewHolder>
                (User.class, R.layout.layout_list_pemandu, ViewHolder.class, mQuery) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, User model, int position) {
                mProgressBar.setVisibility(View.VISIBLE);
                viewHolder.setList(model.getName(),model.getImage());
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String key = getRef(position).getKey().toString();
                        Intent intent = new Intent(ViewDaerahActivity.this, ViewProfilPemanduActivity.class);
                        intent.putExtra("keyPemandu",key);
                        startActivity(intent);
                    }
                });
                return viewHolder;
            }
        };
        mListPemandu.setAdapter(firebaseRecyclerAdapter);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

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
        public void setList(String namaUser, String imgUser){
            TextView namaUserText = mView.findViewById(R.id.textListPemanduNama);
            ImageView imgUserText = mView.findViewById(R.id.imageListPemandu);
            namaUserText.setText(namaUser);

            Glide.with(mView).asBitmap().load(imgUser).into(imgUserText);
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


