package com.example.od_vn;

public class CostData {
    public CostData(String item, int value) {
        Item = item;
        Value = value;
    }

    public CostData() {
    }

    public String getItem() {
        return Item;
    }

    public void setItem(String item) {
        Item = item;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    private String Item;
    private int Value;
}
