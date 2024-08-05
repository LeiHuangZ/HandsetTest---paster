//package com.handheld.huang.handsettest.activity;
//
//import android.app.Activity;
//import android.app.Notification;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.Arrays;
//
//public class ColorsLed extends AppCompatActivity {
//    private Button mBtFinish;
//    private Button mBtRed;
//    private Button mBtBlue;
//    private Button mBtGreen;
//    private TextView mstart;
//    private NotificationManager mNotificationMgr = null;
//    private String mThisChannel = "7328";
//    private String mThisChannel2 = "7329";
//    private String mThisChannel3 = "7330";
//    private int mThisNotificationId = 1396826;
//    private boolean mRed = false;
//    private boolean mBlue = false;
//    private boolean mGreen = false;
//    private boolean mDLLed = false;
//    private SharedPreferences mSp;
//
//    Handler myHandler = new Handler() {
//        public void handleMessage(Message paramAnonymousMessage) {
//            Log.d("MMI", "ColorsLed handleMessage mRed = " + mRed + ", mGreen = " + mGreen + ", mDLLed = " + mDLLed);
//            if (mRed && mBlue && mGreen) {
//                cancelNotification();
//            }
//            if (mRed && mBlue && mGreen && mDLLed) {
//                mstart.setText(getString(R.string.finish_result));
//                cancelNotification();
//                openOrCloseFile(false);
//                setPassButtonEnabled(true);
//                mBtBlue.setEnabled(true);
//            }
//            if (!mRed) {
//                Notification.Builder builder = new Notification.Builder(ColorsLed.this, mThisChannel)
//                        .setSmallIcon(R.drawable.unknown)
//                        .setContentTitle("Factory LED TEST")
//                        .setContentText("Factory LED TEST");
//                mNotificationMgr.notify(mThisNotificationId, builder.build());
//                mstart.setText(getString(R.string.red_result));
//                mRed = true;
//                myHandler.sendEmptyMessageDelayed(1, 1500);
//            } else if (!mBlue) {
//                Notification.Builder builder2 = new Notification.Builder(ColorsLed.this, mThisChannel2)
//                        .setSmallIcon(R.drawable.unknown)
//                        .setContentTitle("Factory LED TEST")
//                        .setContentText("Factory LED TEST");
//                mNotificationMgr.notify(mThisNotificationId, builder2.build());
//                mstart.setText(getString(R.string.blue_result));
//                mBlue = true;
//                myHandler.sendEmptyMessageDelayed(1, 1500);
//            } else if (!mGreen) {
//                Notification.Builder builder3 = new Notification.Builder(ColorsLed.this, mThisChannel3)
//                        .setSmallIcon(R.drawable.unknown)
//                        .setContentTitle("Factory LED TEST")
//                        .setContentText("Factory LED TEST");
//                mNotificationMgr.notify(mThisNotificationId, builder3.build());
//                mstart.setText(getString(R.string.green_result));
//                mGreen = true;
//                myHandler.sendEmptyMessageDelayed(1, 1500);
//            } else if (!mDLLed) {
//                mstart.setText(getString(R.string.blue_result));
//                openOrCloseFile(true);
//                mDLLed = true;
//                myHandler.sendEmptyMessageDelayed(1, 1500);
//            }
//        }
//    };
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void onCreate(Bundle paramBundle) {
//        setContentView(R.layout.colors_led);
//        super.onCreate(paramBundle);
//        this.mSp = getSharedPreferences("FactoryMode", 0);
//        mstart = (TextView) findViewById(R.id.text_red);
//        mBtBlue = (Button) findViewById(R.id.blue);
//        mBtBlue.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mRed = false;
//                mBlue = false;
//                mGreen = false;
//                mDLLed = false;
//                myHandler.sendEmptyMessage(1);
//                mBtBlue.setEnabled(false);
//            }
//        });
//
//        mNotificationMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationChannel channel = new NotificationChannel(mThisChannel, "FactoryMode_LED_TEST", NotificationManager.IMPORTANCE_DEFAULT);
//        channel.enableLights(true);
//        channel.setLightColor(Color.RED);
//        channel.setShowBadge(true);
//        channel.setSound(null, null);
//        NotificationChannel channel2 = new NotificationChannel(mThisChannel2, "FactoryMode_LED_TEST", NotificationManager.IMPORTANCE_DEFAULT);
//        channel2.enableLights(true);
//        channel2.setLightColor(Color.BLUE);
//        channel2.setShowBadge(true);
//        channel2.setSound(null, null);
//        NotificationChannel channel3 = new NotificationChannel(mThisChannel3, "FactoryMode_LED_TEST", NotificationManager.IMPORTANCE_DEFAULT);
//        channel3.enableLights(true);
//        channel3.setLightColor(Color.GREEN);
//        channel3.setShowBadge(true);
//        channel3.setSound(null, null);
//        mNotificationMgr.createNotificationChannels(Arrays.asList(channel, channel2, channel3));
//    }
//
//    private void cancelNotification() {
//        mNotificationMgr.cancel(mThisNotificationId);
//    }
//
//
//    public void onPause() {
//        cancelNotification();
//        openOrCloseFile(false);
//        super.onPause();
//    }
//
//    public void onDestroy() {
//        super.onDestroy();
//        openOrCloseFile(false);
//        myHandler.removeMessages(1);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void openOrCloseFile(boolean enabled) {
//        String str;
//        if (enabled) {
//            str = "echo 1 > /sys/devices/platform/module_power/dlledb_en";
//        } else {
//            str = "echo 0 > /sys/devices/platform/module_power/dlledb_en";
//        }
//        try {
//            Process exec = Runtime.getRuntime().exec(new String[]{"sh", "-c", str});
//            Log.d("MMI", "ColorsLed process str : " + str);
//            Log.d("MMI", "ColorsLed process exec.waitFor() : " + exec.waitFor());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
