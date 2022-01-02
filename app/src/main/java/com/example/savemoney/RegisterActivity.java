package com.example.savemoney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savemoney.API.ApiService;
import com.example.savemoney.API.model.RegisterRequest;
import com.example.savemoney.API.model.RegisterResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtUserName, edtPassword, edtRetypePassword;
    private Button btnRegister, btnSwitchLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        btnSwitchLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = edtFullName.getText().toString();
                String userName = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();
                String retypePassword = edtRetypePassword.getText().toString();

                if (!password.equals(retypePassword)) {
                    makeText("Hai mật khẩu không khớp nhau !");
                    return;
                }

                register(userName, password, fullName);

            }
        });

    }

    private void register(String userName, String password, String fullName) {
        RegisterRequest registerRequest = new RegisterRequest(userName, password, fullName);

        ApiService.apiService.register(registerRequest).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                RegisterResponse registerResponse;
                if (response.code() != 200) {
                    try {
                        Gson gson = new Gson();
                        registerResponse = gson.fromJson(response.errorBody().string(), RegisterResponse.class);
                        makeText(registerResponse.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                registerResponse = response.body();
                makeText(registerResponse.getMessage());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }, 1000);

            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                makeText("Có lỗi kết nối tới server");
            }
        });
    }

    private void makeText(String msg) {
        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    private void init() {
        edtFullName = findViewById(R.id.edtFullName);
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtRetypePassword = findViewById(R.id.edtRetypePassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnSwitchLogin = findViewById(R.id.btnSwitchLogin);
    }

}