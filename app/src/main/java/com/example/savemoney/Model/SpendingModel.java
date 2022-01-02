package com.example.savemoney.Model;


import java.io.Serializable;

public class SpendingModel implements Serializable {
    private int id;
    private int balance;
    private int category;
    private String description, date, time, type;
    private int accountId;

    public SpendingModel(int id, int balance, int category, String description, String date, String time, String type, int accountId) {
        this.id = id;
        this.balance = balance;
        this.category = category;
        this.description = description;
        this.date = date;
        this.time = time;
        this.type = type;
        this.accountId = accountId;
    }

    public SpendingModel(int balance, int category, String description, String date, String time, String type, int accountId) {
        this.balance = balance;
        this.category = category;
        this.description = description;
        this.date = date;
        this.time = time;
        this.type = type;
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }


}
