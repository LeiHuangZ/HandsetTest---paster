package com.handheld.huang.handsettest.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.pda.serialport.SerialPort;

/**
 * @author LeiHuang
 * 测试13，5V
 */
public class Serial13TestActivity extends AppCompatActivity {

    private final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private final ExecutorService executorService = new ThreadPoolExecutor(3, 200, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    private SpUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial13_test);

        TextView tipsTv = findViewById(R.id.tips_tv);

        spUtils = new SpUtils(this);

        executorService.execute(() -> {
            try {
                SerialPort serialPort = new SerialPort(13, 115200, 0);
                InputStream inputStream = serialPort.getInputStream();
                OutputStream outputStream = serialPort.getOutputStream();
                String cmd = "1234";
                outputStream.write(cmd.getBytes(StandardCharsets.UTF_8));
                Thread.sleep(2);
                byte[] response = new byte[4];
                int read = inputStream.read(response);
                if (read == 4) {
                    String s = new String(response, StandardCharsets.UTF_8);
                    if (s.equals(cmd)) {
                        tipsTv.setText(Serial13TestActivity.this.getString(R.string.ttl_test_success));
                        spUtils.saveTTL135VCheckResult(0);
                        scheduleFinish();
                        return;
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tipsTv.setTextColor(Serial13TestActivity.this.getColor(android.R.color.holo_red_light));
                }
                tipsTv.setText(Serial13TestActivity.this.getString(R.string.ttl_test_failed));
                spUtils.saveTTL135VCheckResult(1);
                scheduleFinish();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void scheduleFinish() {
        executorService.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(Serial13TestActivity.this, TestConclusionActivity.class));
            Serial13TestActivity.this.finish();
        });
    }
}