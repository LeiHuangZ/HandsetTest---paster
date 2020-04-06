package com.handheld.huang.handsettest.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.StorageBean;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.StorageUtils;
import com.handheld.huang.handsettest.utils.Util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author huang
 */
public class BasicTestActivity extends AppCompatActivity {
    private static String TAG = BasicTestActivity.class.getSimpleName();

    @BindView(R.id.basic_btn_test)
    FancyButton mBasicBtnTest;
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
    /**
     * 用以标识测试功能，0 --> 相机；1 --> 通话，2 --> 存储卡，3 --> 喇叭
     */
    int testFlag = 0;
    int sdFlag = 2;
    int speakerFlag = 3;
    @BindView(R.id.basic_ll_enter)
    LinearLayout mBasicLlEnter;
    @BindView(R.id.basic_toolbar_title)
    Toolbar mBasicToolbarTitle;
    private SpUtils mSpUtils;

    private Toast mToast;
    private Util mUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_test);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        testFlag = intent.getIntExtra("testFlag", 0);
        if (testFlag == 1){
            mBasicToolbarTitle.setTitle(getString(R.string.call_test));
            mBasicBtnTest.setText(getString(R.string.start_call_test));
            mBasicBtnTest.setIconResource("\uf095");
        }else if (testFlag == 3){
            mBasicToolbarTitle.setTitle(getString(R.string.speaker_test));
            mBasicBtnTest.setText(getString(R.string.start_speaker_test));
            mBasicBtnTest.setIconResource("\uf028");
            mResultLlConfirm.setVisibility(View.VISIBLE);
            mResultTvQuestion.setText(R.string.speaker_test_confirm);
        }

        mSpUtils = new SpUtils(this);
        mUtil = new Util(this);
        mUtil.initAudio();

        // 自动进入测试
        onViewClicked(mBasicBtnTest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtil.closeAudio();
    }

    @OnClick({R.id.basic_btn_test, R.id.result_img_ok, R.id.result_img_cross, R.id.result_tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.basic_btn_test:
                if (testFlag == 0) {
                    try {
                        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);
                    } catch (Exception e) {
                        Toast.makeText(this, "设备相机连接异常，请检查硬件！", Toast.LENGTH_SHORT).show();
                        onViewClicked(mResultImgCross);
                        mResultImgCross.setClickable(false);
                        mResultImgOk.setClickable(false);
                    }
                    mBasicBtnTest.setClickable(false);
                } else if (testFlag == 1) {
                    startActivityForResult(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "")).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 1);
                    mBasicBtnTest.setClickable(false);
                } else if (testFlag == sdFlag) {
                    sdCheck();
                    mBasicBtnTest.setClickable(false);
                } else if (testFlag == speakerFlag) {
                    speakerCheck();
                }

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
                if (testFlag == 0) {
                    mSpUtils.saveCameraCheckResult(checkResult);
                    Log.i(TAG, "CameraCheckResult: " + mSpUtils.getCameraCheckResult());
//                    mResultLlConfirm.setVisibility(View.GONE);
//                    resetCheck();
//                    mBasicLlEnter.setVisibility(View.VISIBLE);
//                    mBasicLlEnter.setBackgroundColor(ContextCompat.getColor(this, R.color.call_test_color));
//                    mBasicBtnTest.setText(getResources().getString(R.string.start_call_test));
//                    mBasicBtnTest.setIconResource("\uf095");
//                    mBasicToolbarTitle.setTitle(R.string.call_test);
//                    testFlag = 1;
//
//                    mResultImgCross.setClickable(true);
//                    mResultImgOk.setClickable(true);
                    Intent intent = new Intent(BasicTestActivity.this, CommunicationTestActivity.class);
                    intent.putExtra("testFlag", 1);
                    startActivity(intent);
                    overridePendingTransition(R.animator.activity_start_rigth, 0);
                    finish();
                } else if (testFlag == 1) {
                    mSpUtils.saveCallCheckResult(checkResult);
                    Log.i(TAG, "CallCheckResult: " + mSpUtils.getCallCheckResult());
                    mResultLlConfirm.setVisibility(View.GONE);
                    resetCheck();
//                    mBasicLlEnter.setVisibility(View.VISIBLE);
//                    mBasicLlEnter.setBackgroundColor(ContextCompat.getColor(this, R.color.sd_test_color));
//                    mBasicBtnTest.setText(getResources().getString(R.string.start_sd_test));
//                    mBasicBtnTest.setIconResource("\uf02f");
//                    mBasicToolbarTitle.setTitle(R.string.sd_test);
//                    testFlag = 2;
                    Intent intent = new Intent(BasicTestActivity.this, CommunicationTestActivity.class);
//                    intent.putExtra("testFlag", 1);
                    startActivity(intent);
                    overridePendingTransition(R.animator.activity_start_rigth, 0);
                    finish();
                } else if (testFlag == sdFlag) {
                    mSpUtils.saveSdCheckResult(checkResult);
                    Log.i(TAG, "SdCheckResult: " + mSpUtils.getSdCheckResult());
                    mResultLlConfirm.setVisibility(View.GONE);
                    resetCheck();
                    mBasicLlEnter.setVisibility(View.VISIBLE);
                    mBasicLlEnter.setBackgroundColor(ContextCompat.getColor(this, R.color.speaker_test_color));
                    mBasicBtnTest.setText(getResources().getString(R.string.start_speaker_test));
                    mBasicBtnTest.setIconResource("\uf028");
                    mBasicToolbarTitle.setTitle(R.string.speaker_test);
                    mResultLlConfirm.setVisibility(View.VISIBLE);
                    mResultTvQuestion.setText(R.string.speaker_test_confirm);
                    testFlag = 3;
                } else if (testFlag == speakerFlag) {
                    mSpUtils.saveSpeakerCheckResult(checkResult);
                    Log.i(TAG, "SpeakerCheckResult: " + mSpUtils.getSpeakerCheckResult());
                    startActivity(new Intent(BasicTestActivity.this, DbmActivity.class));
                    overridePendingTransition(R.animator.activity_start_rigth, 0);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            mBasicLlEnter.setVisibility(View.GONE);
            mResultLlConfirm.setVisibility(View.VISIBLE);
            mResultTvQuestion.setText(R.string.camera_test_confirm);
        } else if (requestCode == 1) {
            Log.i(TAG, "onActivityResult: ");
            mBasicLlEnter.setVisibility(View.GONE);
            mResultLlConfirm.setVisibility(View.VISIBLE);
            mResultTvQuestion.setText(R.string.call_test_confirm);
        }
        mBasicBtnTest.setClickable(true);
    }

    /**
     * 在每一项测试结果确认后，重置结果确认界面，以准备下一次结果确认
     */
    private void resetCheck() {
        mResultTvNext.setClickable(false);
        mResultImgOk.setImageResource(R.drawable.check_ok_unselected);
        mResultImgCross.setImageResource(R.drawable.check_cross_unselected);
    }

    /**
     * SD卡测试
     */
    private void sdCheck() {
        try {
            ArrayList<StorageBean> storageBeanArrayList = StorageUtils.getStorageData(BasicTestActivity.this);
            int size = storageBeanArrayList.size();
//        List<String> pathList = getExtSDCardPathList();
            Log.e(TAG, "sdCheck, pathList.size() >>>>>> " + storageBeanArrayList.size());
            String path = storageBeanArrayList.get(1).getPath();
            Log.e(TAG, "sdCheck, secondaryStoragePath >>>>>> " + path);
//        Log.e(TAG, "sdCheck, secondaryStoragePath >>>>>> " + secondary_storage );
            File sdFile = new File(path);
            File[] files = sdFile.listFiles();
            Log.i(TAG, "sdCheck: " + files.length);
            showToast("存储卡读写正常！");
            checkResult = 0;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "sdCheck, ERROR >>>>>> " + e.getMessage());

            showToast("存储卡读写异常！");
            checkResult = 1;
        } finally {
            //延时发送消息,留时间给SD卡测试结果展示
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0);
                }
            }, 2000);
        }
    }

    /**
     * 喇叭测试
     */
    private void speakerCheck() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //最大音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

        mUtil.playAudio(1);
    }

    /**
     * 展示Toast
     *
     * @param data 需要展示的数据
     */
    private void showToast(String data) {
        if (mToast == null) {
            mToast = Toast.makeText(BasicTestActivity.this, data, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        } else {
            mToast.setText(data);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        }
    }


    private BasicTestHandler mHandler = new BasicTestHandler(this);

    private static class BasicTestHandler extends Handler {
        private WeakReference<BasicTestActivity> mWeakReference;

        BasicTestHandler(BasicTestActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BasicTestActivity activity = mWeakReference.get();
            activity.onViewClicked(activity.mResultTvNext);
            activity.mBasicBtnTest.setClickable(true);
        }
    }

    @Override
    public void onBackPressed() {
    }
}
