package com.example.savemoney.API.model;

public class RegisterRequest {
    private String user, pass, fullName;

    public RegisterRequest(String user, String pass, String fullName) {
        this.user = user;
        this.pass = pass;
        this.fullName = fullName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


}
