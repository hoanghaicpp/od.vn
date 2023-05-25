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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {

    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    int Price = 0, Sum = 0, temp1=0, tempM = 0, tempS = 0;

    TextView SumPrice;
    ValueEventListener eventListener;
    DatabaseReference databaseReference;
    List<FoodData> foodData;

    List<FoodData> foodDataForSave;
    List<FoodData> foodData_;
    FoodAdapterForTableRecycleView adapter_;
    FoodAdapterForTable adapter;
    FloatingActionButton deleteBtn, foodBtn, payTableBtn;
    private List<TableData> dataList;
    private Context context;

    private String Number, DaytimeStamp, MonthtimeStamp, timeStamp;
    private String Status;
    public TableAdapter(Context context, List<TableData> dataList)
    {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_reycler_item, parent,false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        holder.tableRecID.setText(dataList.get(position).getTableNumber());
        holder.tableRecStatus.setText((dataList.get(position).isUsed() ? "Bàn đang được sử dụng" : "Bàn trống"));
        holder.tableRecCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTime();
                Intent intent = ((Activity) context).getIntent();
                String RnameFromDB = intent.getStringExtra("rname");
                Number = dataList.get(holder.getAdapterPosition()).getTableNumber();
                Status = (dataList.get(holder.getAdapterPosition()).isUsed() ? "Bàn đang được sử dụng" : "Bàn trống");
                AlertDialog.Builder build = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_table_detail,null);

                RecyclerView tableRecyclerView = dialogView.findViewById(R.id.TablefoodRecyclerView);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
                tableRecyclerView.setLayoutManager(gridLayoutManager);
                SumPrice = dialogView.findViewById(R.id.SumPrice);
                // phan can code

                foodData = new ArrayList<>();
                adapter_ = new FoodAdapterForTableRecycleView(context, foodData, Number);
                tableRecyclerView.setAdapter(adapter_);
                DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables").child(Number).child("TableFoods");
                eventListener = reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        foodData.clear();
                        Price = 0;
                        for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                            FoodData foodData1 = itemSnapshot.getValue(FoodData.class);
                            foodData1.setCount(Integer.valueOf(snapshot.child(changeFood(foodData1.getFoodTitle())).child("count").getValue().toString()));
                            Price += Integer.parseInt(foodData1.getFoodPrice().toString())*foodData1.getCount();
                            foodData.add(foodData1);
                        }
                        adapter_.notifyDataSetChanged();
                        SumPrice.setText("Tổng tiền: " + decimalFormat.format(Price) + " VNĐ");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //

                TextView tableDetailID = dialogView.findViewById(R.id.tableDetailID);
                TextView tableDetailStatus = dialogView.findViewById(R.id.tableDetailStatus);

                tableDetailID.setText("Bàn số " + Number);
                tableDetailStatus.setText(Status);

                build.setView(dialogView);
                AlertDialog dialog = build.create();

                deleteBtn = dialogView.findViewById(R.id.deleteTableBtn);
                foodBtn = dialogView.findViewById(R.id.addFoodTableBtn);
                payTableBtn = dialogView.findViewById(R.id.PayTableBtn);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables").child(Number);
                        reference.removeValue();
                        Toast.makeText(context,"Xóa thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                if(dialog.getWindow()!=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                foodBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tableDetailStatus.setText("Bàn đang được sử dụng");
                        AlertDialog.Builder build_ = new AlertDialog.Builder(context);
                        LayoutInflater inflater_ = LayoutInflater.from(context);
                        View dialogView_ = inflater_.inflate(R.layout.dialog_table_add_food,null);
                        build_.setView(dialogView_);
                        AlertDialog dialog_ = build_.create();
                        if(dialog_.getWindow()!=null){
                            dialog_.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        }
                        dialog_.show();
                        RecyclerView recyclerView = dialogView_.findViewById(R.id.TablefoodRecyclerView);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        Intent intent = ((Activity) context).getIntent();
                        Number = dataList.get(holder.getAdapterPosition()).getTableNumber();
                        String RnameFromDB = intent.getStringExtra("rname");
                        foodData_ = new ArrayList<>();
                        adapter = new FoodAdapterForTable(context, foodData_, Number);
                        recyclerView.setAdapter(adapter);
                        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("foods");
                        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                foodData_.clear();
                                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                                    FoodData foodData1 = itemSnapshot.getValue(FoodData.class);
                                    foodData_.add(foodData1);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                foodDataForSave = new ArrayList<>();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB);
                ref.child("Revenue").child("Days").child(DaytimeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                            temp1 = Integer.parseInt(snapshot.getValue(String.class).trim());
                        else temp1 = 0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
                ref.child("Revenue").child("Months").child(MonthtimeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                            tempM = Integer.parseInt(snapshot.getValue(String.class).trim());
                        else tempM = 0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
                ref.child("Revenue").child("Sum").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                            tempS = Integer.parseInt(snapshot.getValue(String.class).trim());
                        else tempS = 0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
                DatabaseReference  reference1 = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables").child(Number);
                eventListener = reference1.child("TableFoods").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        foodDataForSave.clear();
                        for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                            FoodData foodData1 = itemSnapshot.getValue(FoodData.class);
//                                    foodData1.setFoodPrice(String.valueOf(Integer.valueOf(foodData1.getCount()*Integer.valueOf(foodData1.getFoodPrice()))));
//                                    FoodDataSmall foodDataSmall = new FoodDataSmall(foodData1.getCount(),foodData1.getFoodTitle(),foodData1.getFoodPrice());
                            foodDataForSave.add(foodData1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
                payTableBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = ((Activity) context).getIntent();
                        Number = dataList.get(holder.getAdapterPosition()).getTableNumber();
                        String RnameFromDB = intent.getStringExtra("rname");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("tables").child(Number);
                        eventListener = reference.child("TableFoods").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                foodDataForSave.clear();
                                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                    FoodData foodData1 = itemSnapshot.getValue(FoodData.class);
//                                    foodData1.setFoodPrice(String.valueOf(Integer.valueOf(foodData1.getCount()*Integer.valueOf(foodData1.getFoodPrice()))));
//                                    FoodDataSmall foodDataSmall = new FoodDataSmall(foodData1.getCount(),foodData1.getFoodTitle(),foodData1.getFoodPrice());
                                    foodDataForSave.add(foodData1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {


                            }
                        });
                        if (!foodDataForSave.isEmpty()) {
                            String[] temp = SumPrice.getText().toString().split(": ");
                            Sum = parseCurrency(temp[1]);
                            ref.child("Revenue").child("Days").child(DaytimeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists())
                                        temp1 = Integer.parseInt(snapshot.getValue(String.class).trim());
                                    else temp1 = 0;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {


                                }
                            });
                            ref.child("Revenue").child("Months").child(MonthtimeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists())
                                        tempM = Integer.parseInt(snapshot.getValue(String.class).trim());
                                    else tempM = 0;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {


                                }
                            });
                            ref.child("Revenue").child("Sum").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists())
                                        tempS = Integer.parseInt(snapshot.getValue(String.class).trim());
                                    else tempS = 0;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {


                                }
                            });
                            String putRes = intent.getStringExtra("usernameInfo") + "//" + "__||THANH TOÁN||$$Bàn số: " + Number + "$";
                            for (int i = 0; i < foodDataForSave.size(); i++) {
                                putRes += "$___" + (i + 1) + ". " + foodDataForSave.get(i).getFoodTitle() + ",__Số lượng: " + foodDataForSave.get(i).getCount() + ",__Đơn giá: " + decimalFormat.format(Integer.parseInt(foodDataForSave.get(i).getFoodPrice().toString())) + " VNĐ" + ",__Tổng: " + decimalFormat.format(Integer.valueOf(foodDataForSave.get(i).getFoodPrice()) * foodDataForSave.get(i).getCount()) + " VNĐ$";
//
                            }
                            putRes += "$$__Tổng thanh toán: " + decimalFormat.format(Sum) + " VNĐ$$";
                            ref.child("Revenue").child("Activitys").child(timeStamp).setValue(putRes);
                            ref.child("Revenue").child("Days").child(DaytimeStamp).setValue(String.valueOf(Sum + temp1));
                            ref.child("Revenue").child("Months").child(MonthtimeStamp).setValue(String.valueOf(Sum + tempM));
                            ref.child("Revenue").child("Sum").setValue(String.valueOf(Sum + tempS));
                            SumPrice.setText("Tổng tiền");

                            reference.child("used").setValue(false);

                            reference.child("TableFoods").removeValue();
                            Toast.makeText(context, "Thanh toán thành công", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public void searchDataList(ArrayList<TableData> searchList)
    {
        dataList = searchList;
        notifyDataSetChanged();
    }
    public void callTime(){
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        DaytimeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

        MonthtimeStamp = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
    }
    public static List<String> getSevenDaysList() {
        List<String> daysList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            String formattedDate = dateFormat.format(calendar.getTime());
            daysList.add(formattedDate);
            calendar.add(Calendar.DAY_OF_YEAR, -1); // Trừ ngày để lấy ngày gần nhất
        }

        return daysList;
    }
    public static int parseCurrency(String currencyString) {
        String numberString = currencyString.replace(",", "").replace(" VNĐ", "").replace(".","");
        return Integer.parseInt(numberString);
    }
    public static String transformString(String inputString) {
        String replacedDollar = inputString.replace("$", "\n");
        String transformedString = replacedDollar.replace("_", " ");
        return transformedString;
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
class TableViewHolder extends RecyclerView.ViewHolder
{
    ImageView tableRecImg;
    TextView tableRecID, tableRecStatus;
    CardView tableRecCard;

    public TableViewHolder(@NonNull View itemView) {
        super(itemView);
        tableRecImg = itemView.findViewById(R.id.tableRecImg);
        tableRecID = itemView.findViewById(R.id.tableRecId);
        tableRecStatus = itemView.findViewById(R.id.tableRecStatus);
        tableRecCard = itemView.findViewById(R.id.tableRecCard);
    }
}
