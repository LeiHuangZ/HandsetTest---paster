package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityDisplayTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.lang.ref.WeakReference;

/**
 * 屏幕测试活动 测试完成 --> 触摸屏测试活动
 *
 * @author huang
 */
public class DisplayTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = DisplayTestActivity.class.getSimpleName();

    /**
     * 图片展示计数，为了记录图片展示进度，如果等于8，进度完成就会进入确认流程
     */
    int count = 0;
    /**
     * 图片计数最大值，如果计数超过该最大值，停止图片展示，进入结果确认
     */
    int maxCount = 5;
    /**
     * 检查结果，默认为0  0 --> 通过，1 --> 不通过
     */
    int checkResult = 0;
    private SpUtils mSpUtils;
    private com.handheld.huang.handsettest.databinding.ActivityDisplayTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        //初始化SP存储工具类，存储检测结果
        mSpUtils = new SpUtils(this);

        binding.displayTvTips.setOnClickListener(this);
        binding.displayImgColor.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);

        // 自动进入屏幕测试
        binding.displayTvTips.callOnClick();
    }

    /**
     * 屏幕测试显示图片
     *
     * @param i 标记，用以标识图片显示的颜色，显示不同颜色测试屏幕
     */
    private void showImageView(int i) {
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getRealSize(outSize);
        int screenWidth = outSize.x;
        int screenHeight = outSize.y;
        Bitmap bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        switch (i) {
            case 0:
                canvas.drawColor(Color.BLUE);
                break;
            case 1:
                canvas.drawColor(Color.RED);
                break;
            case 2:
                canvas.drawColor(Color.GREEN);
                break;
            case 3:
                canvas.drawColor(Color.BLACK);
            default:
                break;
        }
        binding.displayImgColor.setImageBitmap(bitmap);
    }

    /**
     * 使用Handler定时任务，为了进行定时的颜色测试
     */
    DisplayHandler mHandler = new DisplayHandler(this);

    @Override
    public void onClick(@NonNull View view) {
        if (view == binding.displayImgColor) {
            mHandler.post(() -> mHandler.sendEmptyMessage(0));
            //点击屏幕进入屏幕测试阶段
        } else if (view == binding.displayTvTips) {
            binding.displayTvTips.setVisibility(View.GONE);
            binding.displayImgColor.setVisibility(View.VISIBLE);
            showImageView(0);
            count++;
            binding.layoutResultConfirm.resultTvNext.setClickable(false);
        } else if (view == binding.layoutResultConfirm.resultImgOk) {
            binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_selected);
            binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_unselected);
            checkResult = 0;
            binding.layoutResultConfirm.resultTvNext.setClickable(true);
        } else if (view == binding.layoutResultConfirm.resultImgCross) {
            binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_selected);
            binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_unselected);
            checkResult = 1;
            binding.layoutResultConfirm.resultTvNext.setClickable(true);
        } else if (view == binding.layoutResultConfirm.resultTvNext) {
            mSpUtils.saveDisplayCheckResult(checkResult);
            Log.i(TAG, "DisplayCheckResult: " + mSpUtils.getDisplayCheckResult());
            startActivity(new Intent(DisplayTestActivity.this, TouchTestActivity.class));
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }

    }

    static class DisplayHandler extends Handler {
        private WeakReference<DisplayTestActivity> mReference;
        private DisplayTestActivity mActivity;

        DisplayHandler(DisplayTestActivity activity) {
            mReference = new WeakReference<>(activity);
            mActivity = mReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity.count < mActivity.maxCount) {
                mActivity.showImageView(mActivity.count);
                mActivity.count++;
                if (mActivity.count >= mActivity.maxCount) {
                    mActivity.binding.displayImgColor.setVisibility(View.GONE);
                    mActivity.binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                    return;
                }
//                mActivity.mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mActivity.mHandler.sendEmptyMessage(0);
//                    }
//                }, 800);
                //如果计数超过最大值，即屏幕测试完成，进入确认流程
            }
        }
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
}
