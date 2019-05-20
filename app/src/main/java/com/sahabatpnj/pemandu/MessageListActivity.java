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
import com.sahabatpnj.pemandu.model.ChatMessage;
import com.sahabatpnj.pemandu.model.User;

import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {

    private static final String TAG = "MessageListActivity";
    private RecyclerView mReacyclerUser;
    private android.support.v7.widget.Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private Query mQuery;
    private FirebaseAuth mAuth;
    private List<ChatMessage> mMessagesList = new ArrayList<>();
    private String msgList;
    private TextView mEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mProgressBar = findViewById(R.id.progressBarTripList);
        mReacyclerUser = findViewById(R.id.listTripList);
        mToolbar = findViewById(R.id.appbarTripList);
        mEmpty = findViewById(R.id.textMessageListEmpty);

        initToolbar();

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            System.out.println("Exception occurred");
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        //Intent intent = getIntent();
        //String message = intent.getStringExtra("keyPemandu");
        //String ref = rootRef.child("users");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("messages");
        String key = userRef.getKey();
        mQuery = userRef.child(userId).orderByChild("name");
        mQuery.keepSynced(true);

       Log.d(TAG, "onCreate: ref " + mQuery);

        setSupportActionBar(mToolbar);


        mReacyclerUser = findViewById(R.id.listTripList);
        mReacyclerUser.setHasFixedSize(true);
        mReacyclerUser.setLayoutManager(new LinearLayoutManager(this));
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

    private void initRecyclerView() {

        Log.d(TAG, "onStart: Menambahkan Data ke RecyclerView");

        FirebaseRecyclerAdapter<User, MessageListActivity.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, MessageListActivity.ViewHolder>
                (User.class, R.layout.layout_list_pemandu, MessageListActivity.ViewHolder.class, mQuery) {
            @Override
            protected void populateViewHolder(MessageListActivity.ViewHolder viewHolder, User model, int position) {
                mProgressBar.setVisibility(View.VISIBLE);
                viewHolder.setNamaDestinasi(model.getName(), model.getImage());
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public MessageListActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final MessageListActivity.ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new MessageListActivity.ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String key = getRef(position).getKey().toString();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String id = user.getUid();
                        if (id.equals(key)){
                            Toast.makeText(getApplicationContext(),"You Can't Chat With Yourself", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MessageListActivity.this, ChatActivity.class);
                            intent.putExtra("USER_ID",key);
                            startActivity(intent);
                            Log.d(TAG, "onClick: chat " + key);
                        }
                    }
                });
                return viewHolder;
            }
        };
        mReacyclerUser.setAdapter(firebaseRecyclerAdapter);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;

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

        private MessageListActivity.ViewHolder.ClickListener mClickListener;

        public interface ClickListener {
            void onItemClick(View view, int position);
        }

        public void setOnClickListener(MessageListActivity.ViewHolder.ClickListener clickListener) {
            mClickListener = clickListener;
        }
    }
}
