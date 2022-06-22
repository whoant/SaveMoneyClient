package com.example.savemoney.API.model;

import com.example.savemoney.Model.AccountModel;

import java.util.List;

public class ActivitiesResponse {
    private List<AccountModel> activities;
    private String time, message;
    private Boolean isUpdate;


    public ActivitiesResponse(List<AccountModel> activities, String time, String message, Boolean isUpdate) {
        this.activities = activities;
        this.time = time;
        this.message = message;
        this.isUpdate = isUpdate;
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

    public Boolean getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(Boolean update) {
        isUpdate = update;
    }
}
