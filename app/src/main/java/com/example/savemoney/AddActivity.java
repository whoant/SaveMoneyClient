package com.example.savemoney;

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

public class AddActivity extends AppCompatActivity {
    DBHelper dbHelper;

    private int typeAccount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(getApplicationContext());

        TextView txtSodu = findViewById(R.id.txtSodu);
        EditText edtTien = findViewById(R.id.edtTien);
        EditText edtName = findViewById(R.id.edtName);
        Spinner spinnerWallet = findViewById(R.id.spinnerWallet);
        EditText edtNote = findViewById(R.id.edtNote);
        TextView toolbar1 = findViewById(R.id.toolbar1);
        TextView txtDonvi = findViewById(R.id.txtDonvi);
        Button btnLuu = findViewById(R.id.btnLuu);
        Button btnHuy = findViewById(R.id.btnHuy);


        //spinnerWallet
        String[] dataWallet = getResources().getStringArray(R.array.spinner_kind);
        spinnerWallet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(AddActivity.this, dataWallet[i], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //buttonluu
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Log.d("LOG",edtTien.getText().toString() );
                int amount = Integer.parseInt(edtTien.getText().toString());
                String title = edtName.getText().toString();
                String note = edtNote.getText().toString();
                AccountModel newAccount = new AccountModel(title, note, amount, typeAccount);
                dbHelper.addAccount(newAccount);
                Toast.makeText(getApplicationContext(), "Thêm thành công !", Toast.LENGTH_SHORT).show();
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

        //buttonhuy
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtTien.setText("");
                edtName.setText("");
                edtNote.setText("");
            }
        });
    }
}
