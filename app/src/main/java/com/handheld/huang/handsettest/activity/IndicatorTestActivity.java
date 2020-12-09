package com.handheld.huang.handsettest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.UsbTils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pda.serialport.SerialPort;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author huang
 */
public class IndicatorTestActivity extends AppCompatActivity {
    private static String TAG = IndicatorTestActivity.class.getSimpleName();

    @BindView(R.id.indicator_btn_blue)
    FancyButton mIndicatorBtnBlue;
    @BindView(R.id.indicator_btn_red)
    FancyButton mIndicatorBtnRed;
    @BindView(R.id.result_tv_question)
    TextView mResultTvQuestion;
    @BindView(R.id.result_img_ok)
    ImageView mResultImgOk;
    @BindView(R.id.result_img_cross)
    ImageView mResultImgCross;
    @BindView(R.id.result_tv_next)
    TextView mResultTvNext;
    @BindView(R.id.result_ll_confirm)
    LinearLayout mResultLlConfirm;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicator_test);
        ButterKnife.bind(this);

        mSpUtils = new SpUtils(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        mScreenHeight = outSize.y;
        mRelease = Build.VERSION.RELEASE;

        mSerialPort = new SerialPort();
        mResultTvNext.setClickable(false);
        mResultTvQuestion.setText(getResources().getString(R.string.indicator_test_confirm));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, intentFilter);
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

    @OnClick({R.id.indicator_btn_blue, R.id.indicator_btn_red, R.id.result_img_ok, R.id.result_img_cross, R.id.result_tv_next})
    public void onViewClicked(View view) {
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
        switch (view.getId()) {
            case R.id.indicator_btn_blue:
                if (!isBlueOn) {
                    // 亮蓝灯
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P || Build.VERSION.SDK_INT == 29){
                        // BX6000,BX6100,BX6200,Android 9.0
                        mSerialPort.setGPIOhigh(57);
                        blueLedOnP22();
                    } else if (mScreenHeight == screen901) {
                        if (mRelease.equals(version) || mRelease.equals(version1)) {
                            //H942
                            mSerialPort.setGPIOhigh(64);
                        }else {
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
                    mIndicatorBtnBlue.setText(getResources().getString(R.string.blue_off));
                    mIndicatorBtnBlue.setIconResource("\uf05e");
                    isBlueOn = true;
                } else {
                    //蓝灯灭
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P || Build.VERSION.SDK_INT == 29){
                        // BX6000,BX6100,BX6200,Android 9.0
                        mSerialPort.setGPIOlow(57);
                        blueLedOffP22();
                    } else if (mScreenHeight == screen901) {
                        if (mRelease.equals(version) || mRelease.equals(version1)) {
                            //H942
                            mSerialPort.setGPIOlow(64);
                        }else {
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
                    mIndicatorBtnBlue.setText(getResources().getString(R.string.blue_on));
                    mIndicatorBtnBlue.setIconResource("\uf0eb");
                    Log.i(TAG, "onViewClicked, blueoff");
                }
                break;
            case R.id.indicator_btn_red:
                if (!isRedOn) {
                    //红灯亮
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P || Build.VERSION.SDK_INT == 29){
                        // BX6000,BX6100,BX6200,Android 9.0
                        mSerialPort.setGPIOhigh(160);
                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_off));
                        mIndicatorBtnRed.setIconResource("\uf05e");
                        redLedOnP22();
                    } else if (mScreenHeight == screen901) {
                        if (mRelease.equals(version) || mRelease.equals(version1)){
                            //H942
                            mSerialPort.setGPIOhigh(9);
                        }else{
                            //H941
                            mSerialPort.setGPIOhigh(18);
                        }
                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_off));
                        mIndicatorBtnRed.setIconResource("\uf05e");
                    } else if (mScreenHeight == screenC5000) {
                        //C5000
                        mSerialPort.setGPIOhigh(9);
                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_off));
                        mIndicatorBtnRed.setIconResource("\uf05e");
                    } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                        //701红指示灯不可控
//                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_no));
                        mToolbar.setTitle(getResources().getString(R.string.red_no));
                        mIndicatorBtnRed.setIconResource("\uf05e");
                    } else if (mScreenHeight == screen711) {
                        //H711红指示灯不可控
//                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_no));
                        mToolbar.setTitle(getResources().getString(R.string.red_no));
                        mIndicatorBtnRed.setIconResource("\uf05e");
                    }
                    isRedOn = true;
                } else {
                    //红灯灭
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P || Build.VERSION.SDK_INT == 29){
                        // BX6000,BX6100,BX6200,Android 9.0
                        mSerialPort.setGPIOlow(160);
                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_on));
                        mIndicatorBtnRed.setIconResource("\uf0eb");
                        redLedOffP22();
                    } else if (mScreenHeight == screen901) {
                        if (mRelease.equals(version) || mRelease.equals(version1)){
                            //H942
                            mSerialPort.setGPIOlow(9);
                        }else{
                            //H941
                            mSerialPort.setGPIOlow(18);
                        }
                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_on));
                        mIndicatorBtnRed.setIconResource("\uf0eb");
                    } else if (mScreenHeight == screenC5000) {
                        //C5000
                        mSerialPort.setGPIOlow(9);
                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_on));
                        mIndicatorBtnRed.setIconResource("\uf0eb");
                    } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                        //701红指示灯不可控
//                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_no));
                        mToolbar.setTitle(getResources().getString(R.string.red_no));
                        mIndicatorBtnRed.setIconResource("\uf05e");
                    } else if (mScreenHeight == screen711) {
                        //H711红指示灯不可控
//                        mIndicatorBtnRed.setText(getResources().getString(R.string.red_no));
                        mToolbar.setTitle(getResources().getString(R.string.red_no));
                        mIndicatorBtnRed.setIconResource("\uf05e");
                    }
                    isRedOn = false;
                }
                break;
            case R.id.result_img_ok:
                mResultImgOk.setImageResource(R.drawable.check_ok_selected);
                mResultImgCross.setImageResource(R.drawable.check_cross_unselected);
                checkResult = 0;
                mResultTvNext.setClickable(true);
                break;
            case R.id.result_img_cross:
                mResultImgCross.setImageResource(R.drawable.check_cross_selected);
                mResultImgOk.setImageResource(R.drawable.check_ok_unselected);
                checkResult = 1;
                mResultTvNext.setClickable(true);
                break;
            case R.id.result_tv_next:
                mSpUtils.saveIndicatorCheckResult(checkResult);
                Log.i(TAG, "IndicatorCheckResult: " + mSpUtils.getIndocatorCheckResult());
                startActivity(new Intent(IndicatorTestActivity.this, UsbActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void updateButtonStat(){
        //  BX6000进入页面时判断红蓝灯状态，57为蓝灯（控制为57），78为红灯（控制为160）
        if (Build.VERSION.SDK_INT == 28) {
            isBlueOn = UsbTils.getGpioState(57);
            isRedOn = UsbTils.getGpioState(78);
            if (!isRedOn) {
                mIndicatorBtnRed.setText(getResources().getString(R.string.red_on));
                mIndicatorBtnRed.setIconResource("\uf0eb");
            } else {
                mIndicatorBtnRed.setText(getResources().getString(R.string.red_off));
                mIndicatorBtnRed.setIconResource("\uf05e");
            }
            if (!isBlueOn){
                mIndicatorBtnBlue.setText(getResources().getString(R.string.blue_on));
                mIndicatorBtnBlue.setIconResource("\uf0eb");
            } else {
                mIndicatorBtnBlue.setText(getResources().getString(R.string.blue_off));
                mIndicatorBtnBlue.setIconResource("\uf05e");
            }
        }
    }

    private void blueLedOnP22() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led1/brightness"));
            bufferedWriter.write("1");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void blueLedOffP22() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led1/brightness"));
            bufferedWriter.write("0");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void redLedOnP22() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led2/brightness"));
            bufferedWriter.write("1");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void redLedOffP22() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/sys/class/leds/mt6370_pmu_led2/brightness"));
            bufferedWriter.write("0");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
