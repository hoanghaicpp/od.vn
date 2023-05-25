package com.example.od_vn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder> {

    Uri uri;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    FloatingActionButton delButton, editButton;
    FloatingActionMenu FoodActionMenu;
    private String Image, Desc, Title, Price;

    private Context context;

    public FoodAdapter(Context context, List<FoodData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    private List<FoodData> dataList;

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_recycler_item, parent,false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getFoodImage()).into(holder.foodRecImage);
        holder.foodRecTitle.setText(dataList.get(position).getFoodTitle());
        holder.foodRecDesc.setText(dataList.get(position).getFoodDesc());
        holder.foodRecPrice.setText(decimalFormat.format(Integer.valueOf(dataList.get(position).getFoodPrice()))+" VNĐ");

        holder.foodRecCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Image = dataList.get(holder.getAdapterPosition()).getFoodImage();
                Desc = dataList.get(holder.getAdapterPosition()).getFoodDesc();
                Title =  dataList.get(holder.getAdapterPosition()).getFoodTitle();
                Price = dataList.get(holder.getAdapterPosition()).getFoodPrice();
                AlertDialog.Builder build = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_food_detail,null);

                TextView foodDetailDesc = dialogView.findViewById(R.id.foodDetailDesc);
                TextView foodDetailTitle = dialogView.findViewById(R.id.foodDetailTitle);
                TextView foodDetailPrice = dialogView.findViewById(R.id.foodDetailPrice);
                ImageView foodDetailImage = dialogView.findViewById(R.id.foodDetailImage);
                foodDetailDesc.setText("Mô tả: " + Desc);
                foodDetailTitle.setText("Tên món: " + Title);
                foodDetailPrice.setText("Giá: " + Price);
                Glide.with(context).load(Image).into(foodDetailImage);
                build.setView(dialogView);
                AlertDialog dialog = build.create();
                Intent intent = ((Activity) context).getIntent();
                String RnameFromDB = intent.getStringExtra("rname");
                String RoleFromDB = intent.getStringExtra("role");
                FoodActionMenu = dialogView.findViewById(R.id.FoodFloatAction);
                if (RoleFromDB.equals("staff"))
                    FoodActionMenu.setVisibility(View.GONE);
                delButton = dialogView.findViewById(R.id.deleteFoodButton);
                delButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("foods").child(changeFood(Title));
                        reference.removeValue();
                        Toast.makeText(context,"Xóa thành công",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                editButton = dialogView.findViewById(R.id.editFoodButton);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("foodTitle", Title);
                        intent.putExtra("foodDesc", Desc);
                        intent.putExtra("foodPrice", Price);
                        intent.putExtra("foodImage", Image);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new FoodEditFragment()).commit();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                if(dialog.getWindow()!=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public void searchDataList(ArrayList<FoodData> searchList){
        dataList = searchList;
        notifyDataSetChanged();
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

class FoodViewHolder extends RecyclerView.ViewHolder{

    ImageView foodRecImage;
    TextView foodRecTitle, foodRecDesc, foodRecPrice;
    CardView foodRecCard;
    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        foodRecImage = itemView.findViewById(R.id.foodRecImage);
        foodRecTitle = itemView.findViewById(R.id.foodRecTitle);
        foodRecDesc = itemView.findViewById(R.id.foodRecDesc);
        foodRecPrice = itemView.findViewById(R.id.foodRecPrice);
        foodRecCard = itemView.findViewById(R.id.foodRecCard);
    }
}
