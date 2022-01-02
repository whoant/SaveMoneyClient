package com.example.savemoney.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.savemoney.Adapter.TransactionAdapter;
import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;
import com.example.savemoney.R;
import com.example.savemoney.TransactionHistoryActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int monthSelected = 1;

    PieChart pieChart;
    DBHelper dbHelper;
    ArrayList<SpendingModel> chitieu;
    TextInputEditText tienHienTai;
    AppCompatButton btnLichSu;
    private SharedPreferences sharedPreferences;
    private Toolbar toolBar;

    Spinner spinner;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dbHelper = new DBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        String fullName = sharedPreferences.getString("name", "bạn");

        toolBar = view.findViewById(R.id.toolbarHome);
        toolBar.setTitle("Chào " + fullName);

        tienHienTai = view.findViewById(R.id.edtTotalBalance);
        pieChart = view.findViewById(R.id.pie_chart);

        spinner=view.findViewById(R.id.spinnerMonth);

        monthSelected = Calendar.getInstance().get(Calendar.MONTH);
        spinner.setSelection(monthSelected);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthSelected = position;
                chitieu = dbHelper.getALLSpendAccount(String.valueOf(monthSelected + 1));
                renderChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btnLichSu = view.findViewById(R.id.btnTransactionHistory);
        btnLichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransactionHistoryActivity.class);
                intent.putExtra("month", String.valueOf(monthSelected + 1));
                startActivity(intent);
            }
        });

        List<AccountModel> tempAccountModels = dbHelper.getAccountsToSync();

        int totalAmount = 0;
        for (AccountModel account : tempAccountModels) {
            int tempAmount = account.getAmount();
            for (SpendingModel spend : account.getSpendingModels()) {
                if (spend.getType().equals("-")) {
                    tempAmount -= spend.getBalance();
                } else {
                    tempAmount += spend.getBalance();
                }
            }

            totalAmount += tempAmount;
        }

        NumberFormat formatter = new DecimalFormat("#,###");

        String formatted = formatter.format((double) totalAmount);
        tienHienTai.setText(formatted + " đ");

        return view;
    }

    public void renderChart(){
        int none = 0;
        double thu = 0, chi = 0;
        double percentThu, percentChi;
        for (int i = 0; i < chitieu.size(); i++) {
            if (chitieu.get(i).getType().equals("+")) {
                thu += chitieu.get(i).getBalance();
            } else {
                chi += chitieu.get(i).getBalance();
            }
        }
        double tongthuchi = thu + chi;

        if (tongthuchi != 0) {
            none = 1;
        }

        percentThu = (thu / tongthuchi) * 100;
        percentChi = (chi / tongthuchi) * 100;

        pieChart.clearChart();

        if (none == 0) {
            pieChart.addPieSlice(new PieModel("None", 100, Color.parseColor("#D9D9D9")));
        }

        pieChart.addPieSlice(new PieModel("Thu", (int) percentThu, Color.parseColor("#70ad47")));
        pieChart.addPieSlice(new PieModel("Chi", (int) percentChi, Color.parseColor("#c00000")));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}