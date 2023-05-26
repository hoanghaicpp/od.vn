package com.example.od_vn;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadFoodFragment extends Fragment {

    UserValidator userValidator = new UserValidator();
    ImageView uploadFoodImage;
    Button addFoodButton, cancelFoodButton;
    EditText uploadFoodName, uploadFoodDesc, uploadFoodPrice;
    String imgURL;
    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_food, container, false);
        uploadFoodImage = view.findViewById(R.id.uploadFoodImage);
        uploadFoodName = view.findViewById(R.id.uploadFoodName);
        uploadFoodDesc = view.findViewById(R.id.uploadFoodDesc);
        uploadFoodPrice = view.findViewById(R.id.uploadFoodPrice);
        addFoodButton = view.findViewById(R.id.addFoodButton);
        cancelFoodButton = view.findViewById(R.id.cancelFoodButton);
        uri = Uri.parse("android.resource://com.example.od_vn/drawable/none");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadFoodImage.setImageURI(uri);
                        }
                        else{
                            Toast.makeText(getActivity(),"Không tìm thấy ảnh",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        uploadFoodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validatePrice(uploadFoodPrice.getText().toString(), uploadFoodPrice) |!validateDesc(uploadFoodDesc.getText().toString(), uploadFoodDesc) |!validateFood(uploadFoodName.getText().toString(), uploadFoodName) ){

                }
                else{
                    saveData();
                }

            }
        });
        cancelFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new FoodFragment()).commit();

            }
        });
    }
    public Boolean validateFood(String val, EditText s) {
        if (val.isEmpty()) {
            s.setError("Tên món thể bỏ trống");
            return false;
        } else {

                s.setError(null);
                return true;

        }
    }
    public Boolean validatePrice(String val, EditText s) {
        if (val.isEmpty()) {
            s.setError("Giá không thể bỏ trống");
            return false;
        } else {
            if(isInteger(val)){
                s.setError(null);
                return true;
            }
            else {
                s.setError("Giá không hợp lệ");
                return false;

            }
        }
    }
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public Boolean validateDesc(String val, EditText s) {
        if (val.isEmpty()) {
            s.setError("Không thể bỏ trống");
            return false;
        }
        else {
            s.setError(null);
            return true;
        }
    }

    private void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Food Image").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imgURL = urlImage.toString();
                uploadData();
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }
    public void uploadData(){
        Intent intent = getActivity().getIntent();
        String RnameFromDB = intent.getStringExtra("rname");
        String FoodName = uploadFoodName.getText().toString();
        String FoodDesc = uploadFoodDesc.getText().toString();
        String FoodPrice = uploadFoodPrice.getText().toString();
        FoodData foodData = new FoodData(FoodName, FoodDesc, FoodPrice, imgURL);
        System.out.println(foodData);
        FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("foods").child(changeFood(FoodName)).setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(),"Thêm thành công",Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new FoodFragment()).commit();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Lỗi! Thêm thất bại",Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new FoodFragment()).commit();


            }
        });
    }
    public static String changeFood(String str) {
        str = str.toLowerCase()
                .replaceAll("\\s+", "_")
                .replaceAll("đ", "d")
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A")
                .replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E")
                .replaceAll("[ÌÍỊỈĨ]", "I")
                .replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O")
                .replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U")
                .replaceAll("[ỲÝỴỶỸ]", "Y");
        return str;
    }
}