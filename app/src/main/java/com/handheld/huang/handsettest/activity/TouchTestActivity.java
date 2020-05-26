package com.handheld.huang.handsettest.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.view.TouchImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author huang
 */
public class TouchTestActivity extends AppCompatActivity {
    private static String TAG = TouchTestActivity.class.getSimpleName();

    @BindView(R.id.touch_tv_tips)
    TextView mTouchTvTips;
    @BindView(R.id.touch_img_canvas)
    TouchImageView mTouchImgCanvas;
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
    @BindView(R.id.result_tv_question)
    TextView mResultTvQuestion;
    private SpUtils mSpUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_test);
        ButterKnife.bind(this);

        mSpUtils = new SpUtils(this);

        //当触摸测试控件消失时，显示结果确认界面
        mTouchImgCanvas.setListener(new TouchImageView.AbstractGoneListener() {
            @Override
            public void gone() {
                mResultTvQuestion.setText(R.string.touch_test_confirm);
                mResultLlConfirm.setVisibility(View.VISIBLE);
            }
        });

        // 自动进入触摸测试
        onViewClicked(mTouchTvTips);
    }

    @OnClick({R.id.touch_tv_tips, R.id.result_img_ok, R.id.result_img_cross, R.id.result_tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.touch_tv_tips:
                mTouchTvTips.setVisibility(View.GONE);
                mTouchImgCanvas.setVisibility(View.VISIBLE);
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
                mSpUtils.saveToucheCheckResult(checkResult);
                Log.i(TAG, "TouchCheckResult: " + mSpUtils.getTouchCheckResult());
                Intent intent = new Intent(TouchTestActivity.this, SensorTestActivity.class);
//                intent.putExtra("testFlag", 1);
                startActivity(intent);
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
