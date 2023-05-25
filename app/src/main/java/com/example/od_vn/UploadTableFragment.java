package com.example.od_vn;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UploadTableFragment extends Fragment {

    Button addBtn, cancelBtn;
    EditText tableId;
    Intent intent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_table, container, false);
        intent = getActivity().getIntent();
        addBtn = (Button) view.findViewById(R.id.addTableBtn);
        cancelBtn = (Button) view.findViewById(R.id.cancelTableBtn);
        tableId = (EditText) view.findViewById(R.id.tableid);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validTableId()) {}
                else saveTable();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            }
        });
        return view;
    }
    public boolean validTableId()
    {
        String tmp = tableId.getText().toString();
        if (tmp.isEmpty())
        {
            tableId.setError("Không thể để trống số bàn!");
            return false;
        }
        else
        {
            for (int i = 0; i < tmp.length(); i++)
            {
                if (tmp.charAt(i) < '0' || tmp.charAt(i) > '9')
                {
                    tableId.setError("Số bàn chỉ chứa các số từ 0 đến 9");
                    return false;
                }
            }

        }
        return true;
    }
    public void saveTable()
    {
        String RnameFromDB = intent.getStringExtra("rname");
        String newID = tableId.getText().toString().trim();
        TableData tableData = new TableData(newID.toString(), false);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables").child(newID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) tableId.setError("Bàn " + newID + " đã tồn tại!");
                else {
                    Toast.makeText(getActivity(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
                    reference.setValue(tableData);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}