package com.example.savemoney.Fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.savemoney.API.ApiService;
import com.example.savemoney.API.model.ActivitiesResponse;
import com.example.savemoney.API.model.UpdateRequest;
import com.example.savemoney.API.model.UpdateResponse;
import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.LoginActivity;
import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;
import com.example.savemoney.R;
import com.example.savemoney.Receiver.AlarmReceiver;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private LinearLayout linearAccountSetting, linearUpload, linearDownload, linearNotice, linearNoticeSub, linearLogout;
    private Switch switchNotice;
    private TextView tvShowTime;
    private int hour, minute;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private SharedPreferences sharedPreferences;

    private DBHelper dbHelper;


    private boolean isEnable = false;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        intVariable(view);
        dbHelper = new DBHelper(requireActivity().getApplicationContext());

        calendar = Calendar.getInstance();
        sharedPreferences = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE);

        isEnable = sharedPreferences.getBoolean("enable", false);

        switchNotice.setChecked(isEnable);
        createNotificationChannel();

        LocalTime now = LocalTime.now();
        hour = now.getHour();
        minute = now.getMinute();

        hour = sharedPreferences.getInt("hour", hour);
        minute = sharedPreferences.getInt("minute", minute);

        tvShowTime.setText(hour + " : " + minute);


        if (!isEnable) {
            linearNoticeSub.setVisibility(View.GONE);
        }

        linearNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnable = !switchNotice.isChecked();
                switchNotice.setChecked(isEnable);
            }
        });

        switchNotice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isEnable = isChecked;

                if (isEnable) {

                    linearNoticeSub.setVisibility(View.VISIBLE);
                    setAlarm();
                    setPreference(true);
                    Toast.makeText(requireContext(), "Bật thông báo thành công !", Toast.LENGTH_SHORT).show();

                } else {
                    linearNoticeSub.setVisibility(View.GONE);
                    cancelAlarm();
                    setPreference(false);
                    Toast.makeText(requireContext(), "Tắt thông báo thành công !", Toast.LENGTH_SHORT).show();
                }
            }
        });


        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Thời gian nhắc")
                .build();

        linearNoticeSub.setOnClickListener(new View.OnClickListener() {
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
                tvShowTime.setText(time);
                setPreference(true);
                setAlarm();

            }
        });

        linearDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = sharedPreferences.getString("token", "");
                ApiService.apiService.get("Bearer " + token).enqueue(new Callback<ActivitiesResponse>() {
                    @Override
                    public void onResponse(Call<ActivitiesResponse> call, Response<ActivitiesResponse> response) {
                        if (response.code() != 200) {
                            Toast.makeText(requireContext(), "Có lỗi xảy ra !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ActivitiesResponse activitiesResponse = response.body();
                        Toast.makeText(requireContext(), activitiesResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        dbHelper.deleteAllDatabase();


                        List<AccountModel> accountModels = activitiesResponse.getActivities();
                        for (AccountModel account : accountModels) {

                            dbHelper.addAccount(account);
                            for (SpendingModel spending : account.getSpendingModels()) {
                                dbHelper.addSpendingAccount(spending);
                            }
                        }

                        String time = activitiesResponse.getTime();
                        String[] splitTime = time.split(":");

                        if (splitTime[0].equals("-1")) {
                            switchNotice.setChecked(false);
                            linearNoticeSub.setVisibility(View.GONE);
                            cancelAlarm();
                            setPreference(false);
                        } else {
                            switchNotice.setChecked(true);
                            linearNoticeSub.setVisibility(View.VISIBLE);
                            hour = Integer.parseInt(splitTime[0]);
                            minute = Integer.parseInt(splitTime[1]);
                            setAlarm();
                            setPreference(true);
                            tvShowTime.setText(splitTime[0] + " : " + splitTime[1]);
                        }
                    }

                    @Override
                    public void onFailure(Call<ActivitiesResponse> call, Throwable t) {

                    }
                });
            }
        });


        linearUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AccountModel> accountModels = dbHelper.getAccountsToSync();
                String token = sharedPreferences.getString("token", "");
                int hour = sharedPreferences.getInt("hour", -1);
                int minute = sharedPreferences.getInt("minute", -1);

                UpdateRequest updateRequest = new UpdateRequest(accountModels, hour + ":" + minute);
                ApiService.apiService.update("Bearer " + token, updateRequest).enqueue(new Callback<UpdateResponse>() {
                    @Override
                    public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {

                        if (response.code() == 401) {

                            Toast.makeText(requireContext(), "Tài khoản của bạn đã bi xoá !", Toast.LENGTH_SHORT).show();
                            removeToken();

                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().onBackPressed();

                            return;
                        } else if (response.code() != 200) {
                            Toast.makeText(requireContext(), "Có lỗi xảy ra !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        UpdateResponse updateResponse = response.body();
                        Toast.makeText(requireContext(), updateResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<UpdateResponse> call, Throwable t) {

                    }
                });

            }
        });

        linearLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeToken();
                clearDatabase();
                Toast.makeText(requireContext(), "Đăng xuất thành công !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().onBackPressed();

            }
        });

        return view;
    }

    private void removeToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token").apply();
        editor.remove("fullName").apply();
        setPreference(false);
    }


    private void clearDatabase() {
        dbHelper.deleteAllDatabase();
    }

    private void setPreference(boolean isAdd) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isAdd) {
            editor.putInt("hour", hour);
            editor.putInt("minute", minute);
            editor.putBoolean("enable", true);
        } else {
            editor.remove("hour");
            editor.remove("minute");
            editor.putBoolean("enable", false);
        }
        editor.apply();

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void setAlarm() {

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Log.d("LOG", calendar.getTimeInMillis() + "");

        alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(requireContext(), "Cài đặt thời gian thành công ", Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void cancelAlarm() {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        }
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "vovanhoangtuan";
            String description = "Thong bao";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("savemoney", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void intVariable(View view) {
        linearUpload = view.findViewById(R.id.linearUpload);
        linearDownload = view.findViewById(R.id.linearDownload);
        linearLogout = view.findViewById(R.id.linearLogout);
        linearNotice = view.findViewById(R.id.linearNotice);
        linearNoticeSub = view.findViewById(R.id.linearNoticeSub);
        switchNotice = view.findViewById(R.id.switchNotice);
        tvShowTime = view.findViewById(R.id.tvShowTime);
    }

}