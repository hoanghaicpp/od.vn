package com.example.od_vn;

import android.annotation.SuppressLint;
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

public class CostAdapter extends RecyclerView.Adapter<CostViewHolder> {


    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    private String Title, Price;

    private Context context;

    public CostAdapter(Context context, List<CostData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    private List<CostData> dataList;

    @NonNull
    @Override
    public CostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cost_item, parent,false);
        return new CostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.Title.setText(dataList.get(position).getItem()+": ");
        holder.Price.setText(decimalFormat.format(dataList.get(position).getValue())+" VNĐ");

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ((Activity) context).getIntent();
                String RnameFromDB = intent.getStringExtra("rname");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("Revenue").child("Costs").child(dataList.get(position).getItem());
                reference.removeValue();
                dataList.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Xóa!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
   }





class CostViewHolder extends RecyclerView.ViewHolder{


    ImageView del;
    TextView Title, Price;
    CardView costItem;
    public CostViewHolder(@NonNull View itemView) {
        super(itemView);
        Title = itemView.findViewById(R.id.CostTitle);

        Price = itemView.findViewById(R.id.CostPrice);

        del = itemView.findViewById(R.id.delCost);

    }
}
