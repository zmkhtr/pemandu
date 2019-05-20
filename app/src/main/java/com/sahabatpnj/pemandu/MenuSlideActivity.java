package com.sahabatpnj.pemandu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.pemandu.account.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuSlideActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MenuSlideActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Context mContext;
    private Button mButtonMain;
    private TextView mTextName, mTextEmail;
    private CircleImageView mImageNavbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_slide);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "onCreate: start");


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        mButtonMain = findViewById(R.id.button_find_main);

        invalidateOptionsMenu();
        setupFirebaseListener();

        View hView =  navigationView.getHeaderView(0);

        mTextName = hView.findViewById(R.id.TextNavbarNama);
        mTextEmail = hView.findViewById(R.id.TextNavbarEmail);
        mImageNavbar = hView.findViewById(R.id.ImageNavbarFoto);

        mButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuSlideActivity.this, TripDestinationListActivity.class);
                startActivity(intent);
            }
        });

        if (mUser == null){
            mTextName.setText("Login first");
            mTextEmail.setText("");
        } else if (mUser != null){
           displayData();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        NavigationView navigationView = findViewById(R.id.nav_view);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        View hView =  navigationView.getHeaderView(0);

        mTextName = hView.findViewById(R.id.TextNavbarNama);
        mTextEmail = hView.findViewById(R.id.TextNavbarEmail);
        mImageNavbar = hView.findViewById(R.id.ImageNavbarFoto);

        Menu nv = navigationView.getMenu();
        MenuItem myItem = nv.findItem(R.id.nav_login);

        if (id == R.id.nav_profil) {
            if (currentUser == null) {

                Toast.makeText(getApplicationContext(), "Please login first !", Toast.LENGTH_SHORT).show();
                //Snackbar.make(findViewById(R.id.drawer_layout), "Please login first", Snackbar.LENGTH_SHORT).show();
            } else if (currentUser != null) {
                Intent intent = new Intent(MenuSlideActivity.this, ProfileViewActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_message) {
            if (currentUser == null) {
                Toast.makeText(getApplicationContext(), "Please login first !", Toast.LENGTH_SHORT).show();
                //Snackbar.make(findViewById(R.id.drawer_layout), "Please login first", Snackbar.LENGTH_SHORT).show();
            } else if (currentUser != null) {
                Intent intent = new Intent(MenuSlideActivity.this, MessageListActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_login) {
            if (currentUser == null) {
                Intent intent = new Intent(MenuSlideActivity.this, LoginActivity.class);
                startActivity(intent);

            } else if (currentUser != null) {
                FirebaseAuth.getInstance().signOut();
                mTextName.setText("Login First");
                mTextEmail.setText("");
                Glide.with(getApplicationContext()).asBitmap().load(R.drawable.ic_person).into(mImageNavbar);
                Snackbar.make(findViewById(R.id.drawer_layout), "sign out successfull, see ya ^_^", Snackbar.LENGTH_SHORT).show();
            }
        }
        navigationView.setNavigationItemSelectedListener(this);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setupFirebaseListener() {
        Log.d(TAG, "setupFirebaseListener: setting up the auth state listener.");
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
//                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//                    Menu menu = navigationView.getMenu();
//                    MenuItem myItem = menu.findItem(R.id.nav_login);
//                    myItem.setTitle("sign in");
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    Menu menu = navigationView.getMenu();
                    MenuItem myItem = menu.findItem(R.id.nav_login);
                    myItem.setTitle("sign out");
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    Menu menu = navigationView.getMenu();
                    MenuItem myItem = menu.findItem(R.id.nav_login);
                    myItem.setTitle("sign in");
                }
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    private void displayData() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        mTextName = hView.findViewById(R.id.TextNavbarNama);
        mTextEmail = hView.findViewById(R.id.TextNavbarEmail);
        mImageNavbar = hView.findViewById(R.id.ImageNavbarFoto);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String nama = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                    String img = dataSnapshot.child(user.getUid()).child("image").getValue(String.class);
                    String email = dataSnapshot.child(user.getUid()).child("email").getValue(String.class);

                    mTextName.setText(nama);
                    mTextEmail.setText(email);
                    Log.d(TAG, "Value is: " + nama + email + img);
                    Glide.with(getApplicationContext()).asBitmap().load(img).into(mImageNavbar);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());

            }
        });
    }
}
