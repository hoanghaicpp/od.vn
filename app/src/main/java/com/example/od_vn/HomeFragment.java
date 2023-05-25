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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    List<TableData> tableList;
    DatabaseReference reference;
    ValueEventListener eventListener;
    TableAdapter adapter;
    SearchView searchView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fab = view.findViewById(R.id.fab);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.Tablesearch);
        searchView.clearFocus();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        Intent intent = getActivity().getIntent();
        String RnameFromDB = intent.getStringExtra("rname");
        String roleFromDB = intent.getStringExtra("role");
        if (roleFromDB.equals("staff"))
            fab.setVisibility(View.GONE);
        tableList = new ArrayList<>();
        adapter = new TableAdapter(getActivity(), tableList);
        recyclerView.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables");
        dialog.show();
        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tableList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    TableData data = dataSnapshot.getValue(TableData.class);
                    tableList.add(data);
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new UploadTableFragment()).commit();
            }
        });
        return view;
    }
    public void searchList(String text){
        ArrayList<TableData> searchList = new ArrayList<>();
        for (TableData data: tableList){
            if (data.getTableNumber().toLowerCase().contains(text.toLowerCase())){
                searchList.add(data);
            }
        }
        adapter.searchDataList(searchList);
    }
}