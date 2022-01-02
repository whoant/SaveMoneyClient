package com.example.savemoney.Model;

import java.util.List;

public class AccountModel {
    private int id;
    private String title, note;
    private int amount;
    private int type;
    private List<SpendingModel> spendingModels;

    public AccountModel(int id, String title, String note, int amount, int type) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.amount = amount;
        this.type = type;
    }

    public AccountModel(String title, String note, int amount, int type) {
        this.title = title;
        this.note = note;
        this.amount = amount;
        this.type = type;
    }

    public AccountModel(int id, String title, String note, int amount, int type, List<SpendingModel> spendingModels) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.amount = amount;
        this.type = type;
        this.spendingModels = spendingModels;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public List<SpendingModel> getSpendingModels() {
        return spendingModels;
    }

    public void setSpendingModels(List<SpendingModel> spendingModels) {
        this.spendingModels = spendingModels;
    }
}
