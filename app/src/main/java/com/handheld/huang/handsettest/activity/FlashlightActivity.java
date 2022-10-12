package com.handheld.huang.handsettest.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 闪光灯测试
 *
 * @author LeiHuang
 */
public class FlashlightActivity extends Activity {

    private Camera camera;

    @BindView(R.id.onElectricity_tv_question)
    TextView mOnElectricityTvQuestion;
    @BindView(R.id.onElectricity_img_ok)
    ImageView mOnElectricityImgOk;
    @BindView(R.id.onElectricity_img_cross)
    ImageView mOnElectricityImgCross;
    @BindView(R.id.result_tv_next)
    TextView mResultTvNext;

    private int onCheckResult;
    private SpUtils mSpUtils;

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
        setContentView(R.layout.activity_flashlight);
        ButterKnife.bind(this);
        mSpUtils = new SpUtils(this);
        mResultTvNext.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flashLightOn();
        }
    }

    @OnClick({R.id.onElectricity_img_ok, R.id.onElectricity_img_cross, R.id.result_tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.onElectricity_img_ok:
                mOnElectricityImgOk.setImageResource(R.drawable.check_ok_selected);
                mOnElectricityImgCross.setImageResource(R.drawable.check_cross_unselected);
                onCheckResult = 0;
                mResultTvNext.setClickable(true);
                break;
            case R.id.onElectricity_img_cross:
                mOnElectricityImgCross.setImageResource(R.drawable.check_cross_selected);
                mOnElectricityImgOk.setImageResource(R.drawable.check_ok_unselected);
                onCheckResult = 1;
                mResultTvNext.setClickable(true);
                break;
            case R.id.result_tv_next:
                mSpUtils.saveFlashlightCheckResult(onCheckResult);
                startActivity(new Intent(FlashlightActivity.this, Serial13TestActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flashLightOff();
        }
    }
}
