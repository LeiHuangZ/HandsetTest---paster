package com.handheld.huang.handsettest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

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
    private int checkResult;
    private SpUtils mSpUtils;
    private int mScreenHeight;

    private SerialPort mSerialPort;

    private final static String ACTION ="android.hardware.usb.action.USB_STATE";
    private BroadcastReceiver usBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(ACTION)) {
                boolean connected = intent.getExtras().getBoolean("connected");
                if (connected) {
                    onViewClicked(mIndicatorBtnRed);
                } else {
                    onViewClicked(mIndicatorBtnRed);
                }
                }
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

            mSerialPort = new SerialPort();
            mResultTvNext.setClickable(false);
            mResultTvQuestion.setText(getResources().getString(R.string.indicator_test_confirm));

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION);
            registerReceiver(usBroadcastReceiver, intentFilter);
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
            switch (view.getId()) {
                case R.id.indicator_btn_blue:
                    if (!isBlueOn) {
                        if (mScreenHeight == screen901) {
                            //901蓝指示灯亮
                            mSerialPort.setGPIOhigh(15);
                            mSerialPort.setGPIOhigh(64);
                        } else if (mScreenHeight == screenC5000) {
                            //C5000蓝指示灯亮
                            mSerialPort.setGPIOhigh(64);
                        } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                            //701蓝指示灯亮
                            mSerialPort.setGPIOhigh(46);
                        }
                        mIndicatorBtnBlue.setText(getResources().getString(R.string.blue_off));
                        mIndicatorBtnBlue.setIconResource("\uf05e");
                        isBlueOn = true;
                    } else {
                        if (mScreenHeight == screen901) {
                            //901蓝指示灯亮
                            mSerialPort.setGPIOlow(15);
                            mSerialPort.setGPIOlow(64);

                        } else if (mScreenHeight == screenC5000) {
                            //C5000蓝指示灯亮
                            mSerialPort.setGPIOlow(64);
                        } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                            //701蓝指示灯亮
                            mSerialPort.setGPIOlow(46);
                        }
                        isBlueOn = false;
                        mIndicatorBtnBlue.setText(getResources().getString(R.string.blue_on));
                        mIndicatorBtnBlue.setIconResource("\uf0eb");
                        Log.i(TAG, "onViewClicked, blueoff");
                    }
                    break;
                case R.id.indicator_btn_red:
                    if (!isRedOn) {
                        if (mScreenHeight == screen901) {
                            //901红指示灯亮
                            mSerialPort.setGPIOhigh(18);
                            mSerialPort.setGPIOhigh(9);

                            mIndicatorBtnRed.setText(getResources().getString(R.string.red_off));
                            mIndicatorBtnRed.setIconResource("\uf05e");
                        } else if (mScreenHeight == screenC5000) {
                            //C5000红指示灯亮
                            mSerialPort.setGPIOhigh(9);
                            mIndicatorBtnRed.setText(getResources().getString(R.string.red_off));
                            mIndicatorBtnRed.setIconResource("\uf05e");
                        } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                            //701红指示灯不可控
                            mIndicatorBtnRed.setText(getResources().getString(R.string.red_no));
                            mIndicatorBtnRed.setIconResource("\uf05e");
                        }
                        isRedOn = true;
                    } else {
                        if (mScreenHeight == screen901) {
                            //901红指示灯亮
                            mSerialPort.setGPIOlow(18);
                            mSerialPort.setGPIOlow(9);

                            mIndicatorBtnRed.setText(getResources().getString(R.string.red_on));
                            mIndicatorBtnRed.setIconResource("\uf0eb");
                        } else if (mScreenHeight == screenC5000) {
                            //C5000红指示灯亮
                            mSerialPort.setGPIOlow(9);
                            mIndicatorBtnRed.setText(getResources().getString(R.string.red_on));
                            mIndicatorBtnRed.setIconResource("\uf0eb");
                        } else if (mScreenHeight == screen70101 || mScreenHeight == screen70102 || mScreenHeight == screen70103 || mScreenHeight == screen70104 || mScreenHeight == screen70105) {
                            //701红指示灯不可控
                            mIndicatorBtnRed.setText(getResources().getString(R.string.red_no));
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
                    startActivity(new Intent(IndicatorTestActivity.this, ElectricityTestActivity.class));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usBroadcastReceiver);
    }
}
