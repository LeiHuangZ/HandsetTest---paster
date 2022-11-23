package com.handheld.huang.handsettest.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityPingBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author LeiHuang
 */
public class PingActivity extends AppCompatActivity {
    private static final String TAG = PingActivity.class.getSimpleName();

    private final String LOG_PATH =  Environment.getExternalStorageDirectory().getPath() + "/Ringtones/pzisnbg.txt";

    private ActivityPingBinding binding;

    private PingMsgHandler handler;

    private PingLogRvAdapter rvAdapter;

    private final ScheduledExecutorService scheduledExecutorService;

    private volatile boolean networkAvailable;

    private FileWriter fileWriter;

    public PingActivity() {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(3, threadFactory);
    }

    private void addError(String paramString) {
        Message message = new Message();
        message.what = 1000;
        message.obj = paramString;
        this.handler.sendMessage(message);
        saveLog(paramString);
    }

    private void addLog(String log) {
        Log.i(TAG, "addLog log: " + log);
        String suffixDup = "(DUP!)";
        if (log.contains(suffixDup)) {
            return;
        }
        Message message = new Message();
        message.what = 1001;
        message.obj = log;
        this.handler.sendMessage(message);
        saveLog(log);
    }

    private void checkNetwork() {
        new InternetCheck(networkAvailable -> {
            Log.e(TAG, "checkNetwork networkAvailable: " + networkAvailable);
            if (!networkAvailable) {
                Toast.makeText(this, "无网络连接", Toast.LENGTH_SHORT).show();
            }
            this.networkAvailable = networkAvailable;
        }).execute();
    }

    private void startPing() {
        if (!networkAvailable) {
            binding.btnStartPing.setBackgroundColor(ContextCompat.getColor(PingActivity.this, R.color.colorPrimary));
            binding.btnStartPing.setEnabled(true);
            try {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "不支持的设备", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        this.rvAdapter.clearLog();
        this.scheduledExecutorService.execute(() -> {
            String ip = binding.etIp.getText().toString().trim();
            String count = binding.etCount.getText().toString().trim();
            String size = binding.etSize.getText().toString().trim();
            if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(count) || TextUtils.isEmpty(size)) {
                binding.btnStartPing.setBackgroundColor(ContextCompat.getColor(PingActivity.this, R.color.colorPrimary));
                binding.btnStartPing.setEnabled(true);
                addError("填写正确参数");
                return;
            }
            initFileSaver();
            String command = "ping -s " + size + " -c " + count + " " + ip;
            Process process = null;
            addLog("PING 开始");
            try {
                process = Runtime.getRuntime().exec(command);
                ReadThread dataThread = new ReadThread(process.getInputStream());
                ReadThread errorThread = new ReadThread(process.getErrorStream());
                dataThread.start();
                errorThread.start();
                dataThread.join();
                errorThread.join();
                int waitFor = process.waitFor();
                addLog("PING 结束 " + waitFor);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                addError(e.getMessage());
            } finally {
                if (process != null) {
                    process.destroy();
                }
                runOnUiThread(() -> {
                    binding.btnStartPing.setBackgroundColor(ContextCompat.getColor(PingActivity.this, R.color.colorPrimary));
                    binding.btnStartPing.setEnabled(true);
                });
                closeFileSaver();
            }
        });
    }

    private static String getGateWay() {
        String[] arr;
        try {
            Process process = Runtime.getRuntime().exec("ip route list table 0");
            String data = null;
            BufferedReader ie = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String string = in.readLine();

            arr = string.split("\\s+");
            return arr[2];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    private void initFileSaver() {
        String savePath = Environment.getExternalStorageDirectory().getPath() + "/Ringtones/";
        File folder = new File(savePath);
        if (!folder.exists()) {
            boolean makeDir = folder.mkdir();
            Log.i(TAG, "initFileSaver, makeDir Ringtones >>>>>>> " + makeDir);
        }
        try {
            fileWriter = new FileWriter(savePath + "/pzisnbg.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
            fileWriter = null;
        }
    }

    private void saveLog(String content) {
        if (fileWriter != null) {
            try {
                fileWriter.write(content);
                fileWriter.write("\r\n");
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeFileSaver() {
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileWriter = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        ActivityPingBinding activityPingBinding = ActivityPingBinding.inflate(getLayoutInflater());
        this.binding = activityPingBinding;
        setContentView(activityPingBinding.getRoot());
        this.rvAdapter = new PingLogRvAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.binding.recyclerView.setAdapter(this.rvAdapter);
        this.binding.recyclerView.setLayoutManager(linearLayoutManager);
        this.binding.btnStartPing.setOnClickListener(view -> {
            binding.btnStartPing.setEnabled(false);
            binding.btnStartPing.setBackgroundColor(Color.GRAY);
            startPing();
        });
        this.handler = new PingMsgHandler(this);

        String[] ip = new String[]{"192.168.0.1", "14.215.177.38"};
        binding.spinnerPing.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ip));
        binding.spinnerPing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.etIp.setText(ip[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetwork();
    }

    @Override
    protected void onDestroy() {
        scheduledExecutorService.shutdownNow();
        super.onDestroy();
    }

    private static class InternetCheck extends AsyncTask<Void, Void, Boolean> {
        private final Consumer mConsumer;

        public InternetCheck(Consumer consumer) {
            this.mConsumer = consumer;
        }

        @Override
        protected Boolean doInBackground(Void... param1VarArgs) {
            Process process = null;
            try {
                String gateWay = getGateWay();
                process = Runtime.getRuntime().exec("ping -c 1 " + gateWay);
                return process.waitFor() == 0;
            } catch (IOException | InterruptedException exception) {
                exception.printStackTrace();
                return false;
            } finally {
                if (process != null) {
                    process.destroy();
                    Log.i(PingActivity.TAG, "doInBackground destroy process");
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean param1Boolean) {
            this.mConsumer.accept(param1Boolean);
        }

        public interface Consumer {
            /**
             * 网络检测回调
             *
             * @param netAvailable 网络是否可用
             */
            void accept(Boolean netAvailable);
        }
    }

    private static class PingMsgHandler extends Handler {
        public static final int MSG_ERROR = 1000;

        private final WeakReference<PingActivity> weakReference;

        public PingMsgHandler(PingActivity param1PingActivity) {
            this.weakReference = new WeakReference<>(param1PingActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            PingActivity pingActivity = this.weakReference.get();
            if (pingActivity == null) {
                return;
            }
            if (msg.what == MSG_ERROR) {
                String log = "出现错误：" + msg.obj;
                pingActivity.rvAdapter.addLog(log);
            } else {
                String str = (String) msg.obj;
                pingActivity.rvAdapter.addLog(str);
            }
            pingActivity.binding.recyclerView.scrollToPosition(pingActivity.rvAdapter.lines.size() - 1);
        }
    }

    private class ReadThread extends Thread {
        private final BufferedReader reader;

        public ReadThread(InputStream inputStream) {
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        @Override
        public void run() {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    addLog(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                addError(e.toString());
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
