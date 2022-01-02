package com.example.savemoney.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.savemoney.Model.SpendingModel;
import com.example.savemoney.R;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    Listener listener;
    private final ArrayList<SpendingModel> spendingModels;
    private final int [][] a = {{R.drawable.icon_anuong, R.drawable.icon_hoadon,R.drawable.icon_dilai, R.drawable.icon_khac}, {R.drawable.icon_luong, R.drawable.icon_thuong, R.drawable.icon_lai}};
    private final Context context;

    public TransactionAdapter(ArrayList<SpendingModel> spendingModels, Context context, Listener listener) {
        this.spendingModels = spendingModels;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.layout_item_transactionhistory, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpendingModel spendingModel = spendingModels.get(position);
        holder.txtDes.setText(spendingModel.getDescription());
        holder.txtMoney.setText(parseTextMoney(spendingModel.getBalance()) + " đ");
        holder.txtAcc.setText(String.valueOf(spendingModel.getAccountId()));
        holder.txtTime.setText(spendingModel.getTime());
        holder.txtDate.setText(spendingModel.getDate());

        int op=1;
        if(spendingModel.getType().equals("-")){
            op=0;
        }

        int c = spendingModel.getCategory();
        int d= a[op][c];
        holder.imgCategory.setImageResource(d);

        if(spendingModel.getType().equals("-"))
        {
            holder.txtMoney.setTextColor(Color.parseColor("#FF0000"));
        }
        else
        {
            holder.txtMoney.setTextColor(Color.parseColor("#00c300"));
        }

        if(spendingModel.getAccountId()==1)
        {
            holder.txtAcc.setText("Ví điện tử");
        }
        else
        {
            holder.txtAcc.setText("Tiền mặt");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onclick(spendingModel);
            }
        });
    }


    @Override
    public int getItemCount() {
        return spendingModels.size();
    }

    private String parseTextMoney(int amount) {
        NumberFormat formatter = new DecimalFormat("#,###");

        return formatter.format((double) amount);
    }

    public interface Listener {
        void onclick(SpendingModel spendingModel);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDes, txtMoney, txtAcc, txtTime;
        TextView txtDate;
        ImageView imgCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDes = itemView.findViewById(R.id.txtDes);
            txtMoney = itemView.findViewById(R.id.txtMoney);
            txtAcc = itemView.findViewById(R.id.txtAccountUsed);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgCategory = itemView.findViewById(R.id.imgCategory);
        }
    }
}
