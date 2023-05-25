package com.example.od_vn;

public class AdminData {
    String name, email, username, rname, role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public AdminData() {}

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public AdminData(String name, String email, String username, String rname, String role) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.rname = rname;
        this.role = role;
    }
}
