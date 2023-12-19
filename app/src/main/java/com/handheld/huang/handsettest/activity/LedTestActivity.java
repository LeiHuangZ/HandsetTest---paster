package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityLedTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

/**
 * 3.屏幕背光测试
 *
 * @author huang
 */
public class LedTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = LedTestActivity.class.getSimpleName();

    private int screenLight = 0;

    /**
     * 检查结果，默认为0  0 --> 通过，1 --> 不通过
     */
    int checkResult = 0;
    private SpUtils mSpUtils;
    private com.handheld.huang.handsettest.databinding.ActivityLedTestBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLedTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        setScreenBrightness(0);

        mSpUtils = new SpUtils(this);

        binding.layoutResultConfirm.resultTvQuestion.setText(R.string.led_test_confirm);
        binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
        binding.layoutResultConfirm.resultTvNext.setClickable(false);
        //随着SeekBar滑动，屏幕亮度跟随变化，以测试屏幕背光
        binding.ledSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.equals(binding.layoutResultConfirm.resultImgOk)) {
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
            mSpUtils.saveLedCheckResult(checkResult);
            Log.i(TAG, "LedCheckResult: " + mSpUtils.getLedCheckResult());
//                Intent intent = new Intent(LedTestActivity.this, BasicTestActivity.class);
//                intent.putExtra("testFlag", 3);
//                startActivity(intent);
//
//                overridePendingTransition(R.animator.activity_start_rigth,0);
            startActivity(new Intent(LedTestActivity.this, DbmActivity.class));
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }
    }
}
