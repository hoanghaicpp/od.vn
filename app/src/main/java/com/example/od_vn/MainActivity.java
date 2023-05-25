package com.example.od_vn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById((R.id.drawer_layout));
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        replaceFragment(new HomeFragment());
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.food:
                    replaceFragment(new FoodFragment());
                    break;
                case R.id.revenue:
                    if (role.equals("admin"))
                        replaceFragment(new RevenueFragment());
                    else {
                        Toast.makeText(this, "Nhân viên không có quyền truy cập!", Toast.LENGTH_SHORT).show();
                        item.setCheckable(false);
                    }
                    break;
                case R.id.noti:
                    replaceFragment(new NofiFragment());
                    break;
            }
            return true;
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        switch(item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
                break;
            case R.id.nav_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new InformationFragment()).commit();
                break;
            case R.id.nav_qlnv:
                if (role.equals("admin"))
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new QLNVFragment()).commit();
                else{
                    Toast.makeText(this, "Nhân viên không có quyền truy cập!", Toast.LENGTH_SHORT).show();
                    item.setCheckable(false);
                }
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new AboutFragment()).commit();
                break;
            case R.id.nav_logout:
                Toast.makeText(this,"Đăng xuất",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(MainActivity.this, PreLoginActivity.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}