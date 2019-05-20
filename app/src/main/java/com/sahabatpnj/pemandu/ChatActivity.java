package com.sahabatpnj.pemandu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.pemandu.adapter.MessagesAdapter;
import com.sahabatpnj.pemandu.model.ChatMessage;
import com.sahabatpnj.pemandu.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private RecyclerView mChatsRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private EditText mMessageEditText;
    private ImageButton mSendImageButton;
    private DatabaseReference mMessagesDBRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mNotification;
    private FirebaseUser mUser;
    private List<ChatMessage> mMessagesList = new ArrayList<>();
    private MessagesAdapter adapter = null;

    private String mReceiverId, mSenderId;
    private String mReceiverName;
    private android.support.v7.widget.Toolbar mToolbar;
    private TextView mNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mNama = findViewById(R.id.namaUser);
        //initialize the views
        mChatsRecyclerView = (RecyclerView)findViewById(R.id.messagesRecyclerView);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendImageButton = (ImageButton)findViewById(R.id.sendMessageImagebutton);
        mChatsRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mChatsRecyclerView.setLayoutManager(mLayoutManager);

        //init Firebase
        mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("messages");
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        mNotification = FirebaseDatabase.getInstance().getReference().child("notification");

        mSenderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //get receiverId from intent
        mReceiverId = getIntent().getStringExtra("USER_ID");
        ChatMessage mChat = new ChatMessage();
        mChat.setReceiverId(mReceiverId);

        /**listen to send message imagebutton click**/
        mSendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString();
                //String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();


                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this, "You must enter a message", Toast.LENGTH_SHORT).show();
                    } else {
                    //message is entered, send
                    displayData();
                }
            }
        });


        initToolbar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
        /**Query and populate chat messages**/
        querymessagesBetweenThisUserAndClickedUser();


        /**sets title bar with recepient name**/
        queryRecipientName(mReceiverId);
    }



    private void sendMessageToFirebase(final String message, final String senderId, final String receiverId, final String image, final String name, final  String nama, final String gambar){
        mMessagesList.clear();
        final String messageId = senderId+("_")+receiverId;

        final ChatMessage newMsg = new ChatMessage(message, senderId, receiverId, messageId);
        mMessagesDBRef.child(senderId).child(receiverId).child("message").push().setValue(newMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    //error
                    Toast.makeText(ChatActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", senderId);
                    notificationData.put("message", message);
                    mMessagesDBRef.child(receiverId).child(senderId).child("message").push().setValue(newMsg);
                    mMessagesDBRef.child(receiverId).child(senderId).child("image").setValue(gambar);
                    mMessagesDBRef.child(receiverId).child(senderId).child("name").setValue(nama);
                    mMessagesDBRef.child(senderId).child(receiverId).child("image").setValue(image);
                    mMessagesDBRef.child(senderId).child(receiverId).child("name").setValue(name);
                    if (mSenderId == null) {

                        Log.d(TAG, "Value is: id null");

                    } else if (mSenderId != null){
                        mNotification.child(receiverId).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: " + mSenderId);
                                Log.d(TAG, "onSuccess: " + receiverId);
                                Log.d(TAG, "onSuccess: OMG");
                                Toast.makeText(ChatActivity.this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
                                mMessageEditText.setText(null);
                                hideSoftKeyboard();
                            }
                        });
                    }

                }
            }
        });


    }
    private void displayData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String id = user.getUid();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String nama = dataSnapshot.child(mReceiverId).child("name").getValue(String.class);
                String img = dataSnapshot.child(mReceiverId).child("image").getValue(String.class);

                String nama2 = dataSnapshot.child(mSenderId).child("name").getValue(String.class);
                String img2 = dataSnapshot.child(mSenderId).child("image").getValue(String.class);

                String message = mMessageEditText.getText().toString();

                if (message.isEmpty()||message.equals("")||message==null){

                    Log.d(TAG, "Value is: " + message);
                } else {
                    sendMessageToFirebase(message, mSenderId, mReceiverId, img, nama, nama2, img2);
                }

                Log.d(TAG, "Value is: " + nama + img);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void querymessagesBetweenThisUserAndClickedUser(){
        DatabaseReference ref = mMessagesDBRef.child(mSenderId).child(mReceiverId).child("message");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessagesList.clear();

                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    ChatMessage chatMessage = snap.getValue(ChatMessage.class);
                    if(chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && chatMessage.getReceiverId().equals(mReceiverId) || chatMessage.getSenderId().equals(mReceiverId) && chatMessage.getReceiverId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        mMessagesList.add(chatMessage);
                    }

                }

                /**populate messages**/
                populateMessagesRecyclerView();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateMessagesRecyclerView(){
        adapter = new MessagesAdapter(mMessagesList, this);
        mChatsRecyclerView.setAdapter(adapter);

    }

    private void queryRecipientName(final String receiverId){
        mUsersRef.child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User recepient = dataSnapshot.getValue(User.class);
                mReceiverName = recepient.getName();

                try {
                    mNama.setText(mReceiverName);
                    mNama.setText(mReceiverName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}