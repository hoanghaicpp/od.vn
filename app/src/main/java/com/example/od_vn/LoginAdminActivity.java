package com.example.od_vn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginAdminActivity extends AppCompatActivity {
    TextView register,forgetpassword;
    FirebaseAuth auth;
    EditText loginUsername, loginPassword;
    Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        register = findViewById(R.id.register);
        loginButton = findViewById(R.id.loginButton);
        loginUsername = findViewById(R.id.userName);
        loginPassword = findViewById(R.id.password);
        forgetpassword = findViewById(R.id.fogetpassword);
        auth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validatePassword() | !validateUsername()) {}
                else checkUser( loginUsername.getText().toString(), loginPassword.getText().toString());
            }
        });
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder build = new AlertDialog.Builder(LoginAdminActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot,null);
                EditText EmailBox = dialogView.findViewById(R.id.emailBox);

                build.setView(dialogView);
                AlertDialog dialog = build.create();

                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String UserEmail = EmailBox.getText().toString();
                        if (TextUtils.isEmpty(UserEmail) && !Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()){
                            EmailBox.setError("Vui lòng nhập email đúng");
                            EmailBox.requestFocus();
                            return;
                        }
                        auth.sendPasswordResetEmail(UserEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginAdminActivity.this,"Vui lòng kiểm tra email",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                else{
                                    Toast.makeText(LoginAdminActivity.this,"Không thể gửi được, vui lòng thử lại sau",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if(dialog.getWindow()!=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginAdminActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        loginPassword.setOnTouchListener(new View.OnTouchListener() {
            final int RIGHT = 2;
            boolean passwordVisible = false;
            Drawable lockDrawable = getResources().getDrawable(R.drawable.baseline_lock_24);
            Drawable visibilityOffDrawable = getResources().getDrawable(R.drawable.baseline_visibility_off_24);
            Drawable visibilityOnDrawable = getResources().getDrawable(R.drawable.baseline_visibility_24);
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int selection = loginPassword.getSelectionEnd();
                    if (motionEvent.getRawX() >= loginPassword.getRight() - loginPassword.getCompoundDrawables()[RIGHT].getBounds().width()) {
                        if (passwordVisible) {
                            loginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(lockDrawable, null, visibilityOffDrawable, null);
                            loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            loginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(lockDrawable, null, visibilityOnDrawable, null);
                            loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        loginPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }
    public Boolean validateUsername(){
        String val = loginUsername.getText().toString();
        if(val.isEmpty()){
            loginUsername.setError("Tên đăng nhập không thể bỏ trống");
            return false;
        }
        else {
            loginUsername.setError(null);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if(val.isEmpty()){
            loginPassword.setError("Mặt khẩu không thể bỏ trống");
            return false;
        }
        else {
            loginPassword.setError(null);
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
                    if(snapshot.child(encryptedUserName).child("role").getValue(String.class).equals("admin")) {
                        loginUsername.setError(null);
                        String emailFromDB = snapshot.child(encryptedUserName).child("email").getValue(String.class);
                        loginUser(encryptedUserName, emailFromDB, useruserPassword, useruserName);
                    }
                    else {
                        Toast.makeText(LoginAdminActivity.this,"Nhân viên không thể đăng nhập ở đây!",Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    loginUsername.setError("Sai thông tin đăng nhập! Vui lòng kiểm tra lại");
                    loginUsername.requestFocus();
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
                            Intent intent = new Intent(LoginAdminActivity.this, MainActivity.class);
                            intent.putExtra("username", usernameFromDB);
                            intent.putExtra("usernameInfo",UserInfo );
                            String rnameFromDB = snapshot.child(encryptedUserName).child("rname").getValue(String.class);
                            String roleFromDB = snapshot.child(encryptedUserName).child("role").getValue(String.class);
                            intent.putExtra("rname", rnameFromDB);
                            intent.putExtra("role", roleFromDB);
                            Toast.makeText(LoginAdminActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    loginPassword.setError("Sai thông tin đăng nhập! Vui lòng kiểm tra lại");
                    loginPassword.requestFocus();
                }

            }
        });
    }

}