package com.handheld.huang.handsettest.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author huang
 */
public class GpsTestActivity extends AppCompatActivity {
    private static String TAG = GpsTestActivity.class.getSimpleName();

    @BindView(R.id.gps_img_ok)
    ImageView mGpsImgOk;
    @BindView(R.id.gps_img_cross)
    ImageView mGpsImgCross;
    @BindView(R.id.gps_tv_finish)
    TextView mFinishTvNext;
    @BindView(R.id.gps_btn_test)
    FancyButton mGpsBtnTest;
    @BindView(R.id.gps_ll_confirm)
    LinearLayout mGpsLlConfirm;
    @BindView(R.id.gps_ll_enter)
    LinearLayout mGpsLlEnter;
    private int checkResult;
    private SpUtils mSpUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);
        ButterKnife.bind(this);

        mFinishTvNext.setClickable(false);
        mSpUtils = new SpUtils(this);
    }


    @OnClick({R.id.gps_img_ok, R.id.gps_img_cross, R.id.gps_tv_finish, R.id.gps_btn_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gps_img_ok:
                mGpsImgOk.setImageResource(R.drawable.check_ok_selected);
                mGpsImgCross.setImageResource(R.drawable.check_cross_unselected);
                checkResult = 0;
                mFinishTvNext.setClickable(true);


                break;
            case R.id.gps_img_cross:
                mGpsImgCross.setImageResource(R.drawable.check_cross_selected);
                mGpsImgOk.setImageResource(R.drawable.check_ok_unselected);
                checkResult = 1;
                mFinishTvNext.setClickable(true);
                break;
            case R.id.gps_tv_finish:
                mSpUtils.saveGpsCheckResult(checkResult);
                Log.i(TAG, "GpsCheckResult: " + mSpUtils.getGpsCheckResult());
                finish();
                break;
            case R.id.gps_btn_test:
                ComponentName localComponentName = new ComponentName(
                        "com.chartcross.gpstest",
                        "com.chartcross.gpstest.GPSTest");
                Intent localIntent = new Intent();
                localIntent.setComponent(localComponentName);
                startActivity(localIntent);
                mGpsLlEnter.setVisibility(View.GONE);
                mGpsLlConfirm.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }


    @OnClick(R.id.gps_btn_test)
    public void onViewClicked() {
    }
}
