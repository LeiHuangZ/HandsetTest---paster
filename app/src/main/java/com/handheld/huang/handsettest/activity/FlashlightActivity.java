package com.handheld.huang.handsettest.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

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
    private CameraManager cameraManager;

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.e("FlashlightActivity", "onOpened");
            camera.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flashLightOn();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.e("FlashlightActivity", "onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    /**
     * 获取可用的相机ID
     */
    private String getAvailableId() {
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            if (cameraIdList.length == 0) {
                Toast.makeText(this, getString(R.string.camera_not_found), Toast.LENGTH_SHORT).show();
                return "-1";
            }
            String flashCameraId = "-1";
            for (String cameraId : cameraIdList) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Boolean flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (flashAvailable != null && flashAvailable) {
                    flashCameraId = cameraId;
                    break;
                }
            }
            if (flashCameraId.equals("-1")) {
                Toast.makeText(this, getString(R.string.camera_not_found), Toast.LENGTH_SHORT).show();
                return "-1";
            }
            return flashCameraId;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    /**
     * 打开闪光灯
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOn() {
        try {
            String avaId = getAvailableId();
            if (avaId.equals("-1")) {
                return;
            }
            cameraManager.setTorchMode(avaId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭闪光灯
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOff() {
        try {
            String avaId = getAvailableId();
            if (avaId.equals("-1")) {
                return;
            }
            cameraManager.setTorchMode(avaId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        //获取摄像头的管理者CameraManager
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        //检查权限
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //打开相机，第一个参数指示打开哪个摄像头，第二个参数stateCallback为相机的状态回调接口，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            String availableId = getAvailableId();
            if (availableId.equals("-1")) {
                return;
            }
            manager.openCamera(availableId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlashlightBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        // F1F2先打开相机，以让扫码头关闭，不然4710打开时，无法使用手电筒
        if (Build.VERSION.SDK_INT >= 34) {
            openCamera();
        }
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
