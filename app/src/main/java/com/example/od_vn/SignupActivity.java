package com.example.od_vn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignupActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    UserValidator userValidator = new UserValidator();

    Boolean check = false;
    EditText signupName, signupRname, signupEmail, signupUsername, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference1;
    CheckBox checkBox;
    GoogleApiClient googleApiClient;
    String SiteKey = "6LdVjTgmAAAAAFFYaWwSZbP8ePL-cCu1jWAEYTfR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        signupName = findViewById(R.id.signup_Name);
        signupEmail = findViewById(R.id.signup_Mail);
        signupRname = findViewById(R.id.signup_Rname);
        signupUsername = findViewById(R.id.signup_userName);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signupButton);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        checkBox = findViewById(R.id.checkBox);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateName() | !validateUsername() | !validatePassword() | !validateEmail() | !validateRname()) {

                } else {
                    checkUser();
                }
            }


        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, PreLoginActivity.class);
                startActivity(intent);
            }
        });
        signupPassword.setOnTouchListener(new View.OnTouchListener() {
            final int RIGHT = 2;
            boolean passwordVisible = false;
            Drawable lockDrawable = getResources().getDrawable(R.drawable.baseline_lock_24);
            Drawable visibilityOffDrawable = getResources().getDrawable(R.drawable.baseline_visibility_off_24);
            Drawable visibilityOnDrawable = getResources().getDrawable(R.drawable.baseline_visibility_24);
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int selection = signupPassword.getSelectionEnd();
                    if (motionEvent.getRawX() >= signupPassword.getRight() - signupPassword.getCompoundDrawables()[RIGHT].getBounds().width()) {
                        if (passwordVisible) {
                            signupPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(lockDrawable, null, visibilityOffDrawable, null);
                            signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            signupPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(lockDrawable, null, visibilityOnDrawable, null);
                            signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        signupPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        //CAPTCHA
        signupButton.setEnabled(false);
        googleApiClient = new GoogleApiClient.Builder(this).addApi(SafetyNet.API)
                .addConnectionCallbacks(SignupActivity.this).build();
        googleApiClient.connect();
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    SafetyNet.SafetyNetApi.verifyWithRecaptcha(googleApiClient, SiteKey)
                            .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                                @Override
                                public void onResult(@NonNull SafetyNetApi.RecaptchaTokenResult recaptchaTokenResult) {
                                    Status status = recaptchaTokenResult.getStatus();
                                    if (status != null && status.isSuccess()) {
                                        Toast.makeText(SignupActivity.this, "Xác nhận thành công", Toast.LENGTH_SHORT).show();
                                        checkBox.setTextColor(Color.BLUE);
                                        signupButton.setEnabled(true); // Enable the signupButton
                                    }
                                    else {
                                        Toast.makeText(SignupActivity.this, "Xác nhận không thành công", Toast.LENGTH_SHORT).show();
                                        checkBox.setTextColor(Color.RED);
                                        signupButton.setEnabled(false); // Disable the signupButton
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(SignupActivity.this, "Xác nhận không thành công", Toast.LENGTH_SHORT).show();
                    checkBox.setTextColor(Color.RED);
                    signupButton.setEnabled(false); // Disable the signupButton
                }
            }
        });
    //
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

    public Boolean validateUsername() {
        String val = signupUsername.getText().toString();
        if (val.isEmpty()) {
            signupUsername.setError("Tên đăng nhập không thể bỏ trống");
            return false;
        } else {
            if(userValidator.validateUsername(val)){
                signupUsername.setError(null);
                return true;
            }
            else {
                signupUsername.setError("Tên đăng nhập không hợp lệ");
                return false;

            }
        }
    }

    public Boolean validatePassword() {
        String val = signupPassword.getText().toString();
        if (val.isEmpty()) {
            signupPassword.setError("Mật khẩu không thể bỏ trống");
            return false;
        } else {
            if(userValidator.validatePassword(val)){
                signupPassword.setError(null);
                return true;
            }
            else {
                signupPassword.setError("Mặt khẩu không hợp lệ\nYêu cầu về mật khẩu:\n" +
                        "Ít nhất một chữ số\n" +
                        "Ít nhất một chữ thường\n" +
                        "Ít nhất một chữ hoa\n" +
                        "Ít nhất một ký tự đặc biệt (@#$%^&+=)\n" +
                        "Không có khoảng trắng\n" +
                        "Độ dài từ 8 đến 20 ký tự");
                return false;

            }
        }
    }

    public Boolean validateEmail() {
        String val = signupEmail.getText().toString();
        if (val.isEmpty()) {
            signupEmail.setError("Mail không thể bỏ trống thể bỏ trống");
            return false;
        } else {
            if(userValidator.validateEmail(val)){
                signupEmail.setError(null);
                return true;
            }
            else {
                signupEmail.setError("Mail không hợp lệ");
                return false;
            }
        }
    }

    public Boolean validateRname() {
        String val = signupRname.getText().toString();
        if (val.isEmpty()) {
            signupRname.setError("Tên nhà hàng không thể bỏ trống");
            return false;
        } else {
            signupRname.setError(null);
            return true;
        }
    }

    public Boolean validateName() {
        String val = signupName.getText().toString();
        if (val.isEmpty()) {
            signupName.setError("Tên không thể bỏ trống");
            return false;
        } else {
            signupName.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String useruserName = signupUsername.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        String encryptedUserName = encryptToSHA256(useruserName);
        Query checkUserDatabase = reference.orderByChild("username").equalTo(encryptedUserName);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    signupUsername.setError(null);
                    String userMail = signupEmail.getText().toString().trim();
                    Query checkUserMailDatabase = reference.orderByChild("email").equalTo(userMail);
                    checkUserMailDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotM) {
                            if(!snapshotM.exists()){
                                String rname = signupRname.getText().toString();
                                Query checkRnameDatabase = reference.orderByChild("rname").equalTo(rname);
                                checkRnameDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshotR) {
                                        if (!snapshotR.exists()) {
                                            signupEmail.setError(null);
                                            database = FirebaseDatabase.getInstance();
                                            reference1 = database.getReference("users");
                                            String name = signupName.getText().toString();
                                            String email = signupEmail.getText().toString();
                                            String password = signupPassword.getText().toString();
                                            AdminData adminData = new AdminData(name, email, encryptedUserName, rname, "admin");
                                            signupUser(email, password);
                                            reference.child(encryptedUserName).setValue(adminData);
                                            Toast.makeText(SignupActivity.this, "Đăng kí thành công", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(SignupActivity.this, PreLoginActivity.class);
                                            startActivity(intent);
                                        }
                                        else{
                                            signupRname.setError("Tên nhà hàng đã tồn tại!");
                                            signupRname.requestFocus();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else{
                                signupEmail.setError("Mail đã tồn tại!");
                                signupEmail.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    signupUsername.setError("Tài khoản đăng nhập đã tồn tại!");
                    signupUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void signupUser(String Useremail, String password){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(Useremail, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                }
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}