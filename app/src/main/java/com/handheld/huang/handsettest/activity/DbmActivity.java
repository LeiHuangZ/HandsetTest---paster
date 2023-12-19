package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityDbmBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author HuangLei 1252065297@qq.com
 * <code>
 * Create At 2019/5/7 9:48
 * Update By 更新者
 * Update At 2019/5/7 9:48
 * </code>
 * 获取手机移动数据网络信号强度值
 */
public class DbmActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = DbmActivity.class.getSimpleName();

    /**
     * 检查结果，默认为0  0 --> 通过，1 --> 不通过
     */
    int checkResult = 0;
    private SpUtils mSpUtils;
    private int dbm = 0;

    private WifiManager mWifiManager;

    private TelephonyManager mTelephonyManager;

    private PhoneStateListenerImpl mListener = new PhoneStateListenerImpl();
    private com.handheld.huang.handsettest.databinding.ActivityDbmBinding binding;

    @Override
    public void onClick(@NonNull View view) {
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
            mSpUtils.saveDbmCheckResult(checkResult);
            Log.i(TAG, "DbmCheckResult: " + mSpUtils.getLedCheckResult());
            Intent intent = new Intent(DbmActivity.this, KeyTestActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }
    }

    /**
     * 通过监听回调，得到当前的手机蜂窝网络信号强度
     */
    public class PhoneStateListenerImpl extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            try {
                Method getDbm = signalStrength.getClass().getMethod("getDbm");
                dbm = (int) getDbm.invoke(signalStrength);
                Log.e("Huang, DbmActivity", "invoke dbm:" + dbm);
                mSpUtils.saveGsmDbm(dbm);
                binding.dbmTv.setText("移动网络：" + dbm + "dBm\n\n");
                int rssi = mWifiManager.getConnectionInfo().getRssi();
                if (rssi != -127) {
                    binding.dbmTv.append("WiFi：" + rssi + "dBm");
                } else {
                    binding.dbmTv.append("WiFi：未连接");
                }
                mSpUtils.saveWiFiDbm(rssi);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDbmBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mSpUtils = new SpUtils(this);
        if (binding.layoutResultConfirm.resultLlConfirm.getVisibility() == View.GONE) {
            binding.layoutResultConfirm.resultTvNext.setClickable(false);
            binding.layoutResultConfirm.resultTvQuestion.setText("确认网络信号强度测试结果");
            binding.layoutResultConfirm.resultLlConfirm.setVisibility(View.VISIBLE);
        }

        int rssi = mWifiManager.getConnectionInfo().getRssi();
        if (rssi != -127) {
            binding.dbmTv.append("WiFi：" + rssi + "dBm");
        } else {
            binding.dbmTv.append("WiFi：未连接");
        }

        binding.layoutResultConfirm.resultImgOk.setOnClickListener(this);
        binding.layoutResultConfirm.resultImgCross.setOnClickListener(this);
        binding.layoutResultConfirm.resultTvNext.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // 实现空，使得返回键无法返回
    }

}
