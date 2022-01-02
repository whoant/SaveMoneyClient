package com.example.savemoney.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.savemoney.Model.SpendingModel;
import com.example.savemoney.R;

import java.util.ArrayList;

public class EditTransactionAdapter extends RecyclerView.Adapter<EditTransactionAdapter.ViewHolder> {

    private final ArrayList<SpendingModel> spendingModels;
    private final Context context;

    public EditTransactionAdapter(ArrayList<SpendingModel> spendingModels, Context context) {
        this.spendingModels = spendingModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.layout_item_transactionhistory, parent, false);

        return new EditTransactionAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpendingModel spendingModel = spendingModels.get(position);
        holder.edtDescription.setText(spendingModel.getDescription());
        holder.edtBalance.setText(String.valueOf(spendingModel.getBalance()));

    }

    @Override
    public int getItemCount() {
        return spendingModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText edtDescription, edtBalance;
        Button btnDeleteEdit;
        EditTransactionAdapter adapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edtDescription = itemView.findViewById(R.id.edtDescriptionEdit);
            edtBalance = itemView.findViewById(R.id.edtMon);

            btnDeleteEdit = itemView.findViewById(R.id.btnDeleteEdit);

        }
    }
}
