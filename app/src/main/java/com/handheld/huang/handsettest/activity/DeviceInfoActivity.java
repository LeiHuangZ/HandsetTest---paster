package com.handheld.huang.handsettest.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;
import com.handheld.huang.handsettest.databinding.ActivityDeviceInfoBinding;
import com.handheld.huang.handsettest.utils.CodeUtil;
import com.handheld.huang.handsettest.utils.MobileInfoUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author LeiHuang
 */
public class DeviceInfoActivity extends AppCompatActivity {
    private final String TAG = DeviceInfoActivity.class.getSimpleName();
    private final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2, threadFactory);
    private ProgressDialog progressDialog;
    private boolean waitToWifiBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.handheld.huang.handsettest.databinding.ActivityDeviceInfoBinding binding = ActivityDeviceInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.showImei.setOnClickListener(v -> jumpToDetails(DeviceInfoDetailsActivity.imeiIntent));
        binding.showWifiMac.setOnClickListener(v -> jumpToDetails(DeviceInfoDetailsActivity.wifiMacIntent));
        binding.showSerialNumber.setOnClickListener(v -> jumpToDetails(DeviceInfoDetailsActivity.serialNumberIntent));
        binding.showBtMac.setOnClickListener(v -> jumpToDetails(DeviceInfoDetailsActivity.btAddressIntent));
        binding.showAllInfo.setOnClickListener(v -> jumpToDetails(DeviceInfoDetailsActivity.deviceInfoIntent));
    }

    private void jumpToDetails(int intExtra) {
        Intent intent = new Intent(this, DeviceInfoDetailsActivity.class);
        intent.putExtra("ShowFlag", intExtra);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在打开设备开关.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

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

        scheduledExecutorService.execute(() -> {
            try {
                if (waitToWifiBt) {
                    Thread.sleep(1300);
                    waitToWifiBt = false;
                }
                runOnUiThread(() -> progressDialog.dismiss());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}