package com.handheld.huang.handsettest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import cn.pda.scan.ScanThread;
import cn.pda.serialport.SerialPort;

/**
 * @author LeiHuang
 * 硬解扫描头测试界面
 */
public class BarcodeActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = BarcodeActivity.class.getSimpleName();

    private TextView scanResultTv;
    private ImageView okImg;
    private ImageView crossImg;

    private ScanThread scanThread;
    private final KeyReceiver keyReceiver = new KeyReceiver();
    private MsgHandler msgHandler;
    private Util util;
    private SpUtils spUtils;

    private class KeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            // 为兼容早期版本机器
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (keyCode > 0 && !keyDown) {
                if (scanThread != null) {
                    scanThread.scan();
                }
            }
        }
    }

    private static class MsgHandler extends Handler {
        WeakReference<BarcodeActivity> weakReference;
        MsgHandler(BarcodeActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            BarcodeActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }
            int what = msg.what;
            if (what == ScanThread.SCAN) {
                String scanResult = msg.getData().getString("data");
                activity.scanResultTv.setText(scanResult);
                activity.util.playAudio(1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        scanResultTv = findViewById(R.id.scan_result_tv);
        okImg = findViewById(R.id.onElectricity_img_ok);
        okImg.setOnClickListener(this);
        crossImg = findViewById(R.id.onElectricity_img_cross);
        crossImg.setOnClickListener(this);

        try {
            msgHandler = new MsgHandler(this);
            scanThread = new ScanThread(msgHandler);
            scanThread.start();
            util = new Util(this);
            util.initAudio();
            spUtils = new SpUtils(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        registerReceiver(keyReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(keyReceiver);
        scanThread.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == okImg) {
            Log.e(TAG, "onClick okImg");
            okImg.setImageResource(R.drawable.check_ok_selected);
            spUtils.saveBarcodeCkR(0);
            startActivity(new Intent(this, TestConclusionActivity.class));
            finish();
        } else if (v == crossImg) {
            Log.e(TAG, "onClick crossImg");
            crossImg.setImageResource(R.drawable.check_cross_selected);
            spUtils.saveBarcodeCkR(1);
            startActivity(new Intent(this, TestConclusionActivity.class));
            finish();
        }
    }
}