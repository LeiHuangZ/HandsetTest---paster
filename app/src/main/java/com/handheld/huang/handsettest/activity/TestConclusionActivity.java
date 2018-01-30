package com.handheld.huang.handsettest.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.adapter.MyAdapter;
import com.handheld.huang.handsettest.adapter.Result;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.Util;

import java.io.File;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author huang
 */
public class TestConclusionActivity extends AppCompatActivity {
    private static String TAG = TestConclusionActivity.class.getSimpleName();

    @BindView(R.id.test_conclusion_lrcv)
    RecyclerView mTestConclusionLrcv;
    private MaterialDialog mDialog;
    private Util mUtil;
    private SpUtils mSpUtils;
    private List<Result> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_conclusion);
        ButterKnife.bind(this);

        mDialog = new MaterialDialog.Builder(this)
                .title("请稍候")
                .content("正在生成结果，请稍候....")
                .progress(true, 100)
                .cancelable(false)
                .show();

        FancyButton facebookLoginBtn = new FancyButton(this);
        facebookLoginBtn.setText("Login with Facebook");
        facebookLoginBtn.setBackgroundColor(Color.parseColor("#3b5998"));
        facebookLoginBtn.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        facebookLoginBtn.setTextSize(17);
        facebookLoginBtn.setRadius(5);
        facebookLoginBtn.setIconResource("\uf082");
        facebookLoginBtn.setIconPosition(FancyButton.POSITION_LEFT);
        facebookLoginBtn.setFontIconSize(30);

        mUtil = new Util(this);
        mSpUtils = new SpUtils(this);

        initData();
    }

    private void initData() {
        mUtil.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                Result result = new Result();
                result.setName(getResources().getString(R.string.display_test));
                result.setIsCheck(mSpUtils.getDisplayCheckResult());
                mList.add(result);
                mList.add(new Result(getResources().getString(R.string.touch_test), mSpUtils.getTouchCheckResult()));
                mList.add(new Result(getResources().getString(R.string.led_test), mSpUtils.getLedCheckResult()));
                mList.add(new Result(getResources().getString(R.string.key_test), mSpUtils.getKeyCheckResult()));
                mList.add(new Result(getResources().getString(R.string.camera_test), mSpUtils.getCameraCheckResult()));
                mList.add(new Result(getResources().getString(R.string.call_test), mSpUtils.getCallCheckResult()));
                mList.add(new Result(getResources().getString(R.string.sd_test), mSpUtils.getSdCheckResult()));
                mList.add(new Result(getResources().getString(R.string.speaker_test), mSpUtils.getSpeakerCheckResult()));
                mList.add(new Result(getResources().getString(R.string.wifi_test), mSpUtils.getWifiCheckResult()));
                mList.add(new Result(getResources().getString(R.string.imei_test), mSpUtils.getImeiCheckResult()));
                mList.add(new Result(getResources().getString(R.string.mac_test), mSpUtils.getMacCheckResult()));
                mList.add(new Result(getResources().getString(R.string.board_test), mSpUtils.getBoardCheckResult()));
                mList.add(new Result(getResources().getString(R.string.mic_test), mSpUtils.getMicCheckResult()));
                mList.add(new Result(getResources().getString(R.string.bluetooth_test), mSpUtils.getBluetoothCheckResult()));
                mList.add(new Result(getResources().getString(R.string.gps_test), mSpUtils.getGpsCheckResult()));
                mList.add(new Result(getResources().getString(R.string.indicator), mSpUtils.getIndocatorCheckResult()));
                mList.add(new Result(getResources().getString(R.string.onElectricity_test), mSpUtils.getOnElectricityCheckResult()));
                mList.add(new Result(getResources().getString(R.string.offElectricity_test), mSpUtils.getOffElectricityCheckResult()));

                try {
                    Thread.sleep(1000);
                    mHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ConclusionHandler mHandler = new ConclusionHandler(this);

    @OnClick(R.id.btn_spotify)
    public void onViewClicked() {
        mUtil.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String savePath = Environment.getExternalStorageDirectory().getPath() + "/Ringtones/";
                    File folder = new File(savePath);
                    //如果文件夹不存在则创建
                    if (!folder.exists())
                    {
                        boolean makeDir = folder.mkdir();
                        Log.i(TAG, "saveTestConclusion, makeDir >>>>>>> " + makeDir);
                    }
                    FileWriter fw = new FileWriter(savePath + "/zsb.txt");
                    String str;

                    str = getResources().getString(R.string.display_test) + "：   ";
                    if (mSpUtils.getDisplayCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.touch_test) + "：   ");
                    if (mSpUtils.getTouchCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.led_test) + "： ");
                    if (mSpUtils.getLedCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.key_test) + "：   ");
                    if (mSpUtils.getKeyCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.camera_test) + "：   ");
                    if (mSpUtils.getCameraCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.call_test) + "：   ");
                    if (mSpUtils.getCallCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.sd_test) + "：  ");
                    if (mSpUtils.getSdCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.speaker_test) + "：   ");
                    if (mSpUtils.getSpeakerCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.wifi_test) + "：  ");
                    if (mSpUtils.getWifiCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.imei_test) + "：  ");
                    if (mSpUtils.getImeiCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.board_test) + "：  ");
                    if (mSpUtils.getBoardCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.mic_test) + "：   ");
                    if (mSpUtils.getMicCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.mac_test) + "：   ");
                    if (mSpUtils.getMacCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.gps_test) + "：   ");
                    if (mSpUtils.getGpsCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.bluetooth_test) + "：   ");
                    if (mSpUtils.getBluetoothCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
//                    str = str.concat(getResources().getString(R.string.indicator) + "： ");
//                    if (mSpUtils.getIndocatorCheckResult() == 0) {
//                        str = str.concat("通过\n");
//                    }else {
//                        str = str.concat("未通过\n");
//                    }
                    str = str.concat(getResources().getString(R.string.onElectricity_test) + "： ");
                    if (mSpUtils.getOnElectricityCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat(getResources().getString(R.string.offElectricity_test) + "：  ");
                    if (mSpUtils.getOffElectricityCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    fw.flush();
                    fw.write(str);
                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        finish();
    }

    private static class ConclusionHandler extends Handler {
        private WeakReference<TestConclusionActivity> mWeakReference;
        private final TestConclusionActivity mActivity;


        ConclusionHandler(TestConclusionActivity activity) {
            mWeakReference = new WeakReference<>(activity);
            mActivity = mWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MyAdapter adapter = new MyAdapter(mActivity.mList, mActivity);
            mActivity.mTestConclusionLrcv.setAdapter(adapter);
            mActivity.mTestConclusionLrcv.setLayoutManager(new GridLayoutManager(mActivity, 2));
            mActivity.mDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
