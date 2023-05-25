package com.example.od_vn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodAdapterForTableRecycleView extends RecyclerView.Adapter<FoodViewHolderForTableRecycleView> {

    Uri uri;

    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    FloatingActionButton delButton,editButton;
    private String Image,Title,Price;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
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

    public FoodAdapterForTableRecycleView(Context context , List<FoodData> dataList, String number) {
        this.context = context;
        this.dataList = dataList;
        this.number = number;
    }

    private List<FoodData> dataList;

    @NonNull
    @Override
    public FoodViewHolderForTableRecycleView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_recycler_item_fortable, parent,false);
        return new FoodViewHolderForTableRecycleView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolderForTableRecycleView holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(dataList.get(position).getFoodImage()).into(holder.foodRecImage);
        holder.foodRecTitle.setText(dataList.get(position).getFoodTitle());
        holder.foodRecPrice.setText(""+decimalFormat.format(dataList.get(position).getCount()*Integer.valueOf(dataList.get(position).getFoodPrice()))+" VNĐ");
        holder.countFood.setText(String.valueOf(dataList.get(position).getCount()));
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataList.get(position).plusFood();
                Intent intent = ((Activity) context).getIntent();
                String RnameFromDB = intent.getStringExtra("rname");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables").child(number);
                holder.foodRecPrice.setText(""+(dataList.get(position).getCount()*Integer.valueOf(dataList.get(position).getFoodPrice())));
                holder.countFood.setText(String.valueOf(dataList.get(position).getCount()));
                reference.child("TableFoods").child(changeFood(dataList.get(holder.getAdapterPosition()).getFoodTitle())).child("count").setValue(dataList.get(holder.getAdapterPosition()).getCount());
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataList.get(position).minusFood();
                Intent intent = ((Activity) context).getIntent();
                String RnameFromDB = intent.getStringExtra("rname");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables").child(number);
                holder.foodRecPrice.setText(""+(dataList.get(position).getCount()*Integer.valueOf(dataList.get(position).getFoodPrice())));
                holder.countFood.setText(String.valueOf(dataList.get(position).getCount()));
                reference.child("TableFoods").child(changeFood(dataList.get(holder.getAdapterPosition()).getFoodTitle())).child("count").setValue(dataList.get(holder.getAdapterPosition()).getCount());
            }
        });
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ((Activity) context).getIntent();
                String RnameFromDB = intent.getStringExtra("rname");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables").child(number);
                reference.child("TableFoods").child(changeFood(dataList.get(position).getFoodTitle())).removeValue();
                dataList.remove(position);
                if(dataList.isEmpty()){
                    reference.child("used").setValue(false);
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



class FoodViewHolderForTableRecycleView extends RecyclerView.ViewHolder{

    ImageView foodRecImage, plus, minus, del;
    TextView foodRecTitle, foodRecDesc, foodRecPrice, countFood;
    CardView foodRecCard;
    public FoodViewHolderForTableRecycleView(@NonNull View itemView) {
        super(itemView);
        foodRecImage = itemView.findViewById(R.id.foodRecImageForTable);
        foodRecTitle = itemView.findViewById(R.id.foodRecTitleFortable);
        foodRecPrice = itemView.findViewById(R.id.foodRecPriceForTable);
        foodRecCard = itemView.findViewById(R.id.foodRecCardForTable);
        plus = itemView.findViewById(R.id.plusItem);
        minus = itemView.findViewById(R.id.minusItem);
        countFood = itemView.findViewById(R.id.foodCountForTable);
        del = itemView.findViewById(R.id.delItem);
    }


}
