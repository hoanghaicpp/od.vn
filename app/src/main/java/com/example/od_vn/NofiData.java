package com.example.od_vn;

public class NofiData {
    private String name, time, nofi;

    public NofiData(String name, String time, String nofi) {
        this.name = name;
        this.time = time;
        this.nofi = nofi;
    }

    public NofiData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNofi() {
        return nofi;
    }

    public void setNofi(String nofi) {
        this.nofi = nofi;
    }
}
