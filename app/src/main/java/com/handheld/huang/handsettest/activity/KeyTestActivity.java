package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityKeyTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.lang.ref.WeakReference;


/**
 * 4.键盘测试
 *
 * @author huang
 */
public class KeyTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = KeyTestActivity.class.getSimpleName();

    /**
     * 检查结果，默认为0  0 --> 通过，1 --> 不通过
     */
    int checkResult = 0;
    private SpUtils mSpUtils;
    private com.handheld.huang.handsettest.databinding.ActivityKeyTestBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKeyTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        mSpUtils = new SpUtils(this);

        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);

        binding.layoutResultConfirm.resultTvNext.setClickable(false);
        binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
        binding.layoutResultConfirm.resultTvQuestion.setText(R.string.key_test_confirm);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    /**
     * 接收键盘广播，显示已经测试的按键
     */
    boolean isRed = false;
    private MyReceiver mReceiver = new MyReceiver(this);

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
            mSpUtils.saveKeyCheckResult(checkResult);
            Log.i(TAG, "KeyCheckResult: " + mSpUtils.getKeyCheckResult());
            startActivity(new Intent(KeyTestActivity.this, IndicatorTestActivity.class));
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }
    }

    private static class MyReceiver extends BroadcastReceiver {
        private WeakReference<KeyTestActivity> mWeakReference;
        private String mStr;

        MyReceiver(KeyTestActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (!keyDown) {
                if (mWeakReference.get().isRed) {
                    mStr = String.format("<font color=\"#FF2D2D\">%s", "按键Code = " + keyCode);
                    mWeakReference.get().isRed = false;
                } else {
                    mStr = String.format("<font color=\"#0000C6\">%s", "按键Code = " + keyCode);
                    mWeakReference.get().isRed = true;
                }
                Spanned spanned = Html.fromHtml(mStr);
                mWeakReference.get().binding.keyTvShow.append(spanned);
                mWeakReference.get().binding.keyTvShow.append("\n");
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String mStr;
        if (isRed) {
            mStr = String.format("<font color=\"#FF2D2D\">%s", "按键Code = " + keyCode);
            isRed = false;
        } else {
            mStr = String.format("<font color=\"#0000C6\">%s", "按键Code = " + keyCode);
            isRed = true;
        }
        Spanned spanned = Html.fromHtml(mStr);
        Log.i(TAG, "onReceive: keyCode = " + spanned);
        binding.keyTvShow.append(spanned);
        binding.keyTvShow.append("\n");
        return super.onKeyUp(keyCode, event);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }
}
