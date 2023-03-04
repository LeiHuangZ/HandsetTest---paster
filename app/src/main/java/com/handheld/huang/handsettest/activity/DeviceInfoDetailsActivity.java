package com.handheld.huang.handsettest.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;
import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityDeviceDetailsBinding;
import com.handheld.huang.handsettest.utils.CodeUtil;
import com.handheld.huang.handsettest.utils.MobileInfoUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author LeiHuang
 */
public class DeviceInfoDetailsActivity extends AppCompatActivity {

    private final String TAG = DeviceInfoDetailsActivity.class.getSimpleName();

    private boolean waitToWifiBt;
    private ProgressDialog progressDialog;

    public static final int imeiIntent = 0;
    public static final int wifiMacIntent = 1;
    public static final int serialNumberIntent = 2;
    public static final int btAddressIntent = 3;
    public static final int deviceInfoIntent = 4;

    private final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2, threadFactory);
    private int showFlag;
    private com.handheld.huang.handsettest.databinding.ActivityDeviceDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showFlag = getIntent().getIntExtra("ShowFlag", 0);
        binding.deviceImeiLl.setVisibility(showFlag == imeiIntent ? View.VISIBLE : View.GONE);
        binding.deviceWifiAddressLl.setVisibility(showFlag == wifiMacIntent ? View.VISIBLE : View.GONE);
        binding.deviceSerialNumberLl.setVisibility(showFlag == serialNumberIntent ? View.VISIBLE : View.GONE);
        binding.deviceBtAddressLl.setVisibility(showFlag == btAddressIntent ? View.VISIBLE : View.GONE);
        binding.deviceInfoBarcode.setVisibility(showFlag == deviceInfoIntent ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(DeviceInfoDetailsActivity.this);
        progressDialog.setMessage("正在生成设备信息条码及二维码.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            waitToWifiBt = true;
        }
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            waitToWifiBt = true;
        }

        scheduledExecutorService.execute(() -> {
            try {
                if (waitToWifiBt) {
                    Thread.sleep(1300);
                    waitToWifiBt = false;
                }
                if (showFlag == imeiIntent) {
                    String imei = MobileInfoUtil.getIMEI(DeviceInfoDetailsActivity.this);
                    Bitmap bitmapImei = CodeUtil.creatBarcode(
                            DeviceInfoDetailsActivity.this, imei, 850,
                            200, new PointF(0, 180), true);
                    Log.e(TAG, "onStart imei: " + imei);
                    runOnUiThread(() -> binding.deviceImei.setImageBitmap(bitmapImei));
                } else if (showFlag == wifiMacIntent) {
                    String wifiAddress = MobileInfoUtil.getMacAddr();
                    Bitmap bitmapWifiAddress = CodeUtil.creatBarcode(
                            DeviceInfoDetailsActivity.this, wifiAddress, 850,
                            160, new PointF(0, 180), true);
                    Log.e(TAG, "onCreate wifiAddress: " + wifiAddress);
                    runOnUiThread(() -> binding.deviceWifiAddress.setImageBitmap(
                            bitmapWifiAddress));
                } else if (showFlag == serialNumberIntent) {
                    String serialNumber;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        serialNumber = Build.getSerial();
                    } else {
                        serialNumber = Build.SERIAL;
                    }
                    Bitmap bitmapSerialNumber = CodeUtil.creatBarcode(
                            DeviceInfoDetailsActivity.this, serialNumber, 850,
                            160, new PointF(0, 180), true);
                    Log.e(TAG, "onStart serialNumber: " + serialNumber);
                    runOnUiThread(() -> binding.deviceSerialNumber.setImageBitmap(
                            bitmapSerialNumber));
                } else if (showFlag == btAddressIntent) {
                    String btAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                    Bitmap bitmapBtAddress = CodeUtil.creatBarcode(
                            DeviceInfoDetailsActivity.this, btAddress, 850,
                            160, new PointF(0, 180), true);
                    Log.e(TAG, "onCreate bluetooth address: " + btAddress);
                    runOnUiThread(() -> binding.deviceBtAddress.setImageBitmap(bitmapBtAddress));
                } else if (showFlag == deviceInfoIntent) {
                    String imei = MobileInfoUtil.getIMEI(DeviceInfoDetailsActivity.this);
                    String wifiAddress = MobileInfoUtil.getMacAddr();
                    String serialNumber;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        serialNumber = Build.getSerial();
                    } else {
                        serialNumber = Build.SERIAL;
                    }
                    String btAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                    String qrcode = imei + "\t" + wifiAddress + "\t" + serialNumber +
                            "\t" + btAddress;
                    Bitmap bitmap = CodeUtil.createQRCode(qrcode, 600);
                    runOnUiThread(() -> binding.deviceInfoBarcode.setImageBitmap(bitmap));
                }
                runOnUiThread(() -> progressDialog.dismiss());
            } catch (InterruptedException | WriterException e) {
                e.printStackTrace();
            }
        });
    }

}