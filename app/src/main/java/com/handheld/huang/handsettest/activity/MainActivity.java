package com.handheld.huang.handsettest.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityMainBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.util.List;


/**
 * @author huang
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = MainActivity.class.getSimpleName();

    private boolean mNeedClose = true;
    private com.handheld.huang.handsettest.databinding.ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        new SpUtils(this).clearAll();

        // 打开GPS
        Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
        // 打开WiFi
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e(TAG, "Open wifi err: 不支持WLAN");
        } else if (!wifiManager.isWifiEnabled()) {
            boolean enableWifi = wifiManager.setWifiEnabled(true);
            Log.i(TAG, "Open wifi result: " + enableWifi);
        }
        // 打开蓝牙
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Open bluetooth err: 不支持蓝牙");
        } else if (!bluetoothAdapter.isEnabled()) {
            boolean res = bluetoothAdapter.enable();
            Log.i(TAG, "Open bluetooth result: " + res);
        }
        // 声音设置为最大
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_SYSTEM, am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_DTMF, am.getStreamMaxVolume(AudioManager.STREAM_DTMF), AudioManager.FLAG_PLAY_SOUND);
        }

        findViewById(R.id.main_btn_all_test).setOnLongClickListener(v -> {
            Intent intentEm = new Intent(MainActivity.this, PingActivity.class);
            MainActivity.this.startActivity(intentEm);
            return true;
        });
        Log.i(TAG, "onCreate >>>>>>>>>");
        binding.mainBtnAllTest.setOnClickListener(this);
        binding.mainBtnMacTest.setOnClickListener(this);
        binding.mainBtnGpsTest.setOnClickListener(this);
        binding.mainBtnItemTest.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy <<<<<<<<<");
        if (mNeedClose) {
            // 关闭GPS
            Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, false);
            // 关闭WiFi
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                Log.e(TAG, "Close wifi err: 不支持WLAN");
            } else if (wifiManager.isWifiEnabled()) {
                boolean b = wifiManager.setWifiEnabled(false);
                Log.e(TAG, "Close wifi result: " + b);
            }
            // 关闭蓝牙
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.e(TAG, "Close bluetooth err: 不支持蓝牙");
            } else if (bluetoothAdapter.isEnabled()) {
                boolean res = bluetoothAdapter.disable();
                Log.i(TAG, "Close bluetooth result :" + res);
            }
        }
        super.onDestroy();
        Log.i(TAG, "onDestroy >>>>>>>>>");
    }

    /**
     * 判断GPS Test Plus应用是否安装
     *
     * @param context 上下文环境，用于获取PackageManager对象
     * @return 判断结果
     */
    private boolean checkAppInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        if (info.isEmpty()) {
            return false;
        }
        for (int i = 0; i < info.size(); i++) {
            if ("com.chartcross.gpstestplus".equals(info.get(i).packageName)) {
                return true;
            } else if ("com.chartcross.gpstest".equals(info.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view == binding.mainBtnAllTest) {
            mNeedClose = false;
            startActivity(new Intent(MainActivity.this, DisplayTestActivity.class));
            finish();
        } else if (view == binding.mainBtnMacTest) {
            startActivity(new Intent(MainActivity.this, MacTestActivity.class));
        } else if (view == binding.mainBtnGpsTest) {
            boolean appInstalled = checkAppInstalled(MainActivity.this);
            if (!appInstalled) {
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.app_not_installed), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(MainActivity.this, GpsTestActivity.class));
            }
        } else if (view == binding.mainBtnItemTest) {
            startActivity(new Intent(MainActivity.this, ItemTestActivity.class));
        }

    }
}
