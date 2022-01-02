package com.example.savemoney.API.model;

import com.example.savemoney.Model.AccountModel;

import java.util.List;

public class LoginResponse {
    private String fullName, token, message, time;
    private List<AccountModel> activities;

    public LoginResponse(String fullName, String token, String message) {
        this.fullName = fullName;
        this.token = token;
        this.message = message;
    }

    public LoginResponse(String fullName, String token, String message, String time, List<AccountModel> activities) {
        this.fullName = fullName;
        this.token = token;
        this.message = message;
        this.time = time;
        this.activities = activities;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<AccountModel> getActivities() {
        return activities;
    }

    public void setActivities(List<AccountModel> activities) {
        this.activities = activities;
    }
}
