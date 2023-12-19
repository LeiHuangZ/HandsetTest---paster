package com.handheld.huang.handsettest.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityFlashlightBinding;
import com.handheld.huang.handsettest.utils.SpUtils;


/**
 * 闪光灯测试
 *
 * @author LeiHuang
 */
public class FlashlightActivity extends Activity implements View.OnClickListener {

    private int onCheckResult;
    private SpUtils mSpUtils;
    private com.handheld.huang.handsettest.databinding.ActivityFlashlightBinding binding;

    /**
     * 打开闪光灯
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            if (cameraIdList.length == 0) {
                Toast.makeText(this, getString(R.string.camera_not_found), Toast.LENGTH_SHORT).show();
                return;
            }
            String cameraId = cameraIdList[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭闪光灯
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            if (cameraIdList.length <= 0) {
                return;
            }
            String cameraId = cameraIdList[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlashlightBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mSpUtils = new SpUtils(this);
        binding.resultTvNext.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flashLightOn();
        }
        binding.onElectricityImgOk.setOnClickListener(this);
        binding.onElectricityImgCross.setOnClickListener(this);
        binding.resultTvNext.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flashLightOff();
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view == binding.onElectricityImgOk) {
            binding.onElectricityImgOk.setImageResource(R.drawable.check_ok_selected);
            binding.onElectricityImgCross.setImageResource(R.drawable.check_cross_unselected);
            onCheckResult = 0;
            binding.resultTvNext.setClickable(true);
        } else if (view == binding.onElectricityImgCross) {
            binding.onElectricityImgCross.setImageResource(R.drawable.check_cross_selected);
            binding.onElectricityImgOk.setImageResource(R.drawable.check_ok_unselected);
            onCheckResult = 1;
            binding.resultTvNext.setClickable(true);
        } else if (view == binding.resultTvNext) {
            mSpUtils.saveFlashlightCheckResult(onCheckResult);
            startActivity(new Intent(FlashlightActivity.this, TestConclusionActivity.class));
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }
    }
}
