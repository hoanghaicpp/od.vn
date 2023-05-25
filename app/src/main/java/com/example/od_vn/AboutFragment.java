package com.example.od_vn;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.icu.number.Scale;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AboutFragment extends Fragment {


    BottomNavigationView bottomNavigationView;
    private DatabaseReference mDatabase;
    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
        final RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        final ImageView ratingImage = view.findViewById(R.id.ratingImage);
        final EditText responseEditText = view.findViewById(R.id.responseEditText);
        Button submitButton = view.findViewById(R.id.submitButton);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <= 1)
                    ratingImage.setImageResource(R.drawable.one_star);
                else if (rating <= 2)
                    ratingImage.setImageResource(R.drawable.two_star);
                else if (rating <= 3)
                    ratingImage.setImageResource(R.drawable.three_star);
                else if (rating <= 4)
                    ratingImage.setImageResource(R.drawable.four_star);
                else if (rating <= 5)
                    ratingImage.setImageResource(R.drawable.five_star);
                animateImage(ratingImage);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String response = responseEditText.getText().toString();
                float rating = ratingBar.getRating();
                // Save the response and rating to Firebase Realtime Database
                saveResponseAndRating(response, rating);

                // Display success message
                Toast.makeText(getContext(), "Gửi đánh giá thành công", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    private void animateImage(ImageView ratingImage) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }
    private void saveResponseAndRating(String response, float rating) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Intent intent = getActivity().getIntent();
        String user = intent.getStringExtra("username");
        DatabaseReference responseRef = databaseRef.child("responses_and_rating").child(user);
        responseRef.child("response").setValue(response);
        responseRef.child("rating").setValue(rating);

    }
}