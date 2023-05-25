package com.example.od_vn;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FoodFragment extends Fragment {

    FloatingActionButton foodFab;
    RecyclerView recyclerView;
    List<FoodData> foodData;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    SearchView searchView;
    FoodAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        searchView = view.findViewById(R.id.Foodsearch);
        searchView.clearFocus();
        foodFab = view.findViewById(R.id.food_fab);
        recyclerView = view.findViewById(R.id.foodRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        Intent intent = getActivity().getIntent();
        String RnameFromDB = intent.getStringExtra("rname");
        String roleFromDB = intent.getStringExtra("role");
        if (roleFromDB.equals("staff"))
            foodFab.setVisibility(View.GONE);
        foodData = new ArrayList<>();
        adapter = new FoodAdapter(getActivity(), foodData);
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("foods");
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodData.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    FoodData foodData1 = itemSnapshot.getValue(FoodData.class);
                    foodData.add(foodData1);
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        foodFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new UploadFoodFragment()).commit();
            }
        });

    }
    public void searchList(String text){
        ArrayList<FoodData> searchList = new ArrayList<>();
        for (FoodData foodData1: foodData){
            if (foodData1.getFoodTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(foodData1);
            }
        }
        adapter.searchDataList(searchList);
    }
}