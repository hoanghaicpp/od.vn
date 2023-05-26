package com.example.od_vn;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QLNVFragment extends Fragment {

    String MonthtimeStamp = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
    BottomNavigationView bottomNavigationView;
    Button addStaffBtn;

    FloatingActionButton Re;
    RecyclerView recyclerView;
    List<StaffData> staffList;

    List<TableRe> tableReList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    SearchView searchView;
    StaffAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q_l_n_v, container, false);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
        addStaffBtn = (Button) view.findViewById(R.id.addStaffBtn);
        searchView = view.findViewById(R.id.staffsearch);
        searchView.clearFocus();
        Re = view.findViewById(R.id.Rebutton);
        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        Intent intent = getActivity().getIntent();
        String AdminUserNameFromDB = intent.getStringExtra("username");

        staffList = new ArrayList<>();
        adapter = new StaffAdapter(getActivity(), staffList);
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("staff").child(AdminUserNameFromDB);
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                staffList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    StaffData staffData = dataSnapshot.getValue(StaffData.class);
                    staffList.add(staffData);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
        addStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new UploadStaffFragment()).commit();
            }
        });
        Re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(QLNVFragment.this.getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_revenua_staff,null);
                build.setView(dialogView);
                android.app.AlertDialog dialog = build.create();
                //
                tableReList = new ArrayList<>();

                RecyclerView recyclerView1 = dialogView.findViewById(R.id.ReMain);
                GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(),1);
                recyclerView1.setLayoutManager(gridLayoutManager1);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setCancelable(false);
                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                Intent intent1 = (getActivity()).getIntent();
                TableReAdapter tableReAdapter = new TableReAdapter(getActivity(),tableReList);
                recyclerView1.setAdapter(tableReAdapter);
                String RnameFromDB = intent1.getStringExtra("rname");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("Revenue").child("Activitys").child("staff").child(MonthtimeStamp);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tableReList.clear();
                        for(int i = 0;i<staffList.size();i++) {
                            staffList.get(i).setTablemake("0");
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getKey().toString().equals(staffList.get(i).getUsername())) {
                                    staffList.get(i).setTablemake(dataSnapshot.child("Tablemake").getValue().toString());
                                    System.out.println(staffList.get(0).getTablemake());
                                }
                            }
                        }
                        Collections.sort(staffList, new Comparator<StaffData>() {
                            public int compare(StaffData st1, StaffData st2) {
                                if (Integer.parseInt(st1.getTablemake()) <  Integer.parseInt( st2.getTablemake())) {
                                    return 1;
                                }
                                return -1;
                            }
                        });
                        for(int i =0;i<staffList.size();i++){
                            TableRe tableRe = new TableRe(""+(i+1),staffList.get(i).getId()+"",staffList.get(i).getName(),staffList.get(i).getTablemake()+"");
                            tableReList.add(tableRe);
                        }
                        tableReAdapter.notifyDataSetChanged();
                        dialog1.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //
                if(dialog.getWindow()!=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
        return view;
    }

    public void searchList(String text){
        ArrayList<StaffData> searchList = new ArrayList<>();
        for (StaffData data: staffList){
            if (data.getName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(data);
            }
        }
        adapter.searchDataList(searchList);
    }
}
