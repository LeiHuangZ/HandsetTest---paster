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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.CodeUtil;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author huang
 */
public class ImeiTestActivity extends AppCompatActivity {
    private static String TAG = ImeiTestActivity.class.getSimpleName();

    @BindView(R.id.imei_btn_test)
    FancyButton mImeiBtnTest;
    @BindView(R.id.imei_ll_enter)
    LinearLayout mImeiLlEnter;
    @BindView(R.id.imei_img_show)
    ImageView mImeiImgShow;
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
    int checkResult;
    private Util mUtil;
    private Toast mToast;
    private SpUtils mSpUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imei_test);
        ButterKnife.bind(this);
        mUtil = new Util(this);
        mUtil.initAudio();
        mSpUtils = new SpUtils(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtil.closeAudio();
    }

    String originImei = "860527010207000";

    @OnClick({R.id.imei_btn_test, R.id.result_img_ok, R.id.result_img_cross, R.id.result_tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imei_btn_test:
                String imei = getImei();
                Log.i(TAG, "imei : " + imei);
                if (imei == null || originImei.equals(imei)) {
                    showToast("原始IMEI地址，IMEI测试不通过！");
                    mUtil.playAudio(2);
                    mImeiBtnTest.setText("原始IMEI地址，IMEI测试不通过！请点击下一步");
                    mResultLlConfirm.setVisibility(View.VISIBLE);
                    onViewClicked(mResultImgCross);
                    mResultImgOk.setClickable(false);
                    mResultImgCross.setClickable(false);
                } else {
                    Bitmap qrImage;
                    qrImage = CodeUtil.creatBarcode(ImeiTestActivity.this, imei, 800, 400, new PointF(0, 250), true);
                    showToast("检测通过！");
                    mImeiLlEnter.setVisibility(View.GONE);
                    mImeiImgShow.setVisibility(View.VISIBLE);
                    mImeiImgShow.setImageBitmap(qrImage);
                    mResultLlConfirm.setVisibility(View.VISIBLE);
                    mResultTvQuestion.setText(R.string.imei_test);
                    onViewClicked(mResultImgOk);
                    mResultImgOk.setClickable(false);
                    mResultImgCross.setClickable(false);
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
                mSpUtils.saveImeiCheckResult(checkResult);
                Log.i(TAG, "ImeiCheckResult: " + mSpUtils.getImeiCheckResult());
                startActivity(new Intent(ImeiTestActivity.this, CommunicationTestActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
                break;
            default:
                break;
        }
    }

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

    @Override
    public void onBackPressed() {

    }
}
