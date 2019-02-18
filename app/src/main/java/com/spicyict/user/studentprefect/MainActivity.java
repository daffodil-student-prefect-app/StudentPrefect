package com.spicyict.user.studentprefect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spicyict.user.studentprefect.fragment.HomeFragment;
import com.spicyict.user.studentprefect.fragment.MoreFragment;
import com.spicyict.user.studentprefect.fragment.NotificationFragment;
import com.spicyict.user.studentprefect.fragment.ProfileFragment;
import com.spicyict.user.studentprefect.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = new HomeFragment();
    private ImageButton addNewPostButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        addNewPostButton = findViewById(R.id.add_post_btn);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Add a new post!", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            addNewPostButton.setVisibility(View.VISIBLE);
                            break;
                        case R.id.nav_notification:
                            selectedFragment = new NotificationFragment();
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.nav_more:
                            selectedFragment = new MoreFragment();
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            sendUserToLoginActivity();
        }
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
