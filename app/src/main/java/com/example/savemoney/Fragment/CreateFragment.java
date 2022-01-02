package com.example.savemoney.Fragment;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;
import com.example.savemoney.R;
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
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String[] optionSpend = {"Chi tiền", "Thu tiền"};
    private static final String[] pay = {"Ăn uống", "Hoá đơn", "Đi lại", "Khác"};
    private static final String[] collect = {"Lương", "Thưởng", "Lãi"};
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText edtAmount, edtDescription, edtDay, edtTime;
    private AutoCompleteTextView autoSpend, autoType, autoAccount;
    private Button btnSaveDetail;
    private String currentBalance = "";
    private int currentSpend = 0, currentType = -1, currentAccount = -1;
    private int year, month, day, hour, minute;
    private DBHelper dbHelper;
    private List<AccountModel> accountModels;


    public CreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateFragment newInstance(String param1, String param2) {
        CreateFragment fragment = new CreateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    public void intVariable(View view) {
        edtAmount = view.findViewById(R.id.edtAmount);
        autoType = view.findViewById(R.id.autoType);
        autoSpend = view.findViewById(R.id.autoSpend);
        autoAccount = view.findViewById(R.id.autoAccount);
        edtDay = view.findViewById(R.id.edtDay);
        edtTime = view.findViewById(R.id.edtTime);
        edtDescription = view.findViewById(R.id.edtDescription);
        btnSaveDetail = view.findViewById(R.id.btnSaveDetail);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Configuration configuration = getContext().getResources().getConfiguration();
        Locale locale = new Locale("vi");
        Locale.setDefault(locale);
        configuration.setLocale(locale);

        getContext().createConfigurationContext(configuration);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        intVariable(view);
        dbHelper = new DBHelper(requireActivity().getApplicationContext());

        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (!text.equals(currentBalance) && text.length() > 0) {

                    NumberFormat formatter = new DecimalFormat("#,###");

                    String cleanString = text.replaceAll("[$,.]", "");
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = formatter.format(parsed);

                    currentBalance = formatted;
                    edtAmount.setText(formatted);
                    edtAmount.setSelection(formatted.length());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Chi tiet
        ArrayAdapter<String> arrayAdapterType = new ArrayAdapter<String>(getContext(), R.layout.option_item, pay);
//        autoType.setText(optionTypeStatic[0][0], false);
        autoType.setAdapter(arrayAdapterType);

        autoType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentType = position;

            }
        });


        // Thu chi
        ArrayAdapter<String> arrayAdapterSpend = new ArrayAdapter<String>(getContext(), R.layout.option_item, optionSpend);
        autoSpend.setText(optionSpend[currentSpend], false);
        autoSpend.setAdapter(arrayAdapterSpend);

        autoSpend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSpend = position;
                currentType = -1;
                autoType.setText("");

                arrayAdapterType.clear();

                ArrayList<String> temp;
                if (position == 0) {
                    temp = new ArrayList<>(pay.length);
                    for (String item : pay) {
                        temp.add(item);
                    }
                } else {
                    temp = new ArrayList<>(collect.length);
                    for (String item : collect) {
                        temp.add(item);
                    }
                }
                arrayAdapterType.addAll(new ArrayList<>(temp));
                Log.d("LOG", position + "");
                Log.d("LOG", temp.size() + "");
                Log.d("LOG", temp.hashCode() + "");

                arrayAdapterType.notifyDataSetChanged();
            }
        });


        accountModels = dbHelper.getAllAccount();
        ArrayList<String> listAccountName = new ArrayList<>();
        for (AccountModel accountModel : accountModels) {
            listAccountName.add(accountModel.getTitle());
        }

        ArrayAdapter<String> arrayAdapterAccount = new ArrayAdapter<String>(getContext(), R.layout.option_item, listAccountName);
        autoAccount.setAdapter(arrayAdapterAccount);
        autoAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentAccount = position;
            }
        });


        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        long today = MaterialDatePicker.todayInUtcMilliseconds();

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        day = Calendar.getInstance().get(Calendar.DATE);
        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        minute = Calendar.getInstance().get(Calendar.MINUTE);

        edtDay.setText(day + "/" + month + "/" + year);
        edtTime.setText(hour + ":" + minute);

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Chọn ngày giao dịch");
        builder.setSelection(today);

        final MaterialDatePicker<Long> materialDatePicker = builder.build();

        edtDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "datePicker");

            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date(Long.parseLong(selection.toString()));
                year = date.getYear() + 1900;
                month = date.getMonth();
                day = date.getDate();
                edtDay.setText(sdf.format(date));
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
                picker.show(requireActivity().getSupportFragmentManager(), "time_picker");
            }
        });

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = picker.getHour() + ":" + picker.getMinute();
                hour = picker.getHour();
                minute = picker.getMinute();
                edtTime.setText(time);
            }
        });

        btnSaveDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = edtDescription.getText().toString();
                currentBalance = currentBalance.replaceAll("\\.", "");
                if (description.equals("") || currentBalance.equals("") || currentType == -1 || currentAccount == -1) {
                    Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin !", Toast.LENGTH_SHORT).show();
                    return;
                }


                String date = day + "/" + month + "/" + year;
                String time = hour + ":" + minute;

                String type = "-";
                if (currentSpend == 1) type = "+";

                AccountModel accountModel = accountModels.get(currentAccount);
                int balance = Integer.parseInt(currentBalance);


                SpendingModel spendingModel = new SpendingModel(balance, currentType, description, date, time, type, accountModel.getId());
                boolean res = dbHelper.addSpendingAccount(spendingModel);

                if (res) {
                    Toast.makeText(getContext(), "Thêm chi tiêu thành công !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Thêm chi tiêu thất bại !", Toast.LENGTH_SHORT).show();
                }
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.fmContainer, fragment);

                fragmentTransaction.commit();

            }
        });
        return view;
    }


}