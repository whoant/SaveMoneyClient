package com.example.savemoney.API.model;

import com.example.savemoney.Model.AccountModel;

import java.util.List;

public class ActivitiesResponse {
    private List<AccountModel> activities;
    private String time, message;


    public ActivitiesResponse(List<AccountModel> activities, String time, String message) {
        this.activities = activities;
        this.time = time;
        this.message = message;
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
