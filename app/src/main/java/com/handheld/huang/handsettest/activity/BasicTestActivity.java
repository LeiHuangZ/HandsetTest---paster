package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.StorageBean;
import com.handheld.huang.handsettest.databinding.ActivityBasicTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.StorageUtils;
import com.handheld.huang.handsettest.utils.Util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author huang
 */
public class BasicTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = BasicTestActivity.class.getSimpleName();
    /**
     * 检查结果，默认为0  0 --> 通过，1 --> 不通过
     */
    int checkResult = 1;
    /**
     * 用以标识测试功能，0 --> 相机；1 --> 通话，2 --> 存储卡，3 --> 喇叭
     */
    int testFlag = 0;
    int sdFlag = 2;
    int speakerFlag = 3;
    private SpUtils mSpUtils;

    private Toast mToast;
    private Util mUtil;
    private com.handheld.huang.handsettest.databinding.ActivityBasicTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBasicTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        testFlag = intent.getIntExtra("testFlag", 0);
        if (testFlag == 1) {
            binding.basicToolbarTitle.setTitle(getString(R.string.call_test));
            binding.basicBtnTest.setText(getString(R.string.start_call_test));
            binding.basicBtnTest.setIconResource("\uf095");
        } else if (testFlag == 3) {
            binding.basicToolbarTitle.setTitle(getString(R.string.speaker_test));
            binding.basicBtnTest.setText(getString(R.string.start_speaker_test));
            binding.basicBtnTest.setIconResource("\uf028");
            binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
            binding.layoutResultConfirm.resultTvQuestion.setText(R.string.speaker_test_confirm);
        }

        mSpUtils = new SpUtils(this);
        mUtil = new Util(this);
        mUtil.initAudio();

        binding.basicBtnTest.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);

        // 自动进入测试
        binding.basicBtnTest.callOnClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtil.closeAudio();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            binding.basicLlEnter.setVisibility(View.GONE);
            binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
            binding.layoutResultConfirm.resultTvQuestion.setText(R.string.camera_test_confirm);
        } else if (requestCode == 1) {
            Log.i(TAG, "onActivityResult: ");
            binding.basicLlEnter.setVisibility(View.GONE);
            binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
            binding.layoutResultConfirm.resultTvQuestion.setText(R.string.call_test_confirm);
        }
        binding.basicBtnTest.setClickable(true);
    }

    /**
     * 在每一项测试结果确认后，重置结果确认界面，以准备下一次结果确认
     */
    private void resetCheck() {
        binding.layoutResultConfirm.resultTvNext.setClickable(false);
        binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_unselected);
        binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_unselected);
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

    @Override
    public void onClick(@NonNull View view) {
        if (view == binding.basicBtnTest) {
            if (testFlag == 0) {
                try {
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);
                } catch (Exception e) {
                    Toast.makeText(this, "设备相机连接异常，请检查硬件！", Toast.LENGTH_SHORT).show();
                    binding.layoutResultConfirm.resultImgCross.callOnClick();
                    binding.layoutResultConfirm.resultImgCross.setClickable(false);
                    binding.layoutResultConfirm.resultImgOk.setClickable(false);
                }
                binding.basicBtnTest.setClickable(false);
            } else if (testFlag == 1) {
                try {
                    startActivityForResult(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "")).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 1);
                    binding.basicBtnTest.setClickable(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.layoutResultConfirm.resultTvNext.callOnClick();
                }
            } else if (testFlag == sdFlag) {
                sdCheck();
                binding.basicBtnTest.setClickable(false);
            } else if (testFlag == speakerFlag) {
                speakerCheck();
            }
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
            if (testFlag == 0) {
                mSpUtils.saveCameraCheckResult(checkResult);
                Log.i(TAG, "CameraCheckResult: " + mSpUtils.getCameraCheckResult());
                Intent intent = new Intent(BasicTestActivity.this, CommunicationTestActivity.class);
                intent.putExtra("testFlag", 1);
                startActivity(intent);
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
            } else if (testFlag == 1) {
                mSpUtils.saveCallCheckResult(checkResult);
                Log.i(TAG, "CallCheckResult: " + mSpUtils.getCallCheckResult());
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                resetCheck();
                Intent intent = new Intent(BasicTestActivity.this, CommunicationTestActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
            } else if (testFlag == sdFlag) {
                mSpUtils.saveSdCheckResult(checkResult);
                Log.i(TAG, "SdCheckResult: " + mSpUtils.getSdCheckResult());
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                resetCheck();
                binding.basicLlEnter.setVisibility(View.VISIBLE);
                binding.basicLlEnter.setBackgroundColor(ContextCompat.getColor(this, R.color.speaker_test_color));
                binding.basicBtnTest.setText(getResources().getString(R.string.start_speaker_test));
                binding.basicBtnTest.setIconResource("\uf028");
                binding.basicToolbarTitle.setTitle(R.string.speaker_test);
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                binding.layoutResultConfirm.resultTvQuestion.setText(R.string.speaker_test_confirm);
                testFlag = 3;
            } else if (testFlag == speakerFlag) {
                mSpUtils.saveSpeakerCheckResult(checkResult);
                Log.i(TAG, "SpeakerCheckResult: " + mSpUtils.getSpeakerCheckResult());
                startActivity(new Intent(BasicTestActivity.this, DbmActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
            }
        }
    }

    private static class BasicTestHandler extends Handler {
        private WeakReference<BasicTestActivity> mWeakReference;

        BasicTestHandler(BasicTestActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BasicTestActivity activity = mWeakReference.get();
            activity.binding.layoutResultConfirm.resultTvNext.callOnClick();
            activity.binding.basicBtnTest.setClickable(true);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }
}
