package com.handheld.huang.handsettest.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.CodeUtil;
import com.handheld.huang.handsettest.utils.MobileInfoUtil;
import com.handheld.huang.handsettest.utils.SpUtils;
import com.handheld.huang.handsettest.utils.Util;

import java.lang.ref.WeakReference;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author huang
 */
public class MacTestActivity extends AppCompatActivity {
    private static String TAG = MacTestActivity.class.getSimpleName();

    @BindView(R.id.mac_img_show)
    ImageView mMacImgShow;
    @BindView(R.id.result_img_ok)
    ImageView mResultImgOk;
    @BindView(R.id.result_img_cross)
    ImageView mResultImgCross;
    @BindView(R.id.result_tv_next)
    TextView mResultTvNext;
    @BindView(R.id.imei_img_show)
    ImageView mImeiImgShow;
    @BindView(R.id.mac_tv_result)
    TextView mMacTvResult;

    int checkResult;
    @BindView(R.id.imei_tv_result)
    TextView mImeiTvResult;
    @BindView(R.id.board_tv_result)
    TextView mBoardTvResult;
    @BindView(R.id.flash_tv_result)
    TextView mFlashTvResult;
    private Util mUtil;
    private Toast mToast;
    private SpUtils mSpUtils;
    String originMac = "20:08:ed:05:03:65";
    String originMac2 = "02:00:00:00:00:00";
    String originImei = "860527010207000";
    private int clickCount = 0;
    private long lastClickTime;
    /**
     * 0 --> Mac, 1 --> Imei, 2 --> Board, 3 ---> FlashNumber
     */
    private int saveFlag = 0;
    private final int flagMacSuccess = 10010;
    private final int flagMacFail = 10011;
    private final int flagImeiSuccess = 10020;
    private final int flagImeiFail = 10021;
    private final int flagBoardSuccess = 10030;
    private final int flagBoardFail = 10031;
    private final int flagFlashSuccess = 10040;
    private final int flagFlashFail = 10041;

    private TimerTask mTask = new TimerTask() {
        @Override
        public void run() {
            try {
                int maxLoopCount = 7;
                int loopCount = 0;
                while (mWifiManager == null || !mWifiManager.isWifiEnabled()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    loopCount++;
                    if (loopCount > maxLoopCount) {
                        break;
                    }
                }
                String mac = MobileInfoUtil.getMacAddr();
                Log.i(TAG, "mac: " + mac);
                if (TextUtils.isEmpty(mac) || mac.startsWith("00") || originMac.equals(mac) || originMac2.equals(mac)) {
                    mHandler.sendEmptyMessage(flagMacFail);
                } else {
                    String url = mac.replaceAll(":", "");
                    Bitmap qrImage = CodeUtil.creatBarcode(MacTestActivity.this, url, 800, 160, new PointF(0, 140), true);
                    Message message = new Message();
                    message.obj = qrImage;
                    message.what = flagMacSuccess;
                    mHandler.sendMessage(message);
                }
                Thread.sleep(100);

                String imei = MobileInfoUtil.getIMEI(MacTestActivity.this);
                Log.i(TAG, "imei: " + imei);
                if (imei == null || originImei.equals(imei) || "".equals(imei)) {
                    mHandler.sendEmptyMessage(flagImeiFail);
                } else {
                    Bitmap qrImage;
                    qrImage = CodeUtil.creatBarcode(MacTestActivity.this, imei, 800, 160, new PointF(0, 140), true);
                    Message message = new Message();
                    message.obj = qrImage;
                    message.what = flagImeiSuccess;
                    mHandler.sendMessage(message);
                }
                Thread.sleep(100);

                // 展锐7885平台，暂无方法判断是否校验
                if (!"uis7885_2h10".equals(Build.HARDWARE) && !"qcom".equals(Build.HARDWARE)) {
                    String sn;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        sn = MobileInfoUtil.get("vendor.gsm.serial");
                    } else {
                        sn = MobileInfoUtil.get("gsm.serial");
                    }
                    Log.e(TAG, "gsm.barcode: " + sn);
                    if ("".equals(sn) || sn == null || sn.length() < 61) {
                        mHandler.sendEmptyMessage(flagBoardFail);
                    } else {
                        StringBuilder s = new StringBuilder();
                        s.append(sn.charAt(60));
                        s.append(sn.charAt(61));
                        Log.i(TAG, "run, calibration >>>>>> " + s);
                        String calibration = s.toString();
                        if ("10".equals(calibration)) {
                            mHandler.sendEmptyMessage(flagBoardSuccess);
                        } else {
                            mHandler.sendEmptyMessage(flagBoardFail);
                        }
                    }
                }
                Thread.sleep(100);

                String flashnum;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flashnum = Build.getSerial();
                } else {
                    flashnum = Build.SERIAL;
                }
                if (TextUtils.isEmpty(flashnum) || "0123456789ABCDEF".equals(flashnum)) {
                    mHandler.sendEmptyMessage(flagFlashFail);
                } else {
                    Message message = new Message();
                    message.what = flagFlashSuccess;
                    message.obj = flashnum;
                    mHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(flagFlashFail);
            }
        }
    };

    private MyHandler mHandler = new MyHandler(MacTestActivity.this);
    private WifiManager mWifiManager;
    private ProgressDialog mProgressDialog;

    private static class MyHandler extends Handler {
        private WeakReference<MacTestActivity> mWeakReference;

        MyHandler(MacTestActivity macTestActivity) {
            mWeakReference = new WeakReference<>(macTestActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MacTestActivity macTestActivity = mWeakReference.get();
            int what = msg.what;
            if (what == macTestActivity.flagMacFail) {
                macTestActivity.showToast("原始MAC地址，MAC测试不通过！");
                macTestActivity.mUtil.playAudio(2);
                Spannable span = new SpannableString("FAIL！");
                span.setSpan(new AbsoluteSizeSpan(70), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                macTestActivity.mMacTvResult.append(span);
                mWeakReference.get().saveFlag = 0;
                macTestActivity.onViewClicked(macTestActivity.mResultImgCross);
                macTestActivity.onViewClicked(macTestActivity.mResultTvNext);
            } else if (what == macTestActivity.flagMacSuccess) {
                Bitmap image = (Bitmap) msg.obj;
                macTestActivity.showToast("MAC检测通过！");
                Spannable span = new SpannableString("PASS！");
                span.setSpan(new AbsoluteSizeSpan(70), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                macTestActivity.mBoardTvResult.append(Html.fromHtml("主板校准：<h1><font color='#6A8759'>PASS!</font></h1>"));
                macTestActivity.mMacTvResult.append(span);
                macTestActivity.mMacImgShow.setVisibility(View.VISIBLE);
                macTestActivity.mMacImgShow.setImageBitmap(image);
                mWeakReference.get().saveFlag = 0;
                macTestActivity.onViewClicked(macTestActivity.mResultImgOk);
                macTestActivity.onViewClicked(macTestActivity.mResultTvNext);
            } else if (what == macTestActivity.flagImeiSuccess) {
                Bitmap image = (Bitmap) msg.obj;
                macTestActivity.showToast("IMEI检测通过！");
                macTestActivity.mImeiImgShow.setVisibility(View.VISIBLE);
                macTestActivity.mImeiImgShow.setImageBitmap(image);
                Spannable span = new SpannableString("PASS！");
                span.setSpan(new AbsoluteSizeSpan(70), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                macTestActivity.mBoardTvResult.append(Html.fromHtml("主板校准：<h1><font color='#6A8759'>PASS!</font></h1>"));
                macTestActivity.mImeiTvResult.append(span);
                mWeakReference.get().saveFlag = 1;
                macTestActivity.onViewClicked(macTestActivity.mResultImgOk);
                macTestActivity.onViewClicked(macTestActivity.mResultTvNext);
            } else if (what == macTestActivity.flagImeiFail) {
                macTestActivity.showToast("原始IMEI地址，IMEI测试不通过！");
                macTestActivity.mUtil.playAudio(2);
                Spannable span = new SpannableString("FAIL！");
                span.setSpan(new AbsoluteSizeSpan(70), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                macTestActivity.mImeiTvResult.append(span);
                mWeakReference.get().saveFlag = 1;
                macTestActivity.onViewClicked(macTestActivity.mResultImgCross);
                macTestActivity.onViewClicked(macTestActivity.mResultTvNext);
            } else if (what == macTestActivity.flagBoardSuccess) {
                macTestActivity.showToast("机器主板已校准");
                Spannable span = new SpannableString("PASS！");
                span.setSpan(new AbsoluteSizeSpan(70), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                macTestActivity.mBoardTvResult.append(Html.fromHtml("主板校准：<h1><font color='#6A8759'>PASS!</font></h1>"));
                macTestActivity.mBoardTvResult.append(span);
                mWeakReference.get().saveFlag = 2;
                macTestActivity.onViewClicked(macTestActivity.mResultImgOk);
                macTestActivity.mResultImgOk.setVisibility(View.INVISIBLE);
                macTestActivity.mResultImgCross.setVisibility(View.INVISIBLE);
                macTestActivity.onViewClicked(macTestActivity.mResultTvNext);
            } else if (what == macTestActivity.flagBoardFail) {
                macTestActivity.showToast("机器主板未校准！");
                macTestActivity.mUtil.playAudio(2);
                Spannable span = new SpannableString("FAIL！");
                span.setSpan(new AbsoluteSizeSpan(70), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                macTestActivity.mBoardTvResult.append(span);
                mWeakReference.get().saveFlag = 2;
                macTestActivity.onViewClicked(macTestActivity.mResultImgCross);
                macTestActivity.mResultImgOk.setVisibility(View.INVISIBLE);
                macTestActivity.mResultImgCross.setVisibility(View.INVISIBLE);
                macTestActivity.onViewClicked(macTestActivity.mResultTvNext);
            } else if (what == macTestActivity.flagFlashFail) {
                macTestActivity.showToast("未获取到Flash序列号！");
                macTestActivity.mUtil.playAudio(2);
                Spannable span = new SpannableString("无序列号！");
                span.setSpan(new AbsoluteSizeSpan(70), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                macTestActivity.mFlashTvResult.append(span);
                mWeakReference.get().saveFlag = 3;
                macTestActivity.onViewClicked(macTestActivity.mResultImgCross);
                macTestActivity.mResultImgOk.setVisibility(View.INVISIBLE);
                macTestActivity.mResultImgCross.setVisibility(View.INVISIBLE);
                macTestActivity.mProgressDialog.dismiss();
            } else if (what == macTestActivity.flagFlashSuccess) {
                CharSequence flashNum = (CharSequence) msg.obj;
                Spannable span = new SpannableString(flashNum);
                span.setSpan(new AbsoluteSizeSpan(70), 0, flashNum.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.GREEN), 0, flashNum.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                macTestActivity.mBoardTvResult.append(Html.fromHtml("主板校准：<h1><font color='#6A8759'>PASS!</font></h1>"));
                macTestActivity.mFlashTvResult.append(span);
                mWeakReference.get().saveFlag = 3;
                macTestActivity.onViewClicked(macTestActivity.mResultImgOk);
                macTestActivity.mResultImgOk.setVisibility(View.INVISIBLE);
                macTestActivity.mResultImgCross.setVisibility(View.INVISIBLE);
                macTestActivity.mProgressDialog.dismiss();
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mac_test);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(MacTestActivity.this);
        mProgressDialog.setMessage("正在进行设备校准状态检测.....");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mUtil = new Util(this);
        mUtil.initAudio();
        mSpUtils = new SpUtils(this);

        //打开WiFi
        mWifiManager = (WifiManager) MacTestActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null && !mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        ExecutorService executorService = mUtil.getExecutorService();
        executorService.execute(mTask);

        mMacImgShow.setOnClickListener(v -> {
            if (clickCount > 0) {
                long clickTime = SystemClock.elapsedRealtime();
                if (clickTime - lastClickTime < 1000){
                    clickCount++;
                } else {
                    clickCount = 0;
                }
            } else {
                lastClickTime = SystemClock.elapsedRealtime();
                clickCount++;
            }
            if (clickCount >= 5) {
                Intent intent = new Intent(MacTestActivity.this, DeviceInfoActivity.class);
                startActivity(intent);
                MacTestActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtil.closeAudio();
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
                if (saveFlag == 0) {
                    mSpUtils.saveMacCheckResult(checkResult);
                    Log.i(TAG, "MacCheckResult: " + mSpUtils.getMacCheckResult());
                } else if (saveFlag == 1) {
                    mSpUtils.saveImeiCheckResult(checkResult);
                } else if (saveFlag == 2) {
                    mSpUtils.saveBoardCheckResult(checkResult);
                } else if (saveFlag == 3) {
                    mSpUtils.saveFlashCheckResult(checkResult);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 展示Toast
     *
     * @param data 需要展示的数据
     */
    private void showToast(String data) {
        if (mToast == null) {
            mToast = Toast.makeText(MacTestActivity.this, data, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        } else {
            mToast.setText(data);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
