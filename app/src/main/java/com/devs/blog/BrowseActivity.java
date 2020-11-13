package com.devs.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.devs.blog.fragments.HomeFragment;
import com.devs.blog.fragments.NotificationFragment;
import com.devs.blog.fragments.ProfileFragment;
import com.devs.blog.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BrowseActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;

                    case R.id.nav_add:
                        selectorFragment = null;
                        startActivity(new Intent(BrowseActivity.this, PostActivity.class));

                    case R.id.nav_fav:
                        selectorFragment= new NotificationFragment();
                        break;

                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        break;
                }
                if (selectorFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }

                return true;
            }
        });

        Bundle intent= getIntent().getExtras();
        if (intent != null){
            String profileID= intent.getString("authorID");

            //because we can only transfer a data from an activity to a fragment of the same activity only via shared preferences

            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileID", profileID).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

    }
}