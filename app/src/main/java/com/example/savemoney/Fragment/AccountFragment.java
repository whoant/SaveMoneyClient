package com.example.savemoney.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.savemoney.Adapter.AccountModelApdapter;
import com.example.savemoney.AddActivity;
import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;
import com.example.savemoney.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<AccountModel> arraylist;
    private DBHelper dbHelper;
    private AccountModelApdapter accountModelApdapter;
    private ListView list;
    private TextView txtTong, txtLoaiTK, tvNameAccount, tvAccountMoney;


    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /*  return inflater.inflate(R.layout.fragment_account, container, false);*/
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        intVariable(view);
        dbHelper = new DBHelper(requireActivity().getApplicationContext());

        return view;
    }


    public void intVariable(View view) {
        txtTong = view.findViewById(R.id.txtTong);
        txtLoaiTK = view.findViewById(R.id.txtLoaiTK);
        tvNameAccount = view.findViewById(R.id.tvNameAccount);
        tvAccountMoney = view.findViewById(R.id.tvAccountMoney);
        list = view.findViewById(R.id.list);
        //floatingbutton
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);

            }
        });

    }


    @Override
    public void onResume() {
        List<AccountModel> tempAccountModels = dbHelper.getAccountsToSync();
        ArrayList<AccountModel> accountModels = new ArrayList<>(tempAccountModels.size());
        accountModels.addAll(tempAccountModels);

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

        AccountModelApdapter accountModelApdapter = new AccountModelApdapter(requireContext(), R.layout.list_account, accountModels);
        list.setAdapter(accountModelApdapter);

        setTextMoney(totalAmount);

        super.onResume();
    }

    private void setTextMoney(int amount) {

        NumberFormat formatter = new DecimalFormat("#,###");

        String formatted = formatter.format((double) amount);
        txtTong.setText("Tổng tiền: " + formatted + " đ");
    }


}