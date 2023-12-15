package com.handheld.huang.handsettest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 屏幕测试活动 测试完成 --> 触摸屏测试活动
 *
 * @author huang
 */
public class DisplayTestActivity extends AppCompatActivity {
    private static String TAG = DisplayTestActivity.class.getSimpleName();

    @BindView(R.id.display_img_color)
    ImageView mDisplayImgColor;
    @BindView(R.id.display_tv_tips)
    TextView mDisplayTvTips;
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
    @BindView(R.id.result_img_ok)
    ImageView mResultImgOk;
    @BindView(R.id.result_img_cross)
    ImageView mResultImgCross;
    @BindView(R.id.result_tv_next)
    TextView mResultTvNext;
    @BindView(R.id.result_ll_confirm)
    LinearLayout mResultLlConfirm;
    private SpUtils mSpUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_test);
        ButterKnife.bind(this);

        //初始化SP存储工具类，存储检测结果
        mSpUtils = new SpUtils(this);

        // 自动进入屏幕测试
        onViewClicked(mDisplayTvTips);
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
        mDisplayImgColor.setImageBitmap(bitmap);
    }

    @OnClick({R.id.display_tv_tips, R.id.display_img_color, R.id.result_img_ok, R.id.result_img_cross, R.id.result_tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.display_img_color:
                mHandler.post(() -> mHandler.sendEmptyMessage(0));
                break;
            //点击屏幕进入屏幕测试阶段
            case R.id.display_tv_tips:
                mDisplayTvTips.setVisibility(View.GONE);
                mDisplayImgColor.setVisibility(View.VISIBLE);
                showImageView(0);
                count++;
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mHandler.sendEmptyMessage(0);
//                    }
//                }, 800);
                mResultTvNext.setClickable(false);
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
                mSpUtils.saveDisplayCheckResult(checkResult);
                Log.i(TAG, "DisplayCheckResult: " + mSpUtils.getDisplayCheckResult());
                startActivity(new Intent(DisplayTestActivity.this, TouchTestActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 使用Handler定时任务，为了进行定时的颜色测试
     */
    DisplayHandler mHandler = new DisplayHandler(this);

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
                    mActivity.mDisplayImgColor.setVisibility(View.GONE);
                    mActivity.mResultLlConfirm.setVisibility(View.VISIBLE);
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
