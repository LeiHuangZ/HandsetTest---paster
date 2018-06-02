package com.handheld.huang.handsettest.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsbActivity extends AppCompatActivity {
    private static String TAG = UsbActivity.class.getSimpleName();

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);
        ButterKnife.bind(this);

        mSpUtils = new SpUtils(this);
        mResultTvNext.setClickable(false);
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
                mSpUtils.saveUsbCheckResult(onCheckResult);
                Log.i(TAG, "getUsbCheckResult: " + mSpUtils.getUsbCheckResult());
                startActivity(new Intent(UsbActivity.this, ChargerActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth,0);
                finish();
                break;
            default:
                break;
        }
    }

}
