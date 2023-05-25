package com.example.od_vn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NofiAdapter extends RecyclerView.Adapter<NofiViewHolder> {

    private Context context;

    public NofiAdapter(Context context, List<NofiData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    private List<NofiData> dataList;

    @NonNull
    @Override
    public NofiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nofi_item, parent,false);
        return new NofiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NofiViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Intent intent = ((Activity) context).getIntent();
        holder.User.setText(dataList.get(position).getName());
        holder.Time.setText(chuyenDoiThoiGian(dataList.get(position).getTime()));
        holder.Nofi.setText(dataList.get(position).getNofi().replace("|",""));

        String userCheck = intent.getStringExtra("usernameInfo");
        holder.nofiItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((dataList.get(position).getName().equals(userCheck)||intent.getStringExtra("role").equals("admin")) && extractValue(dataList.get(position).getNofi()).equals("THÔNG BÁO")){
                    PopupMenu popupMenu = new PopupMenu(context, holder.nofiItem);
                    popupMenu.getMenu().add(Menu.NONE,0,0,"Xoá");
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if(menuItem.getItemId()==0){
                                String RnameFromDB = intent.getStringExtra("rname");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB);
                                ref.child("Revenue").child("Activitys").child(dataList.get(position).getTime()).removeValue();
                                dataList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context,"Xoá thành công",Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public String extractValue(String input) {
        int start = input.indexOf("||") + 2; // Tìm vị trí bắt đầu của giá trị
        int end = input.indexOf("||", start); // Tìm vị trí kết thúc của giá trị
        if (start != -1 && end != -1) {
            return input.substring(start, end); // Trích xuất giá trị
        }
        return null; // Trường hợp không tìm thấy giá trị
    }

    public static String chuyenDoiThoiGian(String chuoiThoiGian) {
        // Định dạng đầu vào
        DateTimeFormatter dinhDangDauVao = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dinhDangDauVao = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        }

        // Định dạng đầu ra
        DateTimeFormatter dinhDangDauRa = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dinhDangDauRa = DateTimeFormatter.ofPattern("HH:mm:ss 'ngày' dd 'tháng' MM");
        }

        // Chuyển đổi chuỗi thời gian thành đối tượng LocalDateTime
        LocalDateTime thoiGian = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            thoiGian = LocalDateTime.parse(chuoiThoiGian, dinhDangDauVao);
        }

        // Chuyển đổi thành chuỗi định dạng mong muốn
        String ketQua = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ketQua = thoiGian.format(dinhDangDauRa);
        }

        return ketQua;
    }
   }





class NofiViewHolder extends RecyclerView.ViewHolder{


    ImageView del;
    TextView User, Time, Nofi;
    CardView nofiItem;
    public NofiViewHolder(@NonNull View itemView) {
        super(itemView);
        User = itemView.findViewById(R.id.NofiPost);

        Time = itemView.findViewById(R.id.NofiTime);

        Nofi = itemView.findViewById(R.id.NofiBox);
        nofiItem = itemView.findViewById(R.id.NofiRec);

    }
}
