package com.example.od_vn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class TableReAdapter extends RecyclerView.Adapter<TableReViewHolder> {


    DecimalFormat decimalFormat = new DecimalFormat("#,###");


    private Context context;

    public TableReAdapter(Context context, List<TableRe> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    private List<TableRe> dataList;

    @NonNull
    @Override
    public TableReViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staffre_item, parent,false);
        return new TableReViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableReViewHolder holder, @SuppressLint("RecyclerView") int position) {
       if(dataList !=null && dataList.size()>0 ){
            holder.Rank.setText(dataList.get(position).getRank());
            holder.ID.setText(dataList.get(position).getId());
            holder.Name.setText(dataList.get(position).getName());
            holder.Value.setText(dataList.get(position).getValue());
       } else {
           return;
       }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
   }





class TableReViewHolder extends RecyclerView.ViewHolder{


    TextView  Rank, ID, Name, Value;
    public TableReViewHolder(@NonNull View itemView) {
        super(itemView);
        Rank = itemView.findViewById(R.id.TableRank);
        ID = itemView.findViewById(R.id.TableID);
        Name = itemView.findViewById(R.id.TableName);
        Value = itemView.findViewById(R.id.TableCategory);
    }
}
