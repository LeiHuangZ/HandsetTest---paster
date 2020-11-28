package com.handheld.huang.handsettest.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SensorTestActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_x)
    TextView mTvX;
    @BindView(R.id.tv_y)
    TextView mTvY;
    @BindView(R.id.tv_z)
    TextView mTvZ;
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
    private SensorManager sm;
    float[] accelerometerValues = new float[3];

    private SpUtils mSpUtils;

    private static final String TAG = "SensorTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);
        ButterKnife.bind(this);

        mResultTvQuestion.setText(R.string.gsensor_test_confirm);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mSpUtils = new SpUtils(this);
    }

    @Override
    protected void onDestroy() {
        sm.unregisterListener(myListener);
        super.onDestroy();
    }

    final SensorEventListener myListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values;
                mTvX.setText(String.valueOf(accelerometerValues[0]));
                mTvY.setText(String.valueOf(accelerometerValues[1]));
                mTvZ.setText(String.valueOf(accelerometerValues[2]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @OnClick({R.id.result_img_ok, R.id.result_img_cross, R.id.result_tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.result_img_ok:
                mResultImgOk.setImageResource(R.drawable.check_ok_selected);
                mResultImgCross.setImageResource(R.drawable.check_cross_unselected);
                mSpUtils.saveGsensorCheckResult(0);
                break;
            case R.id.result_img_cross:
                mResultImgCross.setImageResource(R.drawable.check_cross_selected);
                mResultImgOk.setImageResource(R.drawable.check_ok_unselected);
                mSpUtils.saveGsensorCheckResult(1);
                break;
            case R.id.result_tv_next:
//                Intent intent = new Intent(this, LedTestActivity.class);
                // 进入通话测试
                Intent intent = new Intent(this, BasicTestActivity.class);
                intent.putExtra("testFlag", 1);
                startActivity(intent);
                overridePendingTransition(R.animator.activity_start_rigth,0);
                finish();
                break;
        }
    }
}
