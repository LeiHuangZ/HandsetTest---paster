package com.handheld.huang.handsettest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
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
 * 4.键盘测试
 *
 * @author huang
 */
public class KeyTestActivity extends AppCompatActivity {
    private static String TAG = KeyTestActivity.class.getSimpleName();

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
    /**
     * 检查结果，默认为0  0 --> 通过，1 --> 不通过
     */
    int checkResult = 0;
    @BindView(R.id.key_tv_show)
    TextView mKeyTvShow;
    private SpUtils mSpUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_test);
        ButterKnife.bind(this);

        mSpUtils = new SpUtils(this);

        mResultTvNext.setClickable(false);
        mResultLlConfirm.setVisibility(View.VISIBLE);
        mResultTvQuestion.setText(R.string.key_test_confirm);
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
                mSpUtils.saveKeyCheckResult(checkResult);
                Log.i(TAG, "KeyCheckResult: " + mSpUtils.getKeyCheckResult());
                startActivity(new Intent(KeyTestActivity.this, IndicatorTestActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth,0);
                finish();
                break;
            default:
                break;
        }
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
                }else {
                    mStr = String.format("<font color=\"#0000C6\">%s", "按键Code = " + keyCode);
                    mWeakReference.get().isRed = true;
                }
                Spanned spanned = Html.fromHtml(mStr);
                mWeakReference.get().mKeyTvShow.append(spanned);
                mWeakReference.get().mKeyTvShow.append("\n");
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String mStr;
        if (isRed) {
            mStr = String.format("<font color=\"#FF2D2D\">%s", "按键Code = " + keyCode);
            isRed = false;
        }else {
            mStr = String.format("<font color=\"#0000C6\">%s", "按键Code = " + keyCode);
            isRed = true;
        }
        Spanned spanned = Html.fromHtml(mStr);
        Log.i(TAG, "onReceive: keyCode = " + spanned);
        mKeyTvShow.append(spanned);
        mKeyTvShow.append("\n");
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {

    }
}
