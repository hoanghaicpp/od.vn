package com.example.od_vn;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class UploadStaffFragment extends Fragment {

    UserValidator userValidator = new UserValidator();
    Button addStaffBtn, cancelStaffBtn;
    EditText uploadStaffName, uploadStaffusername, uploadStaffPassword, uploadStaffEmail;
    List<StaffData> staffList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    int Max = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_staff, container, false);
        staffList = new ArrayList<>();
        addStaffBtn = view.findViewById(R.id.addStaffBtn);
        cancelStaffBtn = view.findViewById(R.id.cancelStaffBtn);
        uploadStaffName = view.findViewById(R.id.uploadStaffName);
        uploadStaffEmail = view.findViewById(R.id.uploadStaffEmail);
        uploadStaffusername = view.findViewById(R.id.uploadStaffUserName);
        uploadStaffPassword = view.findViewById(R.id.uploadStaffPassword);
        Intent intent = getActivity().getIntent();
        String AdminUserName = intent.getStringExtra("username");
        databaseReference = FirebaseDatabase.getInstance().getReference("staff").child(AdminUserName);
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    StaffData data = dataSnapshot.getValue(StaffData.class);
                    if (Max < data.getId()) Max = data.getId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        uploadStaffPassword.setOnTouchListener(new View.OnTouchListener() {
            final int RIGHT = 2;
            boolean passwordVisible = false;
            Drawable lockDrawable = getResources().getDrawable(R.drawable.baseline_lock_24);
            Drawable visibilityOffDrawable = getResources().getDrawable(R.drawable.baseline_visibility_off_24);
            Drawable visibilityOnDrawable = getResources().getDrawable(R.drawable.baseline_visibility_24);
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int selection = uploadStaffPassword.getSelectionEnd();
                    if (motionEvent.getRawX() >= uploadStaffPassword.getRight() - uploadStaffPassword.getCompoundDrawables()[RIGHT].getBounds().width()) {
                        if (passwordVisible) {
                            uploadStaffPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(lockDrawable, null, visibilityOffDrawable, null);
                            uploadStaffPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            uploadStaffPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(lockDrawable, null, visibilityOnDrawable, null);
                            uploadStaffPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        uploadStaffPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateName() || !validateUsername() || !validatePassword()) {
                } else checkUser();
            }
        });
        cancelStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new QLNVFragment()).commit();
            }
        });
    }

    public boolean validateName() {
        String tmp = uploadStaffName.getText().toString();
        if (tmp.isEmpty()) {
            uploadStaffName.setError("Tên không thể để trống");
            return false;
        }
        return true;
    }

    public boolean validateUsername() {
        String tmp = uploadStaffusername.getText().toString();
        if (tmp.isEmpty()) {
            uploadStaffusername.setError("Tên đăng nhập không thể bỏ trống");
            return false;
        }
        return true;
    }

    public Boolean validatePassword() {
        String tmp = uploadStaffPassword.getText().toString();
        if (tmp.isEmpty()) {
            uploadStaffPassword.setError("Mật khẩu không thể bỏ trống");
            return false;
        } else {
            if (userValidator.validatePassword(tmp)) {
                uploadStaffPassword.setError(null);
                return true;
            } else {
                uploadStaffPassword.setError("Mặt khẩu không hợp lệ\nYêu cầu về mật khẩu:\n" +
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
    public void checkUser() {
        Intent intent = getActivity().getIntent();
        String StaffUserName = uploadStaffusername.getText().toString();
        String AdminUserName = intent.getStringExtra("username");
        String RnameFromDB = intent.getStringExtra("rname");
        String encryptedUserName = encryptToSHA256(StaffUserName);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("staff");
        Query checkStaffusername = reference.orderByChild("username").equalTo(encryptedUserName);
        checkStaffusername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) uploadStaffusername.setError("Tài khoản đã tồn tại");
                else {
                    String StaffEmail = uploadStaffEmail.getText().toString();
                    Query checkStaffEmail = reference.orderByChild("email").equalTo(StaffEmail);
                    checkStaffEmail.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            if (snapshot1.exists())
                                uploadStaffEmail.setError("Mail đã tồn tại");
                            else {
                                uploadStaffEmail.setError(null);
                                String StaffName = uploadStaffName.getText().toString();
                                String StaffPassword = uploadStaffPassword.getText().toString();
                                StaffData staffData = new StaffData(StaffName, StaffEmail, encryptedUserName, "staff", RnameFromDB, "");
                                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("users");
                                if (Max != 0) staffData.setId(Max + 1);
                                reference1.child(AdminUserName).child(encryptedUserName).setValue(staffData);
                                reference2.child(encryptedUserName).setValue(staffData);
                                signupUser(StaffEmail, StaffPassword);
                                Toast.makeText(getActivity(), "Đăng kí thành công", Toast.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new QLNVFragment()).commit();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void signupUser(String Useremail, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(Useremail, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                }
            }
        });
    }
}
