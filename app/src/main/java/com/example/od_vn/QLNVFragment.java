package com.example.od_vn;

import android.content.Intent;
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
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QLNVFragment extends Fragment {

    BottomNavigationView bottomNavigationView;
    Button addStaffBtn;
    RecyclerView recyclerView;
    List<StaffData> staffList;
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
        return view;
    }
    public void searchList(String text){
        ArrayList<StaffData> searchList = new ArrayList<>();
        for (StaffData data: staffList){
            if (data.getUsername().toLowerCase().contains(text.toLowerCase())){
                searchList.add(data);
            }
        }
        adapter.searchDataList(searchList);
    }
}