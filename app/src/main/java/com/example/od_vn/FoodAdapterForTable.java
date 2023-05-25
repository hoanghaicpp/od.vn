package com.example.od_vn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodAdapterForTable extends RecyclerView.Adapter<FoodViewHolderForTable> {

    Uri uri;

    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    FloatingActionButton delButton,editButton;
    private String Image,Desc,Title,Price;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    private String number;
    private Context context;

    public FoodAdapterForTable(Context context , List<FoodData> dataList, String number) {
        this.context = context;
        this.dataList = dataList;
        this.number = number;
    }

    private List<FoodData> dataList;

    @NonNull
    @Override
    public FoodViewHolderForTable onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_recycler_item, parent,false);
        return new FoodViewHolderForTable(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolderForTable holder, int position) {
        Glide.with(context).load(dataList.get(position).getFoodImage()).into(holder.foodRecImage);
        holder.foodRecTitle.setText(dataList.get(position).getFoodTitle());
        holder.foodRecDesc.setText(dataList.get(position).getFoodDesc());
        holder.foodRecPrice.setText(decimalFormat.format(Integer.valueOf(dataList.get(position).getFoodPrice()))+" VNĐ");
        holder.foodRecCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ((Activity) context).getIntent();
                String RnameFromDB = intent.getStringExtra("rname");
                DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables").child(number);
                //dang code
                reference.child("TableFoods").child(changeFood(dataList.get(holder.getAdapterPosition()).getFoodTitle())).setValue(dataList.get(holder.getAdapterPosition()));
                reference.child("TableFoods").child(changeFood(dataList.get(holder.getAdapterPosition()).getFoodTitle())).child("count").setValue(dataList.get(holder.getAdapterPosition()).getCount());
                reference.child("used").setValue(true);

                Toast.makeText(context,"Thêm thành công",Toast.LENGTH_SHORT).show();
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

class FoodViewHolderForTable extends RecyclerView.ViewHolder{


    ImageView foodRecImage, plus, minus, del;
    TextView foodRecTitle, foodRecDesc, foodRecPrice;
    CardView foodRecCard;
    public FoodViewHolderForTable(@NonNull View itemView) {
        super(itemView);
        foodRecImage = itemView.findViewById(R.id.foodRecImage);
        foodRecTitle = itemView.findViewById(R.id.foodRecTitle);
        foodRecDesc = itemView.findViewById(R.id.foodRecDesc);
        foodRecPrice = itemView.findViewById(R.id.foodRecPrice);
        foodRecCard = itemView.findViewById(R.id.foodRecCard);
    }
}
