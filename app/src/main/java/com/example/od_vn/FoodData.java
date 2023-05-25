package com.example.od_vn;

import android.app.Activity;

import androidx.fragment.app.Fragment;

public class FoodData {
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public void plusFood(){
        this.count++;
    }
    public void minusFood(){
        if(this.count>1) this.count--;
    }
    private int count;
    private String foodTitle;
    private String foodDesc;
    private String foodPrice;
    private String foodImage;

    public String getFoodTitle() {
        return foodTitle;
    }

    public void setFoodTitle(String foodTitle) {
        this.foodTitle = foodTitle;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public void setFoodDesc(String foodDesc) {

        this.foodDesc = foodDesc;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public FoodData(String foodTitle, String foodDesc, String foodPrice, String foodImage) {
        this.foodTitle = foodTitle;
        this.foodDesc = foodDesc;
        this.foodPrice = foodPrice;
        this.foodImage = foodImage;
        this.count = 1;
    }



    public FoodData() {}

}
