package com.example.savemoney.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.Fragment.AccountFragment;
import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;
import com.example.savemoney.R;
import com.example.savemoney.UpdateActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class AccountModelApdapter extends ArrayAdapter<AccountModel> {
    private final DBHelper dbHelper;
    Context context;
    ArrayList<AccountModel> arrayList;
    int layout;

    public AccountModelApdapter(@NonNull Context context, int resource, ArrayList<AccountModel> arrayList) {
        super(context, resource, arrayList);
        this.context = context;
        this.arrayList = arrayList;
        this.layout = resource;
        dbHelper = new DBHelper(context);
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AccountModel accountModel = arrayList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);
        }

//        ImageView imgAccount = convertView.findViewById(R.id.imgAccount);
//        imgAccount.setImageResource(R.drawable.ic_launcher_background);

        TextView tvNameAccount = convertView.findViewById(R.id.tvNameAccount);
        tvNameAccount.setText(accountModel.getTitle());
//
        TextView tvAccountMoney = convertView.findViewById(R.id.tvAccountMoney);
        int currentAmount = accountModel.getAmount();
        for (SpendingModel spend : accountModel.getSpendingModels()) {
            if (spend.getType().equals("-")) {
                currentAmount -= spend.getBalance();
            } else {
                currentAmount += spend.getBalance();
            }
        }

        double parsed = currentAmount;

        NumberFormat formatter = new DecimalFormat("#,###");
        String formatted = formatter.format(parsed);
        tvAccountMoney.setText(formatted + " đ");

        ImageView imgPopup = convertView.findViewById(R.id.imgPopup);

        imgPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, accountModel.getId() + "", Toast.LENGTH_SHORT).show();

                PopupMenu popupMenu = new PopupMenu(context, v, Gravity.END);
                MenuInflater inflater = popupMenu.getMenuInflater();

                inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint({"UseCompatLoadingForDrawables", "NonConstantResourceId"})
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.mnuSua) {
                            Intent intent = new Intent(getContext(), UpdateActivity.class);
                            intent.putExtra("id", String.valueOf(accountModel.getId()));
                            context.startActivity(intent);

                        } else if (menuItem.getItemId() == R.id.mnuXoa) {
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                            builder.setTitle("Xóa tài khoản");
                            builder.setIcon(R.drawable.ic_baseline_warning_24);
                            builder.setMessage("Bạn có muốn xóa không?");
                            builder.setBackground(getContext().getDrawable(R.drawable.alert_dialog_bg));
                            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    arrayList.remove(position);
                                    dbHelper.deleteOne(accountModel);
                                    notifyDataSetChanged();

                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fmContainer, new AccountFragment()).commit();
                                    Toast.makeText(getContext(), "Xoá tài khoảng thành công !", Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                            builder.show();
                        }
                        return false;
                    }
                });

            }
        });

        return convertView;

    }


}
