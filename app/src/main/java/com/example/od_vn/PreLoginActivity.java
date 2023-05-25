package com.example.od_vn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class PreLoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button loginADButton;
    Button loginStaffButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prelogin);
        loginADButton = findViewById(R.id.loginADButton);
        loginStaffButton = findViewById(R.id.loginStaffButton);
        loginADButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreLoginActivity.this, LoginAdminActivity.class);
                startActivity(intent);
            }
        });
        loginStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreLoginActivity.this, LoginStaffActivity.class);
                startActivity(intent);
            }
        });
    }
}