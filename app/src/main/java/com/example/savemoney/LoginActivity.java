package com.example.savemoney;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savemoney.API.ApiService;
import com.example.savemoney.API.model.LoginRequest;
import com.example.savemoney.API.model.LoginResponse;
import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText edtUser, edtPass;
    private Button btnLogin, btnSwitchRegister;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);

        String token = sharedPreferences.getString("token", "");
        if (!token.equals("")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        btnSwitchRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUser.getText().toString();
                String pass = edtPass.getText().toString();
                login(user, pass);

            }
        });
    }

    private void login(String user, String pass) {
        LoginRequest loginRequest = new LoginRequest(user, pass);

        ApiService.apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.code() != 200) {
                    Toast.makeText(LoginActivity.this, "Vui lòng kiểm tra lại tài khoản của bạn !", Toast.LENGTH_SHORT).show();
                    return;
                }
                LoginResponse loginResponse = response.body();
                Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", loginResponse.getToken());
                editor.putString("name", loginResponse.getFullName());

                String time = loginResponse.getTime();
                String[] splitTime = time.split(":");
                if (splitTime[0].equals("-1")) {
                    editor.putBoolean("enable", false);
                } else {
                    editor.putBoolean("enable", true);
                    editor.putInt("hour", Integer.parseInt(splitTime[0]));
                    editor.putInt("minute", Integer.parseInt(splitTime[1]));
                }

                editor.apply();

                dbHelper = new DBHelper(getApplicationContext());

                List<AccountModel> accountModels = loginResponse.getActivities();
                for (AccountModel account : accountModels) {
                    dbHelper.addAccount(account);
                    for (SpendingModel spending : account.getSpendingModels()) {
                        dbHelper.addSpendingAccount(spending);
                    }
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                Toast.makeText(LoginActivity.this, "Đang có vấn đề kết nối tới server !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init() {

        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnSwitchRegister = findViewById(R.id.btnSwitchRegister);
    }

}