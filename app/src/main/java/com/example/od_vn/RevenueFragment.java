package com.example.od_vn;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class RevenueFragment extends Fragment {
    String test;

    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    int CostSum = 0;

    Button AddCost;
    TextView SumRe, SumFinal;
    CostAdapter adapter;
    List<CostData> Cost;
    RecyclerView recyclerView;
    List<Float> Values;
    List<String> Days, Months;

    ArrayList<BarEntry> barEntries;
    BarChart barChart;
    Button Dayly,Monthly,Yaerly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_revenue, container, false);
        barChart = view.findViewById(R.id.chart);
        SumRe = view.findViewById(R.id.ReDay);
        SumFinal = view.findViewById(R.id.ReFinal);
        AddCost = view.findViewById(R.id.addCost);
        Dayly = view.findViewById(R.id.Dayly);
        Monthly = view.findViewById(R.id.Monthly);
        Yaerly = view.findViewById(R.id.Yearly);
        recyclerView = view.findViewById(R.id.recyclerView2);
        initRes();
        Days = new ArrayList<>();
        Values = new ArrayList<>();
        Days = getSevenDaysList();
        Months = getTwelveMonthsList();
        barEntries = new ArrayList<>();
        setBarDayly();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dayly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBarDayly();
            }
        });
        Monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBarMonthly();
            }
        });
        Yaerly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBarYearly();
            }
        });
        AddCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(RevenueFragment.this.getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_cost_add,null);
                EditText TitleBox = dialogView.findViewById(R.id.CostTitle);
                EditText PriceBox = dialogView.findViewById(R.id.CostPrice);
                build.setView(dialogView);
                android.app.AlertDialog dialog = build.create();
                dialogView.findViewById(R.id.cost_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String CostTitle = TitleBox.getText().toString();
                        String CostPrice = PriceBox.getText().toString();
                        if (!TextUtils.isEmpty(CostTitle) && isInteger(CostPrice)){
                            CostData costData = new CostData(CostTitle,Integer.valueOf(CostPrice));
                            Intent intent = getActivity().getIntent();
                            String RnameFromDB = intent.getStringExtra("rname");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("Revenue");
                            ref.child("Costs").child(CostTitle).setValue(costData);
                            initRes();
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(TextUtils.isEmpty(CostTitle)){
                                TitleBox.setError("Vui lòng nhập tên khoản chi!");
                                TitleBox.requestFocus();
                            }
                            if(!isInteger(CostPrice)){
                                PriceBox.setError("Vui lòng nhập giá tiền hợp lệ!");
                                PriceBox.requestFocus();
                            }
                        }
                    }
                });
                dialogView.findViewById(R.id.cost_btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if(dialog.getWindow()!=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();

            }
        });

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
    public static List<String> getTwelveMonthsList() {
        List<String> monthsList = new ArrayList<>();
        monthsList.add("202301");
        monthsList.add("202302");
        monthsList.add("202303");
        monthsList.add("202304");
        monthsList.add("202305");
        monthsList.add("202306");
        monthsList.add("202307");
        monthsList.add("202308");
        monthsList.add("202309");
        monthsList.add("202310");
        monthsList.add("202311");
        monthsList.add("202312");
        return  monthsList;
    }

    public static List<String> getTwelveMonthsListSmall() {
        List<String> monthsList = new ArrayList<>();
        monthsList.add("01");
        monthsList.add("02");
        monthsList.add("03");
        monthsList.add("04");
        monthsList.add("05");
        monthsList.add("06");
        monthsList.add("07");
        monthsList.add("08");
        monthsList.add("09");
        monthsList.add("10");
        monthsList.add("11");
        monthsList.add("12");
        return  monthsList;
    }
    public static List<String> getSevenDaysListddMM() {
        List<String> daysList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM");
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            String formattedDate = dateFormat.format(calendar.getTime());
            daysList.add(formattedDate);
            calendar.add(Calendar.DAY_OF_YEAR, -1); // Trừ ngày để lấy ngày gần nhất
        }

        return daysList;
    }
    public void setBarDayly(){
        barEntries.clear();
        barChart.clear();
        Intent intent = getActivity().getIntent();
        String RnameFromDB = intent.getStringExtra("rname");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB);
        ref.child("Revenue").child("Days").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Values.clear();
                for(int i =Days.size()-1;i>=0;i--){
                    Float temp = Float.valueOf(0);
                    if (snapshot.child(Days.get(i)).exists())
                    temp = Float.parseFloat(snapshot.child(Days.get(i)).getValue().toString());
                    Values.add(temp);
                }
                for (int i = 0;i<Days.size();i++){
                    float value = (float) Values.get(i);
                    BarEntry barEntry = new BarEntry(i, value);
                    barEntries.add(barEntry);
                }
                List<String> Dayss = new ArrayList<>();
                Dayss =  getSevenDaysListddMM();
                Collections.reverse(Dayss);
                barChart.setDrawBarShadow(false);
                barChart.setDrawGridBackground(false);
                barChart.animateY(500);
                XAxis xaxis = barChart.getXAxis();
                xaxis.setDrawGridLines(false);
                xaxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                xaxis.setDrawLabels(true);
                xaxis.setDrawAxisLine(false);
                xaxis.setValueFormatter(new IndexAxisValueFormatter(Dayss));
                YAxis yAxisLeft = barChart.getAxisLeft();
//                yAxisLeft.setDrawGridLines(false);
                yAxisLeft.setDrawAxisLine(false);
                yAxisLeft.setTextColor(Color.rgb(	81,	185,	244));
                barChart.getAxisRight().setEnabled(false);
                xaxis.setLabelCount(7, false);
                barChart.getDescription().setPosition(600,50);
                barChart.getDescription().setTextSize(12f);
                barChart.getDescription().setText("Thống kê 7 ngày gần nhất");
                BarDataSet barDataSet = new BarDataSet(barEntries,"Ngày");
                barDataSet.setColor(Color.CYAN);
                barDataSet.setValueTextSize(10f);
                barChart.setData(new BarData(barDataSet));
                Dayly.setEnabled(false);
                Monthly.setEnabled(true);
                Yaerly.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }

    public void setBarMonthly(){
        barEntries.clear();
        barChart.clear();
        Intent intent = getActivity().getIntent();
        String RnameFromDB = intent.getStringExtra("rname");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB);
        ref.child("Revenue").child("Months").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Values.clear();
                for(int i =0;i<Months.size();i++){
                    Float temp = Float.valueOf(0);
                    if (snapshot.child(Months.get(i)).exists())
                    temp = Float.parseFloat(snapshot.child(Months.get(i)).getValue().toString());
                    Values.add(temp);
                }
                for (int i = 0;i<Months.size();i++){
                    float value = (float) Values.get(i);
                    BarEntry barEntry = new BarEntry(i, value);
                    barEntries.add(barEntry);
                }
                List<String> Monthss = new ArrayList<>();
                Monthss = getTwelveMonthsListSmall();
                barChart.setDrawBarShadow(false);
                barChart.setDrawGridBackground(false);
                barChart.animateY(500);
                XAxis xaxis = barChart.getXAxis();
                xaxis.setDrawGridLines(false);
                xaxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                xaxis.setLabelCount(12, false);
                xaxis.setDrawLabels(true);
                xaxis.setDrawAxisLine(false);
                xaxis.setValueFormatter(new IndexAxisValueFormatter(Monthss));
                YAxis yAxisLeft = barChart.getAxisLeft();
//                yAxisLeft.setDrawGridLines(false);
                yAxisLeft.setDrawAxisLine(false);
                yAxisLeft.setTextColor(Color.rgb(	81,	185,	244));
                barChart.getAxisRight().setEnabled(false);
                barChart.getDescription().setPosition(600,50);
                barChart.getDescription().setTextSize(12f);
                barChart.getDescription().setText("Thống kê theo tháng");
                BarDataSet barDataSet = new BarDataSet(barEntries,"Tháng");
                barDataSet.setColor(Color.CYAN);
                barDataSet.setValueTextSize(10f);
                barChart.getLegend().setEnabled(true);
                barChart.setData(new BarData(barDataSet));
                Dayly.setEnabled(true);
                Monthly.setEnabled(false);
                Yaerly.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }
    public void setBarYearly(){
        barEntries.clear();
        barChart.clear();
        Intent intent = getActivity().getIntent();
        String RnameFromDB = intent.getStringExtra("rname");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB);
        ref.child("Revenue").child("Sum").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Values.clear();
                    Float temp = Float.valueOf(0);
                    temp = Float.parseFloat(snapshot.getValue().toString());
                    Values.add((float) 0);
                    Values.add((float) 0);
                    Values.add((float) 0);
                    Values.add(temp);
                }

                for (int i = 0; i < Values.size(); i++) {
                    float value = (float) Values.get(i);
                    BarEntry barEntry = new BarEntry(i, value);
                    barEntries.add(barEntry);
                }
                List<String> Yearss = new ArrayList<>();
                Yearss.add("2020");
                Yearss.add("2021");
                Yearss.add("2022");
                Yearss.add("2023");
                barChart.setDrawBarShadow(false);
                barChart.setDrawGridBackground(false);
                barChart.animateY(500);
                XAxis xaxis = barChart.getXAxis();
                xaxis.setDrawGridLines(false);
                xaxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                xaxis.setLabelCount(4, false);
                xaxis.setDrawLabels(true);
                xaxis.setDrawAxisLine(false);
                xaxis.setValueFormatter(new IndexAxisValueFormatter(Yearss));
                YAxis yAxisLeft = barChart.getAxisLeft();
                //                yAxisLeft.setDrawGridLines(false);
                yAxisLeft.setDrawAxisLine(false);
                yAxisLeft.setTextColor(Color.rgb(81, 185, 244));
                barChart.getAxisRight().setEnabled(false);
                barChart.getDescription().setPosition(600, 50);
                barChart.getDescription().setTextSize(12f);
                barChart.getDescription().setText("Thống kê theo năm");
                BarDataSet barDataSet = new BarDataSet(barEntries, "Năm");
                barDataSet.setColor(Color.CYAN);
                barDataSet.setValueTextSize(10f);
                barChart.getLegend().setEnabled(true);
                barChart.setData(new BarData(barDataSet));
                Dayly.setEnabled(true);
                Monthly.setEnabled(true);
                Yaerly.setEnabled(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }

    public void initRes(){
        CostSum = 0;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        Intent intent = getActivity().getIntent();
        String RnameFromDB = intent.getStringExtra("rname");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Cost = new ArrayList<>();
        adapter = new CostAdapter(getActivity(), Cost);
        recyclerView.setAdapter(adapter);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(RnameFromDB).child("Revenue");
        ref.child("Costs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cost.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    CostData costData = itemSnapshot.getValue(CostData.class);
                    CostSum+=costData.getValue();
                    Cost.add(costData);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("Sum").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SumRe.setText("Tổng doanh thu: " + decimalFormat.format((Integer.valueOf(snapshot.getValue().toString()))) + " VNĐ");
                    SumFinal.setText("Tổng lợi nhuận: " + decimalFormat.format((Integer.valueOf(snapshot.getValue().toString()) - CostSum)) + " VNĐ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
