package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityImeiTestBinding;
import com.handheld.huang.handsettest.utils.CodeUtil;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.Util;

/**
 * @author huang
 */
public class ImeiTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = ImeiTestActivity.class.getSimpleName();

    int checkResult;
    private Util mUtil;
    private Toast mToast;
    private SpUtils mSpUtils;
    private com.handheld.huang.handsettest.databinding.ActivityImeiTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImeiTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        mUtil = new Util(this);
        mUtil.initAudio();
        mSpUtils = new SpUtils(this);

        binding.imeiBtnTest.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtil.closeAudio();
    }

    String originImei = "860527010207000";

    /**
     * 获取IMEI号
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getImei() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.getImei();
            } else {
                return tm.getDeviceId();
            }
        }
        return originImei;
    }

    /**
     * 展示Toast
     *
     * @param data 需要展示的数据
     */
    private void showToast(String data) {
        if (mToast == null) {
            mToast = Toast.makeText(ImeiTestActivity.this, data, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        } else {
            mToast.setText(data);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.imeiBtnTest)) {
            String imei = getImei();
            Log.i(TAG, "imei : " + imei);
            if (imei == null || originImei.equals(imei)) {
                showToast("原始IMEI地址，IMEI测试不通过！");
                mUtil.playAudio(2);
                binding.imeiBtnTest.setText("原始IMEI地址，IMEI测试不通过！请点击下一步");
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                binding.layoutResultConfirm.resultImgCross.callOnClick();
                binding.layoutResultConfirm.resultImgOk.setClickable(false);
                binding.layoutResultConfirm.resultImgCross.setClickable(false);
            } else {
                Bitmap qrImage;
                qrImage = CodeUtil.creatBarcode(ImeiTestActivity.this, imei, 800, 400, new PointF(0, 250), true);
                showToast("检测通过！");
                binding.imeiLlEnter.setVisibility(View.GONE);
                binding.imeiImgShow.setVisibility(View.VISIBLE);
                binding.imeiImgShow.setImageBitmap(qrImage);
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                binding.layoutResultConfirm.resultTvQuestion.setText(R.string.imei_test);
                binding.layoutResultConfirm.resultImgOk.callOnClick();
                binding.layoutResultConfirm.resultImgOk.setClickable(false);
                binding.layoutResultConfirm.resultImgCross.setClickable(false);
            }
        } else if (view.equals(binding.layoutResultConfirm.resultImgOk)) {
            binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_selected);
            binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_unselected);
            checkResult = 0;
            binding.layoutResultConfirm.resultTvNext.setClickable(true);
        } else if (view.equals(binding.layoutResultConfirm.resultImgCross)) {
            binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_selected);
            binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_unselected);
            checkResult = 1;
            binding.layoutResultConfirm.resultTvNext.setClickable(true);
        } else if (view.equals(binding.layoutResultConfirm.resultTvNext)) {
            mSpUtils.saveImeiCheckResult(checkResult);
            Log.i(TAG, "ImeiCheckResult: " + mSpUtils.getImeiCheckResult());
            startActivity(new Intent(ImeiTestActivity.this, CommunicationTestActivity.class));
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }
    }
}
