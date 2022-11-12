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
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;
import com.handheld.huang.handsettest.R;
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

    private ImageView imgImei;
    private ImageView imgWiFiMac;
    private ImageView imgSerialNumber;
    private ImageView imgBtAddress;
    private ImageView imgDeviceInfo;

    private boolean waitToWifiBt;
    private ProgressDialog progressDialog;

    private final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2, threadFactory);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_barcode);

        imgImei = findViewById(R.id.device_imei);
        imgWiFiMac = findViewById(R.id.device_wifi_address);
        imgSerialNumber = findViewById(R.id.device_serial_number);
        imgBtAddress = findViewById(R.id.device_bt_address);
        imgDeviceInfo = findViewById(R.id.device_info_barcode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(DeviceInfoActivity.this);
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
                String imei = MobileInfoUtil.getIMEI(DeviceInfoActivity.this);
                Bitmap bitmapImei = CodeUtil.creatBarcode(DeviceInfoActivity.this, imei, 800, 160, new PointF(0, 140), false);
                Log.e(TAG, "onStart imei: " + imei);
                String wifiAddress = MobileInfoUtil.getMacAddr();
                Bitmap bitmapWifiAddress = CodeUtil.creatBarcode(DeviceInfoActivity.this, wifiAddress, 800, 160, new PointF(0, 140), false);
                Log.e(TAG, "onCreate wifiAddress: " + wifiAddress);
                String serialNumber;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    serialNumber = Build.getSerial();
                } else {
                    serialNumber = Build.SERIAL;
                }
                Bitmap bitmapSerialNumber = CodeUtil.creatBarcode(DeviceInfoActivity.this, serialNumber, 800, 160, new PointF(0, 140), false);
                Log.e(TAG, "onStart serialNumber: " + serialNumber);
                String btAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                Bitmap bitmapBtAddress = CodeUtil.creatBarcode(DeviceInfoActivity.this, btAddress, 800, 160, new PointF(0, 140), false);
                Log.e(TAG, "onCreate bluetooth address: " + btAddress);
                String qrcode = imei + "\t" + wifiAddress + "\t" + serialNumber + "\t" + btAddress;
                Bitmap bitmap = CodeUtil.createQRCode(qrcode, 400);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    imgImei.setImageBitmap(bitmapImei);
                    imgWiFiMac.setImageBitmap(bitmapWifiAddress);
                    imgSerialNumber.setImageBitmap(bitmapSerialNumber);
                    imgBtAddress.setImageBitmap(bitmapBtAddress);
                    imgDeviceInfo.setImageBitmap(bitmap);
                });
            } catch (InterruptedException | WriterException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }
}