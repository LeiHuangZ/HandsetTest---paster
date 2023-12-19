package com.handheld.huang.handsettest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityFacadeTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.lang.ref.WeakReference;

/**
 * @author huang
 */
public class FacadeTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = FacadeTestActivity.class.getSimpleName();

    private int checkResult;
    private SpUtils mSpUtils;
    private FacadeHandler mHandler = new FacadeHandler(this);

    /**
     * 检查flag，0 --> 背面螺丝，1 --> 头部螺丝， 2 --> 底部螺丝， 3-->镜片logo， 4-->镜片前舱盖， 5-->腕带， 6-->壳体
     */
    private int flag = 0;
    private com.handheld.huang.handsettest.databinding.ActivityFacadeTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacadeTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        mSpUtils = new SpUtils(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 1500);

        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
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
            switch (flag) {
                case 0:
                    mSpUtils.saveBackScrewCheckResult(checkResult);
                    Log.i(TAG, "BackScrewCheckResult: " + mSpUtils.getBackScrewCheckResult());
                    binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                    flag = 1;
                    mHandler.sendEmptyMessage(0);
                    break;
                case 1:
                    mSpUtils.saveHeadScrewCheckResult(checkResult);
                    Log.i(TAG, "HeadScrewCheckResult: " + mSpUtils.getHeadScrewCheckResult());
                    binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                    flag = 2;
                    mHandler.sendEmptyMessage(0);
                    break;
                case 2:
                    mSpUtils.saveBottomScrewCheckResult(checkResult);
                    Log.i(TAG, "BottomScrewCheckResult: " + mSpUtils.getBottomScrewCheckResult());
                    binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                    flag = 3;
                    mHandler.sendEmptyMessage(0);
                    break;
                case 3:
                    mSpUtils.saveLensLogoCheckResult(checkResult);
                    Log.i(TAG, "LensLogoCheckResult: " + mSpUtils.getLensLogoCheckResult());
                    binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                    flag = 4;
                    mHandler.sendEmptyMessage(0);
                    break;
                case 4:
                    mSpUtils.saveLensHatchesCheckResult(checkResult);
                    Log.i(TAG, "LensHatchesCheckResult: " + mSpUtils.getLensHatchesCheckResult());
                    binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                    flag = 5;
                    mHandler.sendEmptyMessage(0);
                    break;
                case 5:
                    mSpUtils.saveSpireCheckResult(checkResult);
                    Log.i(TAG, "SpireCheckResult: " + mSpUtils.getSpireCheckResult());
                    binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                    flag = 6;
                    mHandler.sendEmptyMessage(0);
                    break;
                case 6:
                    mSpUtils.saveShellCheckResult(checkResult);
                    Log.i(TAG, "ShellCheckResult: " + mSpUtils.getShellCheckResult());
                    startActivity(new Intent(FacadeTestActivity.this, TestConclusionActivity.class));
                    overridePendingTransition(R.animator.activity_start_rigth, 0);
                    finish();
                    break;
                default:
                    break;
            }
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
                        mActivity.binding.facadeTvTips.setText(R.string.back_screw_test);
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 1:
                        mActivity.binding.facadeTvTips.setText(R.string.head_screw_test);
                        mActivity.binding.facadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 2:
                        mActivity.binding.facadeTvTips.setText(R.string.bottom_screw_test);
                        mActivity.binding.facadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 3:
                        mActivity.binding.facadeTvTips.setText(R.string.lens_logo_test);
                        mActivity.binding.facadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 4:
                        mActivity.binding.facadeTvTips.setText(R.string.lens_hatches_test);
                        mActivity.binding.facadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 5:
                        mActivity.binding.facadeTvTips.setText(R.string.spire_test);
                        mActivity.binding.facadeTvTips.setVisibility(View.VISIBLE);
                        mActivity.resetConfirm();
                        mActivity.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.mHandler.sendEmptyMessage(1);
                            }
                        }, 2500);
                        break;
                    case 6:
                        mActivity.binding.facadeTvTips.setText(R.string.shell_test);
                        mActivity.binding.facadeTvTips.setVisibility(View.VISIBLE);
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
            } else if (msg.what == 1) {
                switch (mActivity.flag) {
                    case 0:
                        mActivity.binding.facadeTvTips.setVisibility(View.GONE);
                        mActivity.binding.layoutResultConfirm.resultTvQuestion.setText(R.string.back_screw_test_result);
                        mActivity.binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mActivity.binding.facadeTvTips.setVisibility(View.GONE);
                        mActivity.binding.layoutResultConfirm.resultTvQuestion.setText(R.string.head_screw_test_result);
                        mActivity.binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mActivity.binding.facadeTvTips.setVisibility(View.GONE);
                        mActivity.binding.layoutResultConfirm.resultTvQuestion.setText(R.string.bottom_screw_test_result);
                        mActivity.binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mActivity.binding.facadeTvTips.setVisibility(View.GONE);
                        mActivity.binding.layoutResultConfirm.resultTvQuestion.setText(R.string.lens_logo_test_result);
                        mActivity.binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        mActivity.binding.facadeTvTips.setVisibility(View.GONE);
                        mActivity.binding.layoutResultConfirm.resultTvQuestion.setText(R.string.lens_hatches_test_result);
                        mActivity.binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        mActivity.binding.facadeTvTips.setVisibility(View.GONE);
                        mActivity.binding.layoutResultConfirm.resultTvQuestion.setText(R.string.spire_test_result);
                        mActivity.binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        mActivity.binding.facadeTvTips.setVisibility(View.GONE);
                        mActivity.binding.layoutResultConfirm.resultTvQuestion.setText(R.string.shell_test_result);
                        mActivity.binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void resetConfirm() {
        binding.layoutResultConfirm.resultTvNext.setClickable(false);
        binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_unselected);
        binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_unselected);
    }
}
