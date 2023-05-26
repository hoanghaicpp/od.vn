package com.example.od_vn;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class NofiFragment extends Fragment {

    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    Button Enter;

    EditText NofiBox;
    List<NofiData> nofiData;
    RecyclerView recyclerView;

    NofiAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nofi, container, false);
        recyclerView = view.findViewById(R.id.NofiRec);
        Enter = view.findViewById(R.id.Enter);
        NofiBox = view.findViewById(R.id.NofiBox);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Intent intent = getActivity().getIntent();
        String RnameFromDB = intent.getStringExtra("rname");
        nofiData = new ArrayList<>();
        adapter = new NofiAdapter(getActivity(), nofiData);
        recyclerView.setAdapter(adapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("Revenue").child("Activitys");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nofiData.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    if(!itemSnapshot.getKey().equals("staff")) {
                        NofiData nofiData1 = new NofiData();
                        String[] temp = tachChuoi(itemSnapshot.getValue().toString());
                        nofiData1.setName(temp[0]);
                        nofiData1.setNofi(transformString(temp[1]));
                        nofiData1.setTime(itemSnapshot.getKey().toString());
                        nofiData.add(nofiData1);
                    }
                }
                Collections.reverse(nofiData);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String NofiRes = NofiBox.getText().toString().trim();

                if(TextUtils.isEmpty(NofiRes)){

                }
                else {
                    Intent intent = getActivity().getIntent();
                    String temp = intent.getStringExtra("usernameInfo")+"//" + "__||THÔNG BÁO||$$" + NofiRes +"$$";
                    String RnameFromDB = intent.getStringExtra("rname");
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("Revenue").child("Activitys");
                    ref.child(timeStamp).setValue(temp);
                    adapter.notifyDataSetChanged();
                    NofiBox.setText("");
                }
            }
        });
    }

    public static String transformString(String inputString) {
        String replacedDollar = inputString.replace("$", "\n");
        String transformedString = replacedDollar.replace("_", " ");
        return transformedString;
    }

    public static String[] tachChuoi(String chuoi) {
        String[] mangTach = chuoi.split("//");
        return mangTach;
    }
}