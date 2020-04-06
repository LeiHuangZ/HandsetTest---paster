package com.handheld.huang.handsettest.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
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

    @Override
    protected void onDestroy() {
        // 关闭GPS
        Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, false);
        // 关闭WiFi
        WifiManager mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
        // 关闭蓝牙
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "onCreate 不支持蓝牙:");
            return;
        }
        if (bluetoothAdapter.isEnabled()) {
            boolean res = bluetoothAdapter.disable();
            Log.e(TAG, "onCreate :" + res);
        }
        super.onDestroy();
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
//                mList.add(new Result(getResources().getString(R.string.sd_test), mSpUtils.getSdCheckResult()));
//                mList.add(new Result(getResources().getString(R.string.speaker_test), mSpUtils.getSpeakerCheckResult()));
                mList.add(new Result(getResources().getString(R.string.wifi_test), mSpUtils.getWifiCheckResult()));
//                mList.add(new Result(getResources().getString(R.string.imei_test), mSpUtils.getImeiCheckResult()));
//                mList.add(new Result(getResources().getString(R.string.mac_test), mSpUtils.getMacCheckResult()));
//                mList.add(new Result(getResources().getString(R.string.board_test), mSpUtils.getBoardCheckResult()));
                mList.add(new Result(getResources().getString(R.string.mic_test), mSpUtils.getMicCheckResult()));
                mList.add(new Result(getResources().getString(R.string.bluetooth_test), mSpUtils.getBluetoothCheckResult()));
                mList.add(new Result(getResources().getString(R.string.gps_test), mSpUtils.getGpsCheckResult()));
                mList.add(new Result(getResources().getString(R.string.indicator), mSpUtils.getIndocatorCheckResult()));
                mList.add(new Result("Usb测试", mSpUtils.getUsbCheckResult()));
                mList.add(new Result("底座测试", mSpUtils.getChargerCheckResult()));
                mList.add(new Result("网络信号强度测试", mSpUtils.getDbmCheckResult()));
//                mList.add(new Result(getResources().getString(R.string.onElectricity_test), mSpUtils.getOnElectricityCheckResult()));
//                mList.add(new Result(getResources().getString(R.string.offElectricity_test), mSpUtils.getOffElectricityCheckResult()));

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
                    str = "Flash序列号：    ";
                    if (mSpUtils.getFlashCheckResult() == 0){
                        str = str.concat(Build.SERIAL + "\n");
                    }else {
                        str = str.concat("无序列号\n");
                    }

                    str = str.concat(getResources().getString(R.string.display_test) + "：   ");
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
//                    str = str.concat(getResources().getString(R.string.sd_test) + "：  ");
//                    if (mSpUtils.getSdCheckResult() == 0) {
//                        str = str.concat("通过\n");
//                    }else {
//                        str = str.concat("未通过\n");
//                    }
//                    str = str.concat(getResources().getString(R.string.speaker_test) + "：   ");
//                    if (mSpUtils.getSpeakerCheckResult() == 0) {
//                        str = str.concat("通过\n");
//                    }else {
//                        str = str.concat("未通过\n");
//                    }
                    str = str.concat(getResources().getString(R.string.wifi_test) + "：  ");
                    if (mSpUtils.getWifiCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
//                    str = str.concat(getResources().getString(R.string.imei_test) + "：  ");
//                    if (mSpUtils.getImeiCheckResult() == 0) {
//                        str = str.concat("通过\n");
//                    }else {
//                        str = str.concat("未通过\n");
//                    }
//                    str = str.concat(getResources().getString(R.string.board_test) + "：  ");
//                    if (mSpUtils.getBoardCheckResult() == 0) {
//                        str = str.concat("通过\n");
//                    }else {
//                        str = str.concat("未通过\n");
//                    }
                    str = str.concat(getResources().getString(R.string.mic_test) + "：   ");
                    if (mSpUtils.getMicCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
//                    str = str.concat(getResources().getString(R.string.mac_test) + "：   ");
//                    if (mSpUtils.getMacCheckResult() == 0) {
//                        str = str.concat("通过\n");
//                    }else {
//                        str = str.concat("未通过\n");
//                    }
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
                    str = str.concat(getResources().getString(R.string.indicator) + "： ");
                    if (mSpUtils.getIndocatorCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
//                    str = str.concat(getResources().getString(R.string.onElectricity_test) + "： ");
//                    if (mSpUtils.getOnElectricityCheckResult() == 0) {
//                        str = str.concat("通过\n");
//                    }else {
//                        str = str.concat("未通过\n");
//                    }
//                    str = str.concat(getResources().getString(R.string.offElectricity_test) + "：  ");
//                    if (mSpUtils.getOffElectricityCheckResult() == 0) {
//                        str = str.concat("通过\n");
//                    }else {
//                        str = str.concat("未通过\n");
//                    }
                    str = str.concat("Usb测试： ");
                    if (mSpUtils.getUsbCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat("底座测试  ");
                    if (mSpUtils.getChargerCheckResult() == 0) {
                        str = str.concat("通过\n");
                    }else {
                        str = str.concat("未通过\n");
                    }
                    str = str.concat("网络强度测试  ");
                    if (mSpUtils.getDbmCheckResult() == 0) {
                        str = str.concat("通过  ");
                        str = str.concat("信号值:");
                        str = str.concat("移动网络 ");
                        str = str.concat(String.valueOf(mSpUtils.getGsmDbm()));
                        str = str.concat("dBm  ");
                        str = str.concat("WiFi ");
                        str = str.concat(String.valueOf(mSpUtils.getWiFiDbm()));
                        str = str.concat("dBm");
                        str = str.concat("\n");
                    }else {
                        str = str.concat("未通过 ");
                        str = str.concat("信号值:");
                        str = str.concat("移动网络 ");
                        str = str.concat(String.valueOf(mSpUtils.getGsmDbm()));
                        str = str.concat("dBm  ");
                        str = str.concat("WiFi ");
                        str = str.concat(String.valueOf(mSpUtils.getWiFiDbm()));
                        str = str.concat("dBm");
                        str = str.concat("\n");
                    }
                    fw.flush();
                    fw.write(str);
                    fw.close();
                    notifySystemToScan(savePath + "/zsb.txt");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        finish();
    }

    public void notifySystemToScan(String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);

        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        this.getApplication().sendBroadcast(intent);
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
