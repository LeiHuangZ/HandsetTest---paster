package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityIndicatorTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.UsbTils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.pda.serialport.SerialPort;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author huang
 */
public class IndicatorTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = IndicatorTestActivity.class.getSimpleName();

    private int checkResult;
    private SpUtils mSpUtils;
    private int mScreenHeight;

    private SerialPort mSerialPort;
    private String mRelease;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "onReceive, action:" + action);
        }
    };

    private String blueOnBtnText;
    private String blueOffBtnText;
    private com.handheld.huang.handsettest.databinding.ActivityIndicatorTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIndicatorTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        mSpUtils = new SpUtils(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getRealSize(outSize);
        mScreenHeight = outSize.y;
        mRelease = Build.VERSION.RELEASE;

        mSerialPort = new SerialPort();
        binding.layoutResultConfirm.resultTvNext.setClickable(false);
        binding.layoutResultConfirm.resultTvQuestion.setText(getResources().getString(R.string.indicator_test_confirm));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, intentFilter);

        blueOnBtnText = getString(R.string.blue_on);
        blueOffBtnText = getString(R.string.blue_off);

        binding.indicatorBtnBlue.setOnClickListener(this);
        binding.indicatorBtnRed.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        updateButtonStat();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    boolean isBlueOn = false;
    boolean isRedOn = false;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    private void updateButtonStat() {
        //  BX6000进入页面时判断红蓝灯状态，57为蓝灯（控制为57），78为红灯（控制为160）
        if (Build.VERSION.SDK_INT == 28) {
            isBlueOn = UsbTils.getGpioState(57);
            isRedOn = UsbTils.getGpioState(78);
            if (!isRedOn) {
                binding.indicatorBtnRed.setText(getResources().getString(R.string.red_on));
                binding.indicatorBtnRed.setIconResource("\uf0eb");
            } else {
                binding.indicatorBtnRed.setText(getResources().getString(R.string.red_off));
                binding.indicatorBtnRed.setIconResource("\uf05e");
            }
            if (!isBlueOn) {
                binding.indicatorBtnBlue.setText(blueOnBtnText);
                binding.indicatorBtnBlue.setIconResource("\uf0eb");
            } else {
                binding.indicatorBtnBlue.setText(blueOffBtnText);
                binding.indicatorBtnBlue.setIconResource("\uf05e");
            }
        }
        initBtnColorState();
    }

    private void initBtnColorState() {
        int state;
        if ("uis7885_2h10".equals(Build.HARDWARE)) {
            blueOnBtnText = getResources().getString(R.string.green_on);
            blueOffBtnText = getResources().getString(R.string.green_off);
            state = getLedUis7885State(0);
        } else if ("qcom".equals(Build.HARDWARE)) {
            state = getLedSM4350State(0);
        } else {
            return;
        }
        isBlueOn = state > 0;
        if (state > 0) {
            binding.indicatorBtnBlue.setText(blueOffBtnText);
            binding.indicatorBtnBlue.setIconResource("\uf05e");
        } else {
            binding.indicatorBtnBlue.setText(blueOnBtnText);
            binding.indicatorBtnBlue.setIconResource("\uf0eb");
        }
        if ("uis7885_2h10".equals(Build.HARDWARE)) {
            state = getLedUis7885State(1);
        } else {
            state = getLedSM4350State(1);
        }
        isRedOn = state > 0;
        if (state > 0) {
            binding.indicatorBtnRed.setText(getResources().getString(R.string.red_off));
            binding.indicatorBtnRed.setIconResource("\uf05e");
        } else if (state == 0) {
            binding.indicatorBtnRed.setText(getResources().getString(R.string.red_on));
            binding.indicatorBtnRed.setIconResource("\uf0eb");
        }
    }

    private void blueLedOnP22() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led1/brightness"));
            bufferedWriter.write("1");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    private void blueLedOffP22() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led1/brightness"));
            bufferedWriter.write("0");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    private void redLedOnP22() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led2/brightness"));
            bufferedWriter.write("1");
            bufferedWriter.flush();
            bufferedWriter.close();
            // NB801S第一批试产主板，为led4控制红灯
            bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led4/brightness"));
            bufferedWriter.write("1");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    private void redLedOffP22() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led2/brightness"));
            bufferedWriter.write("0");
            bufferedWriter.flush();
            bufferedWriter.close();
            // NB801S第一批试产主板，为led4控制红灯
            bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led4/brightness"));
            bufferedWriter.write("0");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    private void setLedUis7885State(int ledType, boolean enable) {
        try {
            BufferedWriter bufferedWriter;
            if (ledType == 0) {
                // 绿灯
                bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/sc27xx:green/brightness"));
            } else {
                // 红灯
                bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/sc27xx:red/brightness"));
            }
            if (enable) {
                bufferedWriter.write("255");
            } else {
                bufferedWriter.write("0");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    private int getLedUis7885State(int ledType) {
        try {
            BufferedReader bufferedReader;
            if (ledType == 0) {
                // 绿灯
                bufferedReader = new BufferedReader(new FileReader("/sys/class/leds/sc27xx:green/brightness"));
            } else {
                // 红灯
                bufferedReader = new BufferedReader(new FileReader("/sys/class/leds/sc27xx:red/brightness"));
            }
            String state = bufferedReader.readLine();
            bufferedReader.close();
            return Integer.parseInt(state);
        } catch (IOException e) {
            return -1;
        }
    }

    private void setLedSM4350State(int ledType, boolean enable) {
        try {
            BufferedWriter bufferedWriter;
            if (ledType == 0) {
                // 蓝灯
                bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/blue/brightness"));
            } else {
                // 红灯
                bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/red/brightness"));
            }
            if (enable) {
                bufferedWriter.write("255");
            } else {
                bufferedWriter.write("0");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    private int getLedSM4350State(int ledType) {
        try {
            BufferedReader bufferedReader;
            if (ledType == 0) {
                // 蓝灯
                bufferedReader = new BufferedReader(new FileReader("/sys/class/leds/blue/brightness"));
            } else {
                // 红灯
                bufferedReader = new BufferedReader(new FileReader("/sys/class/leds/red/brightness"));
            }
            String state = bufferedReader.readLine();
            bufferedReader.close();
            return Integer.parseInt(state);
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onClick(@NonNull View view) {
        int screen901 = 800;
        int screenC5000 = 1280;
        int screen70101 = 720;
        int screen70102 = 600;
        int screen70103 = 1024;
        int screen70104 = 552;
        int screen70105 = 976;
        int screen711 = 1208;
        String version = "5.1";
        String version1 = "7.0";
        String versionQ = "9.0";
        int sdkInt = Build.VERSION.SDK_INT;
        Log.e(TAG, "onViewClicked: sdkInt=" + sdkInt);
        if (view == binding.indicatorBtnBlue) {
            if (!isBlueOn) {
                // 亮蓝灯
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P || Build.VERSION.SDK_INT >= 29) {
                    if (Build.HARDWARE.equals("qcom")) {
                        // NB801-5G
                        setLedSM4350State(0, true);
                    } else {
                        // BX6000,BX6100,BX6200,Android 9.0
                        mSerialPort.setGPIOhigh(57);
                        blueLedOnP22();
                        setLedUis7885State(0, true);
                    }
                } else if (mScreenHeight == screen901) {
                    if (mRelease.equals(version) || mRelease.equals(version1)) {
                        //H942
                        mSerialPort.setGPIOhigh(64);
                    } else {
                        //H941
                        mSerialPort.setGPIOhigh(15);
                    }
                } else if (mScreenHeight == screenC5000) {
                    //C5000
                    mSerialPort.setGPIOhigh(64);
                } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                    //701
                    mSerialPort.setGPIOhigh(64);
                } else if (mScreenHeight == screen711) {
                    //H711
                    mSerialPort.setGPIOhigh(64);
                }
                binding.indicatorBtnBlue.setText(blueOffBtnText);
                binding.indicatorBtnBlue.setIconResource("\uf05e");
                isBlueOn = true;
            } else {
                //蓝灯灭
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P || Build.VERSION.SDK_INT >= 29) {
                    if (Build.HARDWARE.equals("qcom")) {
                        // NB801-5G
                        setLedSM4350State(0, false);
                    } else {
                        // BX6000,BX6100,BX6200,Android 9.0
                        mSerialPort.setGPIOlow(57);
                        blueLedOffP22();
                        setLedUis7885State(0, false);
                    }
                } else if (mScreenHeight == screen901) {
                    if (mRelease.equals(version) || mRelease.equals(version1)) {
                        //H942
                        mSerialPort.setGPIOlow(64);
                    } else {
                        //H941
                        mSerialPort.setGPIOlow(15);
                    }
                } else if (mScreenHeight == screenC5000) {
                    //C5000
                    mSerialPort.setGPIOlow(64);
                } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                    //701
                    mSerialPort.setGPIOlow(64);
                } else if (mScreenHeight == screen711) {
                    //H711
                    mSerialPort.setGPIOlow(64);
                }
                isBlueOn = false;
                binding.indicatorBtnBlue.setText(blueOnBtnText);
                binding.indicatorBtnBlue.setIconResource("\uf0eb");
                Log.i(TAG, "onViewClicked, blueoff");
            }
        } else if (view == binding.indicatorBtnRed) {
            if (!isRedOn) {
                //红灯亮
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P || Build.VERSION.SDK_INT >= 29) {
                    if (Build.HARDWARE.equals("qcom")) {
                        // NB801-5G
                        setLedSM4350State(1, true);
                    } else {
                        // BX6000,BX6100,BX6200,Android 9.0
                        mSerialPort.setGPIOhigh(160);
                        redLedOnP22();
                        setLedUis7885State(1, true);
                    }
                } else if (mScreenHeight == screen901) {
                    if (mRelease.equals(version) || mRelease.equals(version1)) {
                        //H942
                        mSerialPort.setGPIOhigh(9);
                    } else {
                        //H941
                        mSerialPort.setGPIOhigh(18);
                    }
                } else if (mScreenHeight == screenC5000) {
                    //C5000
                    mSerialPort.setGPIOhigh(9);
                } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                    //701红指示灯不可控
//                        binding.indicatorBtnRed.setText(getResources().getString(R.string.red_no));
                    binding.toolbar.setTitle(getResources().getString(R.string.red_no));
                    binding.indicatorBtnRed.setIconResource("\uf05e");
                } else if (mScreenHeight == screen711) {
                    //H711红指示灯不可控
//                        binding.indicatorBtnRed.setText(getResources().getString(R.string.red_no));
                    binding.toolbar.setTitle(getResources().getString(R.string.red_no));
                    binding.indicatorBtnRed.setIconResource("\uf05e");
                }
                isRedOn = true;
                binding.indicatorBtnRed.setText(getResources().getString(R.string.red_off));
                binding.indicatorBtnRed.setIconResource("\uf05e");
            } else {
                //红灯灭
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P || Build.VERSION.SDK_INT >= 29) {
                    if (Build.HARDWARE.equals("qcom")) {
                        // NB801-5G
                        setLedSM4350State(1, false);
                    } else {
                        // BX6000,BX6100,BX6200,Android 9.0
                        mSerialPort.setGPIOlow(160);
                        redLedOffP22();
                        setLedUis7885State(1, false);
                    }
                } else if (mScreenHeight == screen901) {
                    if (mRelease.equals(version) || mRelease.equals(version1)) {
                        //H942
                        mSerialPort.setGPIOlow(9);
                    } else {
                        //H941
                        mSerialPort.setGPIOlow(18);
                    }
                } else if (mScreenHeight == screenC5000) {
                    //C5000
                    mSerialPort.setGPIOlow(9);
                } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                    //701红指示灯不可控
//                        binding.indicatorBtnRed.setText(getResources().getString(R.string.red_no));
                    binding.toolbar.setTitle(getResources().getString(R.string.red_no));
                    binding.indicatorBtnRed.setIconResource("\uf05e");
                } else if (mScreenHeight == screen711) {
                    //H711红指示灯不可控
//                        binding.indicatorBtnRed.setText(getResources().getString(R.string.red_no));
                    binding.toolbar.setTitle(getResources().getString(R.string.red_no));
                    binding.indicatorBtnRed.setIconResource("\uf05e");
                }
                isRedOn = false;
                binding.indicatorBtnRed.setText(getResources().getString(R.string.red_on));
                binding.indicatorBtnRed.setIconResource("\uf0eb");
            }
        } else if (view == binding.layoutResultConfirm.resultImgOk) {
            binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_selected);
            binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_unselected);
            checkResult = 0;
            binding.layoutResultConfirm.resultTvNext.setClickable(true);
        } else if (view == binding.layoutResultConfirm.resultImgCross) {
            binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_selected);
            binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_unselected);
            checkResult = 1;
            binding.layoutResultConfirm.resultTvNext.setClickable(true);
        } else if (view == binding.layoutResultConfirm.resultTvNext) {
            mSpUtils.saveIndicatorCheckResult(checkResult);
            Log.i(TAG, "IndicatorCheckResult: " + mSpUtils.getIndocatorCheckResult());
            startActivity(new Intent(IndicatorTestActivity.this, UsbActivity.class));
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }

    }
}
