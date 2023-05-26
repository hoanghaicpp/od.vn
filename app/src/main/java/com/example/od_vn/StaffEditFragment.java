package com.example.od_vn;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class StaffEditFragment extends Fragment {

    Button editButton, cancleButton;
    EditText EditEvaluate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_staff_edit, container, false);
        editButton = view.findViewById(R.id.editStaffButton);
        cancleButton = view.findViewById(R.id.cancelEditStaffButton);
        EditEvaluate = view.findViewById(R.id.editEvaluate);
        Intent intent = getActivity().getIntent();
        String AdminUserName = intent.getStringExtra("username");
        String Evaluate = intent.getStringExtra("StaffEvaluate");
        EditEvaluate.setText(Evaluate);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String StaffName = intent.getStringExtra("StaffName");
                String StaffUserName = intent.getStringExtra("StaffUserName");
                String StaffEmail = intent.getStringExtra("StaffEmail");
                String StaffRname = intent.getStringExtra("StaffRname");
                StaffData data = new StaffData(StaffName, StaffEmail, StaffUserName, "staff", StaffRname, EditEvaluate.getText().toString(), intent.getStringExtra("username"));
                FirebaseDatabase.getInstance().getReference("users").child(StaffUserName).removeValue();
                FirebaseDatabase.getInstance().getReference("users").child(StaffUserName).setValue(data);
                FirebaseDatabase.getInstance().getReference("staff").child(AdminUserName).child(StaffUserName).removeValue();
                FirebaseDatabase.getInstance().getReference("staff").child(AdminUserName).child(StaffUserName).setValue(data);
                Toast.makeText(getActivity(),"Sửa thành công", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new QLNVFragment()).commit();
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new QLNVFragment()).commit();
            }
        });
        return view;
    }
}