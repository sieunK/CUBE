package com.example.cube.Setting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.cube.DBHelper;
import com.example.cube.R;

public class MenuAlarmService extends Service {
    MenuAlarmThread thread;
    private DBHelper helper;
    private static SQLiteDatabase db;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myServiceHandler handler = new myServiceHandler();
        thread = new MenuAlarmThread(handler);
        thread.start();
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업

    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            SharedPreferences sf = getSharedPreferences("menuAlarm",MODE_PRIVATE);
//            Boolean GJ = sf.getBoolean("GJ",false);
//            Boolean GJ_m = sf.getBoolean("GJ_m",false);
//            Boolean GJ_l = sf.getBoolean("GJ_l",false);
//            Boolean GJ_d = sf.getBoolean("GJ_d",false);
            Boolean MC = sf.getBoolean("MC",false);
//            Boolean MC_m = sf.getBoolean("MC_m",false);
//            Boolean MC_l = sf.getBoolean("MC_l",false);
//            Boolean MC_d = sf.getBoolean("MC_d",false);
//            Boolean SB = sf.getBoolean("SB",false);
//            Boolean SB_m = sf.getBoolean("SB_m",false);
//            Boolean SB_l = sf.getBoolean("SB_l",false);
//            Boolean SB_d = sf.getBoolean("SB_d",false);
//            Boolean HS = sf.getBoolean("HS",false);
//            Boolean HS_m = sf.getBoolean("HS_m",false);
//            Boolean HS_l = sf.getBoolean("HS_l",false);
//            Boolean HS_d = sf.getBoolean("HS_d",false);

            helper = new DBHelper(getApplicationContext(), "MC_MENU.db", null,1);
            db = helper.getWritableDatabase();
            Cursor cursor = db.rawQuery(" SELECT NAME FROM MC_MENU ",null);
            String mc_list = "";
            if(MC) {
                while( cursor.moveToNext()) {
                    mc_list += cursor.getString(0);
                    mc_list += ",";
                }
            }

            String result = mc_list.substring(0,mc_list.lastIndexOf(","));

            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                notificationManager.createNotificationChannel(notificationChannel);
                builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());

            } else {
                builder = new NotificationCompat.Builder(getApplicationContext());
            }
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
            if(MC) {
                builder.setContentTitle("문창회관 메뉴");
                builder.setContentText(result);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
            }

        }
    };
}
