package com.example.od_vn;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InformationFragment extends Fragment {

    BottomNavigationView bottomNavigationView;
    FirebaseAuth auth;
    Button buttonEditprofile;

    TextView profileName, profileEmail, profileUsername, profileRname, profilePassword;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_information, container, false);
       bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
       profileName = view.findViewById(R.id.profileName);
       profileEmail = view.findViewById(R.id.profileEmail);
       profileUsername = view.findViewById(R.id.profileUsername);
       profileRname = view.findViewById(R.id.profileRname);
       buttonEditprofile = view.findViewById(R.id.editButton);
       auth = FirebaseAuth.getInstance();
       showUserData();
       return  view;
    }

    public void showUserData(){
        Intent intent = getActivity().getIntent();
        String usernameFromDB = intent.getStringExtra("username");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String emailFromDB = snapshot.child(usernameFromDB).child("email").getValue(String.class);
                String nameFromDB = snapshot.child(usernameFromDB).child("name").getValue(String.class);
                String rnameFromDB = snapshot.child(usernameFromDB).child("rname").getValue(String.class);
                profileName.setText(nameFromDB);
                profileEmail.setText(emailFromDB);
                profileUsername.setText(intent.getStringExtra("usernameInfo"));
                profileRname.setText(rnameFromDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public  void passUserData(){

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView.setVisibility(View.GONE);
        buttonEditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getActivity().getIntent();
                String RoleFromDB = intent.getStringExtra("role");
                if (RoleFromDB.equals("admin")) getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new EditAdminInfoFragment()).commit();
                else getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new EditStaffInfoFragment()).commit();
            }
        });
    }
}