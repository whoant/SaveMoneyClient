package com.example.savemoney;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.savemoney.Fragment.AccountFragment;
import com.example.savemoney.Fragment.CreateFragment;
import com.example.savemoney.Fragment.HomeFragment;
import com.example.savemoney.Fragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayFragment(R.id.mnHome);

        bottomNavigationView = findViewById(R.id.bottom_navbar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);
                displayFragment(item.getItemId());
                return false;
            }
        });

    }

    private void displayFragment(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.mnHome:
                fragment = new HomeFragment();
                break;
            case R.id.mnAccount:
                fragment = new AccountFragment();
                break;
            case R.id.mnCreate:
                fragment = new CreateFragment();
                break;
            case R.id.mnSetting:
                fragment = new SettingFragment();
                break;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fmContainer, fragment);
        fragmentTransaction.commit();
    }

}