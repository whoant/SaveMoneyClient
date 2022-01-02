package com.example.savemoney.API.model;

import com.example.savemoney.Model.AccountModel;

import java.util.List;

public class UpdateRequest {
    private List<AccountModel> data;
    private String time;

    public UpdateRequest(List<AccountModel> data, String time) {
        this.data = data;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<AccountModel> getData() {
        return data;
    }

    public void setData(List<AccountModel> data) {
        this.data = data;
    }
}
