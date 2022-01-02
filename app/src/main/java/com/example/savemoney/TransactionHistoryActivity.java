package com.example.savemoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.savemoney.Adapter.TransactionAdapter;
import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.Model.SpendingModel;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TransactionHistoryActivity extends AppCompatActivity implements TransactionAdapter.Listener {
    DBHelper dbHelper;
    SpendingModel spendingModel;

    RecyclerView rvTransaction, rvDate;
    ArrayList<SpendingModel> arrayList;
    ArrayList<SpendingModel> chitieu;
    ArrayList<SpendingModel> hinhanh;

    TextView txtDes, txtMoney, txtAcc;
    TextView txtIncome, txtExpense;

    Button btnBack;

    ImageView imgCate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        dbHelper = new DBHelper(getApplicationContext());

        txtDes = findViewById(R.id.txtDes);
        txtMoney = findViewById(R.id.txtMoney);
        txtAcc = findViewById(R.id.txtAccountUsed);

        txtIncome = findViewById(R.id.txtTotalIncome);
        txtExpense = findViewById(R.id.txtTotolExpense);


        rvTransaction = findViewById(R.id.rvTransactionHistory);
//        rvDate = findViewById(R.id.rvDate);


        //Back
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionHistoryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imgCate=findViewById(R.id.imgCategory);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onResume() {
        String month = getIntent().getStringExtra("month");
        String monthCurrent = String.valueOf(Integer.parseInt(month));
        int thu = 0, chi = 0;


        arrayList = dbHelper.getALLSpendAccount(monthCurrent);
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getType().equals("+")) {
                thu += arrayList.get(i).getBalance();
            } else {
                chi += arrayList.get(i).getBalance();
            }
        }

        txtIncome.setText(parseTextMoney(thu) + " đ");
        txtExpense.setText(parseTextMoney(chi) + " đ");


        Collections.sort(arrayList, new Comparator<SpendingModel>() {
            @Override
            public int compare(SpendingModel o1, SpendingModel o2) {
                int tmp;
                tmp=o1.getDate().compareTo(o2.getDate());
                if(tmp==0)
                    tmp=o1.getTime().compareTo(o2.getTime());
                return tmp;
            }
        });
        Collections.reverse(arrayList);

        TransactionAdapter transactionAdapter = new TransactionAdapter(arrayList, getApplicationContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TransactionHistoryActivity.this, LinearLayoutManager.VERTICAL, false);
        rvTransaction.setLayoutManager(linearLayoutManager);

        rvTransaction.setAdapter(transactionAdapter);
        super.onResume();

    }

    private String parseTextMoney(int amount) {
        NumberFormat formatter = new DecimalFormat("#,###");

        return formatter.format((double) amount);
    }

    @Override
    public void onclick(SpendingModel spendingModel) {

        Intent intent = new Intent(this, EditTransactionActivity.class);
        intent.putExtra("spending", spendingModel);
        Log.d("LOG", spendingModel.getDescription());
        startActivity(intent);
    }
}