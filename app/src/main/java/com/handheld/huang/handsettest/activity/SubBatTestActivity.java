package com.handheld.huang.handsettest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.handheld.huang.handsettest.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class SubBatTestActivity extends AppCompatActivity {

    private static final String TAG = "SubBatTestActivity";

    private TextView powerStat;
    private TextView subBatCapa;
    private TextView activateSubBatCharge;
    private BatteryManager batteryManager;

    private final Runnable updateCapaTask = new Runnable() {
        @Override
        public void run() {
            int capacity = readFileValue("/sys/devices/platform/11016000.i2c/i2c-5/5-0055/power_supply/bq27542-0/capacity");
            if (capacity < 0) {
                subBatCapa.setText("未识别到手柄电池");
            } else {
                subBatCapa.setText("手柄电池电量：" + capacity + "%");
            }
            handler.postDelayed(updateCapaTask, 1000);
        }
    };

    private final Runnable updateChargeTask = new Runnable() {
        @Override
        public void run() {
            activateSubBatCharge.setText("取消手柄电池给主电充电:" + getCurrentNow() + "mA");
            handler.postDelayed(updateChargeTask, 2000);
        }
    };

    private final BroadcastReceiver powerConnectReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), Intent.ACTION_POWER_CONNECTED)) {
                Log.i(TAG, "onReceive: " + intent.getAction());
                updatePowerStat();
            } else if (Objects.equals(intent.getAction(), Intent.ACTION_POWER_DISCONNECTED)) {
                Log.i(TAG, "onReceive: " + intent.getAction());
                SystemClock.sleep(300);
                updatePowerStat();
            }
        }
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            updateSubBatCapa();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_bat_test);

        powerStat = findViewById(R.id.power_stat);
        subBatCapa = findViewById(R.id.sub_bat_capa);
        activateSubBatCharge = findViewById(R.id.activate_sub_bat_charge);

        /*
         * 监听USB拔插，非通用USB拔插，是GPIO42的中断拔插判断，区分手柄电池充电和USB充电的
         */
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        if (isCharging) {
            updatePowerStat();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(powerConnectReceiver, intentFilter);

        /*
         * 获取手柄电池电量
         */
        updateSubBatCapa();
//        subBatCapa.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateSubBatCapa();
//            }
//        });


        /*
         * 切换手柄电池给主电充电
         */
        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);

        int gpio_enboost = readFileValue("/sys/devices/platform/module_power/gpio_enboost");
        if (gpio_enboost == 0) {
            activateSubBatCharge.setText("取消手柄电池给主电充电");
            activateSubBatCharge.append(":" + getCurrentNow() + "mA");
        } else {
            activateSubBatCharge.setText("激活手柄电池给主电充电");
        }
        activateSubBatCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int gpio_enboost = readFileValue("/sys/devices/platform/module_power/gpio_enboost");
                if (gpio_enboost == 0) {
                    boolean b = writeFileValue("/sys/devices/platform/module_power/gpio_enboost", "2");
                    if (b) {
                        activateSubBatCharge.setText("激活手柄电池给主电充电");
                        handler.removeCallbacks(updateChargeTask);
                    }
                } else {
                    boolean b = writeFileValue("/sys/devices/platform/module_power/gpio_enboost", "0");
                    if (b) {
                        activateSubBatCharge.setText("取消手柄电池给主电充电");
                        handler.postDelayed(updateChargeTask, 2500);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(powerConnectReceiver);
        handler.removeCallbacks(updateCapaTask);
        handler.removeCallbacks(updateChargeTask);
        super.onDestroy();
    }

    private void updatePowerStat() {
        int usb_in = readFileValue("/sys/devices/platform/module_power/usb_in");
        if (usb_in == 0) {
            powerStat.setText("没有检测到USB插入");
            powerStat.setTextColor(Color.RED);
        } else {
            powerStat.setText("检测到USB插入");
            powerStat.setTextColor(Color.GREEN);
        }
    }

    private void updateSubBatCapa() {
//        int capacity = readFileValue("/sys/devices/platform/11016000.i2c/i2c-5/5-0055/power_supply/bq27542-0/capacity");
//        if (capacity < 0) {
//            subBatCapa.setText("未识别到手柄电池");
//        } else {
//            subBatCapa.setText("手柄电池电量：" + capacity + "%");
//        }
        handler.postDelayed(updateCapaTask, 1000);
    }

    private double getCurrentNow() {
        double currentMa = 0;
        if (batteryManager != null) {
            // 获取瞬时电流，单位为微安 (μA)，正数表示充电，负数表示放电
            int currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            // 转换为毫安 (mA)
            currentMa = currentNow / 1000.0;
        }
        return currentMa;
    }

    // 复用字节缓冲区，作为成员变量（避免每次分配）
    private final byte[] mBuffer = new byte[8];

    private int readFileValue(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            int len = fis.read(mBuffer);
            if (len > 0) {
                int value = 0;
                for (int i = 0; i < len; i++) {
                    byte b = mBuffer[i];
                    if (b >= '0' && b <= '9') {
                        value = value * 10 + (b - '0');
                    } else if (b == '\n' || b == '\r') {
                        break;  // Parsing stops upon encountering a newline character.
                    }
                    // Ignore other characters (such as spaces, minus signs, etc.)
                }
                return value;
            }
        } catch (Exception e) {
            Log.d("hcz", "Read file value exception = " + e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    Log.d("hcz", "Read file value close fis exception = " + e);
                }
            }
        }
        return -1;
    }

    private boolean writeFileValue(String path, String value) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(value.getBytes());
            fos.flush();
            return true;
        } catch (Exception e) {
            Log.d("hcz", "Write file value exception = " + e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    Log.d("hcz", "Write file value close fos exception = " + e);
                }
            }
        }
        return false;
    }
}