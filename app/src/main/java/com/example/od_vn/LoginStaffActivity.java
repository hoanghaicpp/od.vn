package com.example.od_vn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginStaffActivity extends AppCompatActivity {

    EditText LoginUserName, LoginPassword;
    Button LoginButton;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_staff);
        LoginUserName = findViewById(R.id.userName);
        LoginPassword = findViewById(R.id.password);
        LoginButton = findViewById(R.id.loginButton);
        auth = FirebaseAuth.getInstance();
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validatePassword() | !validateUsername()) {}
                else checkUser(LoginUserName.getText().toString(), LoginPassword.getText().toString());
            }
        });
        LoginPassword.setOnTouchListener(new View.OnTouchListener() {
            final int RIGHT = 2;
            boolean passwordVisible = false;
            Drawable lockDrawable = getResources().getDrawable(R.drawable.baseline_lock_24);
            Drawable visibilityOffDrawable = getResources().getDrawable(R.drawable.baseline_visibility_off_24);
            Drawable visibilityOnDrawable = getResources().getDrawable(R.drawable.baseline_visibility_24);
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int selection = LoginPassword.getSelectionEnd();
                    if (motionEvent.getRawX() >= LoginPassword.getRight() - LoginPassword.getCompoundDrawables()[RIGHT].getBounds().width()) {
                        if (passwordVisible) {
                            LoginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(lockDrawable, null, visibilityOffDrawable, null);
                            LoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            LoginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(lockDrawable, null, visibilityOnDrawable, null);
                            LoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        LoginPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }
    public Boolean validateUsername(){
        String val = LoginUserName.getText().toString();
        if(val.isEmpty()){
            LoginUserName.setError("Tên đăng nhập không thể bỏ trống");
            return false;
        }
        else {
            LoginUserName.setError(null);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val = LoginPassword.getText().toString();
        if(val.isEmpty()){
            LoginPassword.setError("Mặt khẩu không thể bỏ trống");
            return false;
        }
        else {
            LoginPassword.setError(null);
            return true;
        }
    }
    public static String encryptToSHA256(String stringToEncrypt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(stringToEncrypt.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public void checkUser(String useruserName, String useruserPassword){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        String encryptedUserName = encryptToSHA256(useruserName);
        Query checkUserDatabase = reference.orderByChild("username").equalTo(encryptedUserName);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child(encryptedUserName).child("role").getValue(String.class).equals("staff")) {
                        LoginUserName.setError(null);
                        String emailFromDB = snapshot.child(encryptedUserName).child("email").getValue(String.class);
                        loginUser(encryptedUserName, emailFromDB, useruserPassword, useruserName);
                    }
                    else {
                        Toast.makeText(LoginStaffActivity.this,"Admin không thể đăng nhập ở đây!",Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    LoginUserName.setError("Sai thông tin đăng nhập! Vui lòng kiểm tra lại");
                    LoginUserName.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void loginUser(String encryptedUserName ,String EmailFromDB, String PasswordFromDB, String UserInfo) {
        auth.signInWithEmailAndPassword(EmailFromDB, PasswordFromDB).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String usernameFromDB = snapshot.child(encryptedUserName).child("username").getValue(String.class);
                            Intent intent = new Intent(LoginStaffActivity.this, MainActivity.class);
                            intent.putExtra("username", usernameFromDB);
                            intent.putExtra("usernameInfo", UserInfo);

                            String roleFromDB = snapshot.child(encryptedUserName).child("role").getValue(String.class);
                            String evaluateFromDB = snapshot.child(encryptedUserName).child("evaluate").getValue(String.class);
                            String rnameFromDB = snapshot.child(encryptedUserName).child("rname").getValue(String.class);
                            intent.putExtra("rname", rnameFromDB);
                            intent.putExtra("role", roleFromDB);
                            intent.putExtra("evaluate", evaluateFromDB);

                            Toast.makeText(LoginStaffActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    LoginPassword.setError("Sai thông tin đăng nhập! Vui lòng kiểm tra lại");
                    LoginPassword.requestFocus();
                }

            }
        });
    }
}