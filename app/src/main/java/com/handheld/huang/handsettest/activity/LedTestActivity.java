package com.handheld.huang.handsettest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 3.屏幕背光测试
 * @author huang
 */
public class LedTestActivity extends AppCompatActivity {
    private static String TAG = LedTestActivity.class.getSimpleName();

    @BindView(R.id.led_sb_progress)
    SeekBar mLedSbProgress;
    @BindView(R.id.result_img_ok)
    ImageView mResultImgOk;
    @BindView(R.id.result_img_cross)
    ImageView mResultImgCross;
    @BindView(R.id.result_tv_next)
    TextView mResultTvNext;
    @BindView(R.id.result_ll_confirm)
    LinearLayout mResultLlConfirm;
    @BindView(R.id.result_tv_question)
    TextView mResultTvQuestion;

    private int screenLight = 0;

    /**
     * 检查结果，默认为0  0 --> 通过，1 --> 不通过
     */
    int checkResult = 0;
    private SpUtils mSpUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_test);
        ButterKnife.bind(this);

        setScreenBrightness(0);

        mSpUtils = new SpUtils(this);

        mResultTvQuestion.setText(R.string.led_test_confirm);
        mResultLlConfirm.setVisibility(View.VISIBLE);
        mResultTvNext.setClickable(false);
        //随着SeekBar滑动，屏幕亮度跟随变化，以测试屏幕背光
        mLedSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                screenLight = progress;
                setScreenBrightness(screenLight);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setScreenBrightness(screenLight);
            }
        });
    }

    /**
     * 设置背光亮度
     *
     * @param brightness 亮度
     */
    private void setScreenBrightness(int brightness) {
        //不让屏幕全暗
        if (brightness <= 1) {
            brightness = 1;
        }
        //设置当前activity的屏幕亮度
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        //0到1,调整亮度暗到全亮
        lp.screenBrightness = brightness / 255f;
        this.getWindow().setAttributes(lp);

        //保存为系统亮度方法1
        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                brightness);
    }

    @OnClick({R.id.result_img_ok, R.id.result_img_cross, R.id.result_tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
                mSpUtils.saveLedCheckResult(checkResult);
                Log.i(TAG, "LedCheckResult: " + mSpUtils.getLedCheckResult());
                startActivity(new Intent(LedTestActivity.this, KeyTestActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth,0);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
