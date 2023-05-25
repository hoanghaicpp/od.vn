package com.example.od_vn;

public class TableData {
    private String TableNumber;
    private boolean isUsed;

    public TableData() {}

    public TableData(String _TableNumber, boolean _isUsed)
    {
        this.TableNumber = _TableNumber;
        this.isUsed = _isUsed;
    }
    public String getTableNumber() {
        return TableNumber;
    }

    public void setTableNumber(String tableNumber) {
        TableNumber = tableNumber;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
