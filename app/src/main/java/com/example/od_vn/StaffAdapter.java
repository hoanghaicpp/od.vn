package com.example.od_vn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffViewHolder> {

    String MonthtimeStamp = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    FloatingActionButton delButton, editButton;
    private String Name, UserName, Evaluate, Email, Rname;

    String Money = "0", Table = "0";
    private int ID;
    private Context context;
    public StaffAdapter(Context context, List<StaffData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    private List<StaffData> dataList;
    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_recycle_item, parent,false);

        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        holder.StaffRecName.setText(dataList.get(position).getName());
        holder.StaffEvaluate.setText(dataList.get(position).getEvaluate());
        holder.StaffRecID.setText(String.valueOf(dataList.get(position).getId()));

        holder.StaffRecCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Money = "0"; Table = "0";
                Name = dataList.get(holder.getAdapterPosition()).getName();
                UserName = dataList.get(holder.getAdapterPosition()).getUsername();
                Evaluate = dataList.get(holder.getAdapterPosition()).getEvaluate();
                Email = dataList.get(holder.getAdapterPosition()).getEmail();
                Rname = dataList.get(holder.getAdapterPosition()).getRname();
                ID = dataList.get(holder.getAdapterPosition()).getId();
                AlertDialog.Builder build = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.diolog_staff_detail,null);

                TextView StaffDetailName = dialogView.findViewById(R.id.staffDetailName);
                TextView StaffDetailEvaluate = dialogView.findViewById(R.id.staffDetailEvaluate);
                TextView StaffDetailID = dialogView.findViewById(R.id.staffDetailID);
                TextView StaffMoney = dialogView.findViewById(R.id.staffDetailMoney);
                TextView StaffTable = dialogView.findViewById(R.id.staffDetailTable);
                StaffDetailName.setText("Họ và tên: " + Name);
                StaffDetailEvaluate.setText(Evaluate);
                StaffDetailID.setText("ID: " + ID);
                build.setView(dialogView);
                AlertDialog dialog = build.create();
                Intent intent = ((Activity) context).getIntent();
                String RnameFromDB = intent.getStringExtra("rname");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("Revenue").child("Activitys").child("staff").child(MonthtimeStamp).child(UserName);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("Money").exists()){
                            Money = snapshot.child("Money").getValue().toString();
                        }
                        if(snapshot.child("Tablemake").exists()){
                            Table = snapshot.child("Tablemake").getValue().toString();
                        }
                        StaffTable.setText("Trong tháng:\nSố bàn đã thanh toán: "+Table);
                        StaffMoney.setText("Số tiền kiếm được: "+decimalFormat.format(Integer.parseInt(Money))+ " VNĐ");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                String AdminUserNameFromDB = intent.getStringExtra("username");
                delButton = dialogView.findViewById(R.id.deleteStaffButton);
                editButton = dialogView.findViewById(R.id.editStaffButton);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("StaffName", Name);
                        intent.putExtra("StaffUserName", UserName);
                        intent.putExtra("StaffEmail", Email);
                        intent.putExtra("StaffRname", Rname);
                        intent.putExtra("StaffEvaluate", Evaluate);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new StaffEditFragment()).commit();
                        dialog.dismiss();
                    }
                });
                delButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(UserName);
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("staff").child(AdminUserNameFromDB).child(UserName);
                        reference.removeValue();
                        reference1.removeValue();
                        ref.removeValue();
                        Toast.makeText(context,"Xóa thành công",Toast.LENGTH_SHORT).show();
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
    public void searchDataList(ArrayList<StaffData> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}
class StaffViewHolder extends RecyclerView.ViewHolder {

    ImageView StaffRecImg;
    TextView StaffRecName, StaffEvaluate, StaffRecID;
    CardView StaffRecCard;

    public StaffViewHolder(@NonNull View itemView) {
        super(itemView);
        StaffRecImg = itemView.findViewById(R.id.staffRecImg);
        StaffRecName = itemView.findViewById(R.id.staffRecName);
        StaffEvaluate = itemView.findViewById(R.id.staffEvaluate);
        StaffRecCard = itemView.findViewById(R.id.staffRecCard);
        StaffRecID = itemView.findViewById(R.id.staffRecID);
    }
}
