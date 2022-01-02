package com.example.savemoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.Model.AccountModel;

public class UpdateActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private int typeAccount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        dbHelper = new DBHelper(getApplicationContext());

        TextView txtSodu = findViewById(R.id.txtSodu);
        EditText edtTien = findViewById(R.id.edtTien);
        EditText edtName = findViewById(R.id.edtName);
        Spinner spinnerWallet = findViewById(R.id.spinnerWallet);
        EditText edtNote = findViewById(R.id.edtNote);
        TextView toolbar2 = findViewById(R.id.toolbar2);
        TextView txtDonvi = findViewById(R.id.txtDonvi);
        Button btnLuu = findViewById(R.id.btnLuu);

        Intent intent = getIntent();
        int accountId = Integer.parseInt(intent.getStringExtra("id"));
        AccountModel accountModel = dbHelper.getAccountById(accountId);

        edtTien.setText(String.valueOf(accountModel.getAmount()));
        edtName.setText(accountModel.getTitle());
        edtNote.setText(accountModel.getNote());
        typeAccount = accountModel.getType();

        spinnerWallet.setSelection(typeAccount);

        //buttonluu
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int amount = Integer.parseInt(edtTien.getText().toString());
                String title = edtName.getText().toString();
                String note = edtNote.getText().toString();
                accountModel.setAmount(amount);
                accountModel.setTitle(title);
                accountModel.setNote(note);
                accountModel.setType(typeAccount);
                dbHelper.updateAccount(accountModel);
                Toast.makeText(getApplicationContext(), "Cập nhập thành công !", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        spinnerWallet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeAccount = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

}
