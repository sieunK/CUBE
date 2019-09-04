package com.example.cube.Setting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.cube.DBHelper;
import com.example.cube.DefaultActivity;
import com.example.cube.MoonChang.MoonChangMenuFragment;
import com.example.cube.Opening.MainActivity;
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
            int iii=0;
            if(MC) {
                while( cursor.moveToNext()) {
                    mc_list += cursor.getString(0);
                    if(iii<2) {
                        mc_list += "\t";
                        ++iii;
                    } else {
                        mc_list += "\n";
                        iii=0;
                    }
                }
            }

            String channelId = "channel4";
            String channelName = "학식 메뉴알림";
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(getApplicationContext(), DefaultActivity.class);
            notificationIntent.putExtra("식단알림", "문창"); //전달할 값
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo)) //BitMap 이미지 요구
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentTitle("식단 알림")
                    .setContentText("문창회관 메뉴")
                    // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(mc_list))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 DefaultActivity로 이동하도록 설정
                    .setAutoCancel(true);

            //OREO API 26 이상에서는 채널 필요
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setSmallIcon(R.drawable.ic_logo); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
                String description = "오레오 이상을 위한 것임";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel(channelId, channelName , importance);
                channel.setDescription(description);
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
                channel.setVibrationPattern(new long[]{100, 200, 100, 200});
                channel.enableVibration(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();

                channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), att);

                // 노티피케이션 채널을 시스템에 등록
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);

            }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

            assert notificationManager != null;
            notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴

        }
    }

}

