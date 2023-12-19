package com.handheld.huang.handsettest.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivitySensorTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

public class SensorTestActivity extends AppCompatActivity implements View.OnClickListener {
    private SensorManager sm;
    float[] accelerometerValues = new float[3];

    private SpUtils mSpUtils;

    private static final String TAG = "SensorTestActivity";
    private com.handheld.huang.handsettest.databinding.ActivitySensorTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySensorTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.layoutResultConfirm.resultTvQuestion.setText(R.string.gsensor_test_confirm);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mSpUtils = new SpUtils(this);

        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);
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
                binding.tvX.setText(String.valueOf(accelerometerValues[0]));
                binding.tvY.setText(String.valueOf(accelerometerValues[1]));
                binding.tvZ.setText(String.valueOf(accelerometerValues[2]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onClick(@NonNull View view) {
        if (view == binding.layoutResultConfirm.resultImgOk) {
            binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_selected);
            binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_unselected);
            mSpUtils.saveGsensorCheckResult(0);
        } else if (view == binding.layoutResultConfirm.resultImgCross) {
            binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_selected);
            binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_unselected);
            mSpUtils.saveGsensorCheckResult(1);
        } else if (view == binding.layoutResultConfirm.resultTvNext) {
            // 进入通话测试
            Intent intent = new Intent(this, BasicTestActivity.class);
            intent.putExtra("testFlag", 1);
            startActivity(intent);
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }
    }
}
