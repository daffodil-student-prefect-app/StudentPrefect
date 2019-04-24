package com.spicyict.user.studentprefect;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.spicyict.user.studentprefect.fragment.HomeFragment;
import com.spicyict.user.studentprefect.fragment.MoreFragment;
import com.spicyict.user.studentprefect.fragment.NotificationFragment;
import com.spicyict.user.studentprefect.fragment.ProfileFragment;
import com.spicyict.user.studentprefect.fragment.SearchFragment;
import com.spicyict.user.studentprefect.login.LoginActivity;
import com.spicyict.user.studentprefect.login.SetupActivity;
import com.spicyict.user.studentprefect.model.User;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mainBottomNav;
    private Fragment selectedFragment = null;
    private HomeFragment homeFragment;
    private MoreFragment moreFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;

    private ImageButton addNewPostButton;
    private String postSubject;
    private String postDescription;

    boolean doubleBackToExitPressedOnce = false;

    private CircleImageView profileImage;
    private TextView profileName;
    private String[] education;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mainBottomNav = findViewById(R.id.bottom_navigation);
        addNewPostButton = findViewById(R.id.add_post_btn);
        if (mAuth.getCurrentUser() != null){

            homeFragment = new HomeFragment();
            moreFragment = new MoreFragment();
            notificationFragment = new NotificationFragment();
            profileFragment = new ProfileFragment();
            searchFragment = new SearchFragment();

            replaceFragment(homeFragment);

            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            replaceFragment(homeFragment);
                            addNewPostButton.setVisibility(View.VISIBLE);
                            return true;
                        case R.id.nav_notification:
                            replaceFragment(notificationFragment);
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            return true;
                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            replaceFragment(profileFragment);
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            return true;
                        case R.id.nav_search:
                            replaceFragment(searchFragment);
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            return true;
                        case R.id.nav_more:
                            replaceFragment(moreFragment);
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            return true;
                            default:
                                return false;
                    }
                }
            });
        }


       /* mainBottomNav.setOnNavigationItemSelectedListener(listener);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                new HomeFragment()).commit();*/

       /* Intent i = getIntent();
        String fragmentName = i.getStringExtra("Profilefragment");
        String forum = "forum";
        Log.e("Test1", "Test1" + fragmentName);
        if (fragmentName != null && fragmentName.equals(forum)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        }*/

        // publisher id comes from comment section in comment adapter
      /*  Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }*/


        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_new_post, null);

                final TextView addPostClose = mView.findViewById(R.id.add_new_post_close);
                profileImage = mView.findViewById(R.id.add_new_post_profileImageView);
                profileName = mView.findViewById(R.id.add_new_profileNameTextView);
                final TextView postStatus = mView.findViewById(R.id.add_new_post_sendImageView);
                final EditText addPostET = mView.findViewById(R.id.add_new_post_postEditText);
                final AppCompatAutoCompleteTextView addChooseSubject = mView.findViewById(R.id.add_new_post_addChooseSubjectEditText);

                education = getResources().getStringArray(R.array.education);
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,education);
                addChooseSubject.setAdapter(adapter);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                loadUserInfo();
                dialog.show();


                //close the alertDialog
                addPostClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //Post Status
                postStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postSubject = addChooseSubject.getText().toString();
                        postDescription = addPostET.getText().toString().trim();
                        if (TextUtils.isEmpty(postDescription)) {
                            Toast.makeText(MainActivity.this,
                                    "write something about what help you needed", Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(postSubject)){
                            Toast.makeText(MainActivity.this, "this problem is about which course !", Toast.LENGTH_SHORT).show();
                        }else  {
                            uploadStatus(postSubject,postDescription);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Post Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser == null){
            sendUserToLoginActivity();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }
   /* private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            addNewPostButton.setVisibility(View.VISIBLE);
                            break;
                        case R.id.nav_notification:
                            selectedFragment = new NotificationFragment();
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.nav_more:
                            selectedFragment = new MoreFragment();
                            addNewPostButton.setVisibility(View.INVISIBLE);
                            break;
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };*/

    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user.getImageurl().equals("")) {
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MainActivity.this).load(user.getImageurl()).into(profileImage);
                }

                profileName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadStatus(String postSubject,String postDescription) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        String postId = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postId", postId);
        hashMap.put("postDescription", postDescription);
        hashMap.put("postSubject",postSubject);
        hashMap.put("postPublisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("postPhoneNumber","01624826221");
        hashMap.put("time",ServerValue.TIMESTAMP);

        reference.child(postId).setValue(hashMap);

    }



    private void sendUserToLoginActivity() {
        Toast.makeText(this, "send User To log in activity", Toast.LENGTH_SHORT).show();
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

    //online
    private void status(boolean status){
        reference = FirebaseDatabase.getInstance().getReference("Status").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(firebaseUser != null) {
            status(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseUser != null) {
            status(false);
        }

    }


}
