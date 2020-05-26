package com.handheld.huang.handsettest.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author huang
 */
public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private boolean mNeedClose = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        ButterKnife.bind(this);

        new SpUtils(this).clearAll();

        // 打开GPS
        Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
        // 打开WiFi
        WifiManager mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null && !mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        // 打开蓝牙
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "onCreate 不支持蓝牙:");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            boolean res = bluetoothAdapter.enable();
            Log.e(TAG, "onCreate :" + res);
        }
        // 声音设置为最大
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_SYSTEM, am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_DTMF, am.getStreamMaxVolume(AudioManager.STREAM_DTMF), AudioManager.FLAG_PLAY_SOUND);
        }
    }

    @OnClick({R.id.main_btn_all_test, R.id.main_btn_mac_test, R.id.main_btn_gps_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_btn_all_test:
                mNeedClose = false;
                startActivity(new Intent(MainActivity.this, DisplayTestActivity.class));
                finish();
                break;
            case R.id.main_btn_mac_test:
                startActivity(new Intent(MainActivity.this, MacTestActivity.class));
                break;
            case R.id.main_btn_gps_test:
                boolean appInstalled = checkAppInstalled(MainActivity.this);
                if (!appInstalled){
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.app_not_installed), Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(MainActivity.this, GpsTestActivity.class));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mNeedClose){
            // 关闭GPS
            Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, false);
            // 关闭WiFi
            WifiManager mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
            }
            // 关闭蓝牙
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.e(TAG, "onCreate 不支持蓝牙:");
                return;
            }
            if (bluetoothAdapter.isEnabled()) {
                boolean res = bluetoothAdapter.disable();
                Log.e(TAG, "onCreate :" + res);
            }
        }
        super.onDestroy();
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
            }
        }
        return false;
    }

}
