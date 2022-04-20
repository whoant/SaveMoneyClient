package com.example.savemoney.Services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.savemoney.API.ApiService;
import com.example.savemoney.API.model.ActivitiesResponse;
import com.example.savemoney.Database.DBHelper;
import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;
import com.example.savemoney.Receiver.AlarmReceiver;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private SharedPreferences sharedPreferences;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private DBHelper dbHelper;
    private int hour, minute;
    private PendingIntent pendingIntent;

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
            sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            calendar = Calendar.getInstance();

        }

        @Override
        public void handleMessage(Message msg) {
            while (true) {
                synchronized (this) {
                    try {
                        wait(5000);

                        this.syncData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        public void syncData(){
            dbHelper = new DBHelper(getApplicationContext());
            String token = sharedPreferences.getString("token", "");
            ApiService.apiService.get("Bearer " + token).enqueue(new retrofit2.Callback<ActivitiesResponse>() {
                @Override
                public void onResponse(Call<ActivitiesResponse> call, Response<ActivitiesResponse> response) {
                    Log.d("SERVICE", "The service is still running.");
                    if (response.code() != 200) {
                        return;
                    }
                    ActivitiesResponse activitiesResponse = response.body();
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
                        cancelAlarm();
                        setPreference(false);
                    } else {
                        hour = Integer.parseInt(splitTime[0]);
                        minute = Integer.parseInt(splitTime[1]);
                        setAlarm();
                        setPreference(true);
                    }
                }

                @Override
                public void onFailure(Call<ActivitiesResponse> call, Throwable t) {

                }
            });
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

            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }

        @SuppressLint("UnspecifiedImmutableFlag")
        private void cancelAlarm() {
            if (alarmManager == null) {
                alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            }
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.cancel(pendingIntent);
        }

    }



    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

    }
}