package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityTouchTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.view.TouchImageView;


/**
 * @author huang
 */
public class TouchTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = TouchTestActivity.class.getSimpleName();

    /**
     * 检查结果，默认为0  0 --> 通过，1 --> 不通过
     */
    int checkResult = 0;
    private SpUtils mSpUtils;
    private com.handheld.huang.handsettest.databinding.ActivityTouchTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTouchTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        mSpUtils = new SpUtils(this);

        //当触摸测试控件消失时，显示结果确认界面
        binding.touchImgCanvas.setListener(new TouchImageView.AbstractGoneListener() {
            @Override
            public void gone() {
                binding.layoutResultConfirm.resultTvQuestion.setText(R.string.touch_test_confirm);
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
            }
        });

        binding.touchTvTips.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);

        // 自动进入触摸测试
        binding.touchTvTips.callOnClick();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.equals(binding.touchTvTips)) {
            binding.touchTvTips.setVisibility(View.GONE);
            binding.touchImgCanvas.setVisibility(View.VISIBLE);
            binding.layoutResultConfirm.resultTvNext.setClickable(false);
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
            mSpUtils.saveToucheCheckResult(checkResult);
            Log.i(TAG, "TouchCheckResult: " + mSpUtils.getTouchCheckResult());
            Intent intent = new Intent(TouchTestActivity.this, SensorTestActivity.class);
//                intent.putExtra("testFlag", 1);
            startActivity(intent);
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }
    }
}
