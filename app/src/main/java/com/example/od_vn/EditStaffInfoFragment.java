package com.example.od_vn;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class EditStaffInfoFragment extends Fragment {
    String usernameFromDB;
    String EmailBefore, EmailAfter;
    String emailTemp;
    FirebaseAuth auth;
    Button editProfileBtn, editPasswordBtn, cancelBtn;
    UserValidator userValidator = new UserValidator();
    EditText editProfileName, editProfileEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_staff_info, container, false);
        editProfileName = view.findViewById(R.id.Edit_profileName);
        editProfileEmail = view.findViewById(R.id.Edit_profileEmail);
        editProfileBtn = view.findViewById(R.id.Edit_profileButton);
        editPasswordBtn = view.findViewById(R.id.Edit_passwordButton);
        cancelBtn = view.findViewById(R.id.edit_cancelButton);
        // show data
        Intent intent = getActivity().getIntent();
        usernameFromDB = intent.getStringExtra("username");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nameFromDB = snapshot.child(usernameFromDB).child("name").getValue(String.class);
                String emailFromDB = snapshot.child(usernameFromDB).child("email").getValue(String.class);
                editProfileName.setText(nameFromDB);
                editProfileEmail.setText(emailFromDB);
                emailTemp = emailFromDB;
                EmailBefore = emailFromDB;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //
        auth = FirebaseAuth.getInstance();

        return view;
    }



    public Boolean validatePassword(String check, EditText textbox) {
        String val = check;
        if (val.isEmpty()) {
            textbox.setError("Mật khẩu không thể bỏ trống");
            return false;
        } else {
            if(userValidator.validatePassword(val)){
                textbox.setError(null);
                return true;
            }
            else {
                textbox.setError("Mặt khẩu không hợp lệ\nYêu cầu về mật khẩu:\n" +
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
        String val = editProfileEmail.getText().toString();
        if (val.isEmpty()) {
            editProfileEmail.setError("Mail không thể bỏ trống thể bỏ trống");
            return false;
        } else {
            if(userValidator.validateEmail(val)){
                editProfileEmail.setError(null);
                return true;
            }
            else {
                editProfileEmail.setError("Mail không hợp lệ");
                return false;
            }
        }
    }
    public Boolean validateName() {
        String val = editProfileName.getText().toString();
        if (val.isEmpty()) {
            editProfileName.setError("Tên không thể bỏ trống");
            return false;
        } else {
            editProfileName.setError(null);
            return true;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmailAfter = editProfileEmail.getText().toString();
                if(!validateEmail() || !validateName() ) {}
                else {
                    String userForEdit = usernameFromDB;
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("users");
                    DatabaseReference Auth = FirebaseDatabase.getInstance().getReference("Auth");
                    Query checkUserEmail = reference1.orderByChild("email").equalTo(editProfileEmail.getText().toString().trim());
                    checkUserEmail.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotE) {
                            if (!snapshotE.exists() || EmailBefore.equals(EmailAfter)){
                                reference.child(userForEdit).child("name").setValue(editProfileName.getText().toString());
                                reference.child(userForEdit).child("email").setValue(editProfileEmail.getText().toString());
                                Auth.child(editProfileEmail.getText().toString().replaceAll("@.*", "")).setValue(userForEdit);
                                auth.getCurrentUser().updateEmail(editProfileEmail.getText().toString());
                                Toast.makeText(EditStaffInfoFragment.this.getContext(),"Thay đổi thành công",Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new InformationFragment()).commit();
                            }
                            else{
                                editProfileEmail.setError("Email đã tồn tại!");
                                editProfileEmail.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        editPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder build = new AlertDialog.Builder(EditStaffInfoFragment.this.getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm,null);
                EditText ConfirmPasswordBox = dialogView.findViewById(R.id.confirm_passwordBox);

                build.setView(dialogView);
                AlertDialog dialog = build.create();
                dialogView.findViewById(R.id.confirm_btnConfirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String ConfirmPassword = ConfirmPasswordBox.getText().toString();
                        if (TextUtils.isEmpty(ConfirmPassword)){
                            ConfirmPasswordBox.setError("Vui lòng nhập mặt khẩu");
                            ConfirmPasswordBox.requestFocus();
                            return;
                        }
                        auth.signOut();
                        auth.signInWithEmailAndPassword(emailTemp,ConfirmPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(EditStaffInfoFragment.this.getContext(),"Xác nhận thành công",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    AlertDialog.Builder build = new AlertDialog.Builder(EditStaffInfoFragment.this.getContext());
                                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_changepassword,null);
                                    EditText ChangePasswordBox = dialogView.findViewById(R.id.change_passwordBox);
                                    build.setView(dialogView);
                                    AlertDialog dialog = build.create();
                                    dialogView.findViewById(R.id.change_btnConfirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(!validatePassword(ChangePasswordBox.getText().toString(),ChangePasswordBox)){

                                            }
                                            else {
                                                auth.getCurrentUser().updatePassword(ChangePasswordBox.getText().toString());
                                                dialog.dismiss();
                                                Toast.makeText(EditStaffInfoFragment.this.getContext(),"Thay đổi thành công",Toast.LENGTH_SHORT).show();
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new InformationFragment()).commit();
                                            }


                                        }


                                    });
                                    dialogView.findViewById((R.id.change_btnCancel)).setOnClickListener(new View.OnClickListener() {
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
                                else{
                                    ConfirmPasswordBox.setError("Sai mật khẩu! Vui lòng kiểm tra lại");
                                }
                            }
                        });

                    }
                });
                dialogView.findViewById(R.id.confirm_btnCancel).setOnClickListener(new View.OnClickListener() {
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
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new InformationFragment()).commit();
            }
        });
    }


}