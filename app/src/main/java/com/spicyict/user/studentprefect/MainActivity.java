package com.spicyict.user.studentprefect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spicyict.user.studentprefect.fragment.HomeFragment;
import com.spicyict.user.studentprefect.fragment.MoreFragment;
import com.spicyict.user.studentprefect.fragment.NotificationFragment;
import com.spicyict.user.studentprefect.fragment.ProfileFragment;
import com.spicyict.user.studentprefect.login.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = new HomeFragment();
    private ImageButton addNewPostButton;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        addNewPostButton = findViewById(R.id.add_post_btn);

        //Edited by Jim - start


        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_new_post, null);
                final EditText addChooseSubjectEditText= mView.findViewById(R.id.add_new_post_addChooseSubjectEditText);
                final TextView addLocationTextView=  mView.findViewById(R.id.add_new_post_addLocationTextView);
                final ImageView sendImageView=  mView.findViewById(R.id.add_new_post_sendImageView);
                final EditText postEditText=  mView.findViewById(R.id.add_new_post_postEditText);
                final TextView profileNameTextView= mView.findViewById(R.id.add_new_profileNameTextView);
                final CircleImageView profileImageView= mView.findViewById(R.id.add_new_post_profileImageView);


             /*   mBuilder.setPositiveButton(AddNewPost, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }); */

                mBuilder.setNegativeButton( "Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                sendImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       /* if(!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this,
                                    R.string.success_login_msg,
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(MainActivity.this,
                                    R.string.error_login_msg,
                                    Toast.LENGTH_SHORT).show();
                        } */
                        Toast.makeText(MainActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        //Edited by Jim - end


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

       /* addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostFragment()).commit();
                addNewPostButton.setVisibility(View.INVISIBLE);
            }
        });*/



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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
