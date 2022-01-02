package com.example.savemoney;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditTransactionActivity extends AppCompatActivity {
    private static final String[] optionSpend = {"Chi tiền", "Thu tiền"};
    private static final String[][] optionTypeStatic = {{"Ăn uống", "Hoá đơn", "Đi lại", "Khác"}, {"Lương", "Thưởng", "Lãi"}};

    private AutoCompleteTextView autoSpend, autoType, autoAccount, edtAccount;
    private EditText edtBalance, edtDescription, edtDate, edtTime;
    private AutoCompleteTextView edtSpend, edtType;
    private Button btnSaveEdit, btnDeleteEdit;
    private DBHelper dbHelper;
    private String currentBalance = "", currentDate = "", currentTime = "";
    private int currentSpend = 0, currentType = 0, currentAccount = -1;
    private List<AccountModel> accountModels;
    private SpendingModel spendingModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        initVariables();
        dbHelper = new DBHelper(getApplicationContext());

        Intent intent = getIntent();
        spendingModel = (SpendingModel) intent.getSerializableExtra("spending");

        if (spendingModel.getType().equals("+")) currentType = 1;

        currentSpend = spendingModel.getCategory();
        currentBalance = parseTime(String.valueOf(spendingModel.getBalance()));
        edtBalance.setText(currentBalance);
        edtBalance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (!text.equals(currentBalance) && text.length() > 0) {

                    currentBalance = parseTime(text);
                    edtBalance.setText(currentBalance);
                    edtBalance.setSelection(currentBalance.length());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ArrayAdapter<String> arrayAdapterSpend = new ArrayAdapter<String>(getApplicationContext(), R.layout.option_item, optionTypeStatic[currentType]);
        edtType.setAdapter(arrayAdapterSpend);
        edtType.setText(optionTypeStatic[currentType][currentSpend], false);

        edtType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSpend = position;
            }
        });

        ArrayAdapter<String> arrayAdapterType = new ArrayAdapter<String>(getApplicationContext(), R.layout.option_item, optionSpend);
        edtSpend.setAdapter(arrayAdapterType);
        edtSpend.setText(optionSpend[currentType], false);

        edtSpend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentType = position;
                currentSpend = -1;
                edtType.setText("");
                arrayAdapterSpend.clear();
                arrayAdapterSpend.addAll(optionTypeStatic[currentType]);
                arrayAdapterType.notifyDataSetChanged();

            }
        });

        edtDescription.setText(spendingModel.getDescription());

        accountModels = dbHelper.getAllAccount();
        ArrayList<String> listAccountName = new ArrayList<>();

        int i = 0;
        for (AccountModel accountModel : accountModels) {
            listAccountName.add(accountModel.getTitle());
            if (accountModel.getId() == spendingModel.getAccountId()) currentAccount = i;
            i++;
        }

        ArrayAdapter<String> arrayAdapterAccount = new ArrayAdapter<String>(getApplicationContext(), R.layout.option_item, listAccountName);
        edtAccount.setAdapter(arrayAdapterAccount);
        edtAccount.setText(listAccountName.get(currentAccount), false);

        edtAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentAccount = position;
            }
        });

        long today = MaterialDatePicker.todayInUtcMilliseconds();
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Chọn ngày giao dịch");
        builder.setSelection(today);

        final MaterialDatePicker<Long> materialDatePicker = builder.build();

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "datePicker");

            }
        });

        currentDate = spendingModel.getDate();
        edtDate.setText(currentDate);
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date(Long.parseLong(selection.toString()));
                currentDate = sdf.format(date);
                edtDate.setText(currentDate);
            }
        });

        LocalTime now = LocalTime.now();
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(now.getHour())
                .setMinute(now.getMinute())
                .setTitleText("Thời gian")
                .build();

        edtTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "time_picker");
            }
        });

        currentTime = spendingModel.getTime();
        edtTime.setText(currentTime);
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = picker.getHour() + ":" + picker.getMinute();
                currentTime = time;
                edtTime.setText(time);
            }
        });

//        imgCate=findViewById(R.id.imgCategory);
//        int op=0;
//        int [][] a = {{R.drawable.icon_anuong, R.drawable.icon_hoadon,R.drawable.icon_dilai, R.drawable.icon_khac}, {R.drawable.icon_luong, R.drawable.icon_thuong, R.drawable.icon_lai}};
//        if(spendingModel.getType()=="-"){op=0;}
//        else {op=1;}
//
//        imgCate.setImageResource(a[op][currentSpend]);
//        Log.d("LOG",op+"");
//        Log.d("LOG",spendingModel.getType());
//        Log.d("LOG",currentSpend+"");

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBalance =  currentBalance.replaceAll("\\,", "");


                String description = edtDescription.getText().toString();

                if (description.equals("") || strBalance.equals("") || strBalance.equals("0")|| currentType == -1 || currentAccount == -1) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đủ thông tin !", Toast.LENGTH_SHORT).show();
                    return;
                }

                String type = "-";
                if (currentType == 1) type = "+";

                AccountModel accountModel = accountModels.get(currentAccount);

                int balance = Integer.parseInt(strBalance);


                Log.d("LOG", balance + "");
                Log.d("LOG", description + "");
                Log.d("LOG", currentDate + "");
                Log.d("LOG", currentTime + "");
                Log.d("LOG", type + "");
                Log.d("LOG", balance + "");
                Log.d("LOG", currentSpend + "");
                Log.d("LOG", accountModel.getId() + "");

                spendingModel.setDescription(description);
                spendingModel.setBalance(balance);
                spendingModel.setDate(currentDate);
                spendingModel.setTime(currentTime);
                spendingModel.setType(type);
                spendingModel.setCategory(currentSpend);
                spendingModel.setAccountId(accountModel.getId());
                dbHelper.updateSpending(spendingModel);
                finish();

            }
        });

        btnDeleteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditTransactionActivity.this);
                builder.setTitle("Xoá chi tiêu");
                builder.setIcon(R.drawable.ic_baseline_warning_24);
                builder.setMessage("Bạn có muốn xoá chi tiêu này không ?");
                builder.setNegativeButton("Không", null);
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteSpending(spendingModel);
                        finish();
                    }
                });

                builder.show();
            }
        });
    }


    private void initVariables() {
        edtBalance = findViewById(R.id.edtMon);
        edtDescription = findViewById(R.id.edtDescriptionEdit);
        edtDate = findViewById(R.id.edtDayEdit);
        edtTime = findViewById(R.id.edtTimeEdit);
        edtAccount = findViewById(R.id.autoAccountEdit);
        edtSpend = findViewById(R.id.autoSpendEdit);
        edtType = findViewById(R.id.autoTypeEdit);
        btnSaveEdit = findViewById(R.id.btnSaveEdit);
        btnDeleteEdit = findViewById(R.id.btnDeleteEdit);

    }

    private String parseTime(String text){
        NumberFormat formatter = new DecimalFormat("#,###");
        String cleanString = text.replaceAll("[$,.]", "");
        double parsed = Double.parseDouble(cleanString);
        return formatter.format(parsed);
    }
}