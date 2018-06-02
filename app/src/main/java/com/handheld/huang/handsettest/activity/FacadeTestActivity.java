package com.handheld.huang.handsettest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
 * @author huang
 */
public class FacadeTestActivity extends AppCompatActivity {
    private static String TAG = FacadeTestActivity.class.getSimpleName();

    @BindView(R.id.facade_tv_tips)
    TextView mFacadeTvTips;
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

    private int checkResult;
    private SpUtils mSpUtils;
    private FacadeHandler mHandler = new FacadeHandler(this);

    /**
     * 检查flag，0 --> 背面螺丝，1 --> 头部螺丝， 2 --> 底部螺丝， 3-->镜片logo， 4-->镜片前舱盖， 5-->腕带， 6-->壳体
     */
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facade_test);
        ButterKnife.bind(this);

        mSpUtils = new SpUtils(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 1500);
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
                switch (flag) {
                    case 0:
                        mSpUtils.saveBackScrewCheckResult(checkResult);
                        Log.i(TAG, "BackScrewCheckResult: " + mSpUtils.getBackScrewCheckResult());
                        mResultLlConfirm.setVisibility(View.GONE);
                        flag = 1;
                        mHandler.sendEmptyMessage(0);
                        break;
                    case 1:
                        mSpUtils.saveHeadScrewCheckResult(checkResult);
                        Log.i(TAG, "HeadScrewCheckResult: " + mSpUtils.getHeadScrewCheckResult());
                        mResultLlConfirm.setVisibility(View.GONE);
                        flag = 2;
                        mHandler.sendEmptyMessage(0);
                        break;
                    case 2:
                        mSpUtils.saveBottomScrewCheckResult(checkResult);
                        Log.i(TAG, "BottomScrewCheckResult: " + mSpUtils.getBottomScrewCheckResult());
                        mResultLlConfirm.setVisibility(View.GONE);
                        flag = 3;
                        mHandler.sendEmptyMessage(0);
                        break;
                    case 3:
                        mSpUtils.saveLensLogoCheckResult(checkResult);
                        Log.i(TAG, "LensLogoCheckResult: " + mSpUtils.getLensLogoCheckResult());
                        mResultLlConfirm.setVisibility(View.GONE);
                        flag = 4;
                        mHandler.sendEmptyMessage(0);
                        break;
                    case 4:
                        mSpUtils.saveLensHatchesCheckResult(checkResult);
                        Log.i(TAG, "LensHatchesCheckResult: " + mSpUtils.getLensHatchesCheckResult());
                        mResultLlConfirm.setVisibility(View.GONE);
                        flag = 5;
                        mHandler.sendEmptyMessage(0);
                        break;
                    case 5:
                        mSpUtils.saveSpireCheckResult(checkResult);
                        Log.i(TAG, "SpireCheckResult: " + mSpUtils.getSpireCheckResult());
                        mResultLlConfirm.setVisibility(View.GONE);
                        flag = 6;
                        mHandler.sendEmptyMessage(0);
                        break;
                    case 6:
                        mSpUtils.saveShellCheckResult(checkResult);
                        Log.i(TAG, "ShellCheckResult: " + mSpUtils.getShellCheckResult());
                        startActivity(new Intent(FacadeTestActivity.this, TestConclusionActivity.class));
                        overridePendingTransition(R.animator.activity_start_rigth,0);
                        finish();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private static class FacadeHandler extends Handler {
        private WeakReference<FacadeTestActivity> mWeakReference;
        private FacadeTestActivity mActivity;

        FacadeHandler(FacadeTestActivity testActivity) {
            mWeakReference = new WeakReference<>(testActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mActivity = mWeakReference.get();
            if (msg.what == 0) {
                switch (mActivity.flag) {
                    case 0:
                        mActivity.mFacadeTvTips.setText(R.string.back_screw_test);
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 1:
                        mActivity.mFacadeTvTips.setText(R.string.head_screw_test);
                        mActivity.mFacadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 2:
                        mActivity.mFacadeTvTips.setText(R.string.bottom_screw_test);
                        mActivity.mFacadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 3:
                        mActivity.mFacadeTvTips.setText(R.string.lens_logo_test);
                        mActivity.mFacadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 4:
                        mActivity.mFacadeTvTips.setText(R.string.lens_hatches_test);
                        mActivity.mFacadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 5:
                        mActivity.mFacadeTvTips.setText(R.string.spire_test);
                        mActivity.mFacadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 6:
                        mActivity.mFacadeTvTips.setText(R.string.shell_test);
                        mActivity.mFacadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    default:
                        break;
                }
            }else if (msg.what == 1){
                switch (mActivity.flag) {
                    case 0:
                        mActivity.mFacadeTvTips.setVisibility(View.GONE);
                        mActivity.mResultTvQuestion.setText(R.string.back_screw_test_result);
                        mActivity.mResultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mActivity.mFacadeTvTips.setVisibility(View.GONE);
                        mActivity.mResultTvQuestion.setText(R.string.head_screw_test_result);
                        mActivity.mResultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mActivity.mFacadeTvTips.setVisibility(View.GONE);
                        mActivity.mResultTvQuestion.setText(R.string.bottom_screw_test_result);
                        mActivity.mResultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mActivity.mFacadeTvTips.setVisibility(View.GONE);
                        mActivity.mResultTvQuestion.setText(R.string.lens_logo_test_result);
                        mActivity.mResultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        mActivity.mFacadeTvTips.setVisibility(View.GONE);
                        mActivity.mResultTvQuestion.setText(R.string.lens_hatches_test_result);
                        mActivity.mResultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        mActivity.mFacadeTvTips.setVisibility(View.GONE);
                        mActivity.mResultTvQuestion.setText(R.string.spire_test_result);
                        mActivity.mResultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        mActivity.mFacadeTvTips.setVisibility(View.GONE);
                        mActivity.mResultTvQuestion.setText(R.string.shell_test_result);
                        mActivity.mResultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void resetConfirm(){
        mResultTvNext.setClickable(false);
        mResultImgOk.setImageResource(R.drawable.check_ok_unselected);
        mResultImgCross.setImageResource(R.drawable.check_cross_unselected);
    }
}
