package com.example.od_vn;

public class TableRe {
    String rank, id ,name, value;



    public TableRe(String rank, String id, String name, String value) {
        this.rank = rank;
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
