package com.example.od_vn;

public class StaffData {
    public StaffData() {}

    private int id;
    private String email, name, role, username, rname, evaluate, adminusername;
    private String Tablemake;

    public String getTablemake() {
        return Tablemake;
    }

    public void setTablemake(String tablemake) {
        Tablemake = tablemake;
    }

    public void setAdminusername(String adminusername) {this.adminusername = adminusername;}
    public String getAdminusername() {return this.adminusername;}

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getRname() {return rname;}

    public String getEvaluate() {return evaluate;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StaffData(String name, String email, String username, String role, String rname, String evaluate, String adminusername) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.role = role;
        this.rname = rname;
        this.evaluate = evaluate;
        this.id = 1;
        this.adminusername = adminusername;
    }
}
