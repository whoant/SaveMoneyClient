package com.example.savemoney.utiles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.example.savemoney.API.ApiService;
import com.example.savemoney.API.model.ActivitiesResponse;
import com.example.savemoney.API.model.UpdateRequest;
import com.example.savemoney.API.model.UpdateResponse;
import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.LoginActivity;
import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Helpers {
    private SharedPreferences sharedPreferences;


    public Helpers(DBHelper dbHelper) {

    }

}
