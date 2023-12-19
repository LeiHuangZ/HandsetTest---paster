package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityCommunicationTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.Util;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author huang
 */
public class CommunicationTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = CommunicationTestActivity.class.getSimpleName();
    /**
     * 检查结果，默认为0  0 --> 通过，1 --> 不通过
     */
    int checkResult = 1;
    /**
     * 用以标识测试功能，0 --> wifi；1 --> mic，2 --> gps，3 --> 蓝牙
     */
    int testFlag = 0;
    int bluetoothFlag = 3;
    private SpUtils mSpUtils;
    private Util mUtil;
    private com.handheld.huang.handsettest.databinding.ActivityCommunicationTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommunicationTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        testFlag = intent.getIntExtra("testFlag", 0);

        mSpUtils = new SpUtils(this);
        mUtil = new Util(this);
        mUtil.initAudio();

        binding.layoutResultConfirm.resultTvNext.setClickable(false);

        binding.communicationBtnTest.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);

        if (testFlag == 1){
            // 工厂测试版本，直接进行录音
            binding.communicationToolbarTitle.setTitle(getString(R.string.mic_test));
//            binding.communicationBtnTest.setText(getString(R.string.start_mic_test));
//            binding.communicationBtnTest.setIconResource("\uf130");
            micCheck();
            binding.communicationBtnTest.setIconResource("\uf028");
            binding.communicationBtnTest.setText(getResources().getString(R.string.recording));
            binding.communicationBtnTest.setClickable(false);
        } else {
            binding.communicationBtnTest.callOnClick();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtil.closeAudio();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                Log.i(TAG, "onActivityResult: 0");
                binding.communicationLlEnter.setVisibility(View.GONE);
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                binding.layoutResultConfirm.resultTvQuestion.setText(R.string.wifi_test_confirm);
                break;
            case 1:
                Log.i(TAG, "onActivityResult:  1");
                binding.communicationLlEnter.setVisibility(View.GONE);
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                binding.layoutResultConfirm.resultTvQuestion.setText(R.string.gps_test_confirm);
                break;
            case 2:
                Log.i(TAG, "onActivityResult:  2");
                binding.communicationLlEnter.setVisibility(View.GONE);
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
                binding.layoutResultConfirm.resultTvQuestion.setText(R.string.bluetooth_test_confirm);
                break;
            default:
                break;
        }
    }

    private CommunicateHandler mHandler = new CommunicateHandler(this);

    @Override
    public void onClick(@NonNull View view) {
        if (view.equals(binding.communicationBtnTest)) {
            Log.i(TAG, "onViewClicked, testFlag >>>>>> " + testFlag);
            if (testFlag == 0) {
                try {
                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.layoutResultConfirm.resultTvNext.callOnClick();
                }
            } else if (testFlag == 1) {
                micCheck();
                binding.communicationBtnTest.setIconResource("\uf028");
                binding.communicationBtnTest.setText(getResources().getString(R.string.recording));
                binding.communicationBtnTest.setClickable(false);
            } else if (testFlag == bluetoothFlag) {
                try {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivityForResult(intent, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.layoutResultConfirm.resultTvNext.callOnClick();
                }
            }
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
            if (testFlag == 0) {
                mSpUtils.saveWifiCheckResult(checkResult);
                Log.i(TAG, "WifiCheckResult: " + mSpUtils.getWifiCheckResult());
//                    binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                resetCheck();
//                    binding.communicationLlEnter.setVisibility(View.VISIBLE);
//                    binding.communicationLlEnter.setBackgroundColor(ContextCompat.getColor(this, R.color.mic_test_color));
//                    binding.communicationBtnTest.setText(getResources().getString(R.string.start_mic_test));
//                    binding.communicationBtnTest.setIconResource("\uf028");
//                    binding.communicationToolbarTitle.setTitle(R.string.mic_test);
//                    testFlag = 1;
//                    binding.communicationLlEnter.setBackgroundColor(ContextCompat.getColor(this, R.color.sd_test_color));
//                    binding.communicationBtnTest.setText(getResources().getString(R.string.start_bluetooth_test));
//                    binding.communicationBtnTest.setIconResource("\uf028");
                binding.communicationToolbarTitle.setTitle(R.string.bluetooth_test);
                testFlag = 3;
                binding.communicationBtnTest.callOnClick();
            } else if (testFlag == 1) {
                mSpUtils.saveMicCheckResult(checkResult);
                Log.i(TAG, "MicCheckResult: " + mSpUtils.getMicCheckResult());
                binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.GONE);
                resetCheck();
//                    binding.communicationLlEnter.setVisibility(View.VISIBLE);
//                    binding.communicationLlEnter.setBackgroundColor(ContextCompat.getColor(this, R.color.sd_test_color));
//                    binding.communicationBtnTest.setText(getResources().getString(R.string.start_bluetooth_test));
//                    binding.communicationBtnTest.setIconResource("\uf028");
//                    binding.communicationToolbarTitle.setTitle(R.string.bluetooth_test);
//                    testFlag = 3;
                Intent intent = new Intent(CommunicationTestActivity.this, LedTestActivity.class);
//                    intent.putExtra("testFlag", 0);
                startActivity(intent);
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
            } else if (testFlag == bluetoothFlag) {
                mSpUtils.saveBluetoothCheckResult(checkResult);
                Log.i(TAG, "BluetoothCheckResult: " + mSpUtils.getBluetoothCheckResult());
//                    startActivity(new Intent(CommunicationTestActivity.this, IDReadActivity.class));
                startActivity(new Intent(CommunicationTestActivity.this, BasicTestActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
            }
        }
    }

    private static class CommunicateHandler extends Handler {
        private WeakReference<CommunicationTestActivity> mWeakReference;

        CommunicateHandler(CommunicationTestActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CommunicationTestActivity activity = mWeakReference.get();
            activity.binding.communicationLlEnter.setVisibility(View.GONE);
            activity.binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
            activity.binding.layoutResultConfirm.resultTvQuestion.setText(activity.getString(R.string.mic_test_confirm));
            activity.binding.communicationLlEnter.setClickable(true);
            activity.binding.communicationBtnTest.setClickable(true);
        }
    }

    /***
     * 在每一项测试结果确认后，重置结果确认界面，以准备下一次结果确认
     */
    private void resetCheck() {
        binding.layoutResultConfirm.resultTvNext.setClickable(false);
        binding.layoutResultConfirm.resultImgOk.setImageResource(R.drawable.check_ok_unselected);
        binding.layoutResultConfirm.resultImgCross.setImageResource(R.drawable.check_cross_unselected);
    }

    /**
     * 录音
     */
    private void micCheck() {
        final MediaRecorder mediaRecorder = new MediaRecorder();
        File audioFile;
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //第2步：设置音频输出格式（默认的输出格式）
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //第3步：设置音频编码方式（默认的编码方式）
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //创建一个临时的音频输出文件
        try {
            audioFile = new File(Environment.getExternalStorageDirectory() + "/recordtest" + ".amr");
            //第4步：指定音频输出文件
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            //第5步：调用prepare方法
            mediaRecorder.prepare();
            //第6步：调用start方法开始录音
            mediaRecorder.start();
            mUtil.getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    mediaRecorder.stop();
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource("/storage/emulated/0/recordtest.amr");
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IllegalArgumentException e) {
                        Log.e("错误1", e.toString());
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        Log.e("错误2", e.toString());
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        Log.e("错误3", e.toString());
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e("错误4", e.toString());
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(0);
                    File file = new File("/storage/emulated/0/recordtest.amr");
                    if (file.exists()){
                        boolean delete = file.delete();
                        Log.e(TAG, "micCheck delete temp amr file:" + delete);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }
}
