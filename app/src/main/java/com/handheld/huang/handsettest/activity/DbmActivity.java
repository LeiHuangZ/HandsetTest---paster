package com.handheld.huang.handsettest.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author HuangLei 1252065297@qq.com
 * <code>
 * Create At 2019/5/7 9:48
 * Update By 更新者
 * Update At 2019/5/7 9:48
 * </code>
 * 获取手机移动数据网络信号强度值
 */
public class DbmActivity extends AppCompatActivity {
    private static String TAG = DbmActivity.class.getSimpleName();

    @BindView(R.id.dbm_tv)
    TextView mDbmTv;
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
    private SpUtils mSpUtils;
    private int dbm = 0;

    private WifiManager mWifiManager;

    private TelephonyManager mTelephonyManager;

    private PhoneStateListenerImpl mListener = new PhoneStateListenerImpl();

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
                mSpUtils.saveDbmCheckResult(checkResult);
                Log.i(TAG, "DbmCheckResult: " + mSpUtils.getLedCheckResult());
                Intent intent = new Intent(DbmActivity.this, KeyTestActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.activity_start_rigth,0);
                finish();
                break;
            default:
                break;
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
                mDbmTv.setText("移动网络：" + dbm + "dBm\n\n");
                int rssi = mWifiManager.getConnectionInfo().getRssi();
                if (rssi != -127) {
                    mDbmTv.append("WiFi：" + rssi + "dBm");
                } else {
                    mDbmTv.append("WiFi：未连接");
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
        setContentView(R.layout.activity_dbm);
        ButterKnife.bind(this);

        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mSpUtils = new SpUtils(this);
        if (mResultLlConfirm.getVisibility() == View.GONE){
            mResultTvNext.setClickable(false);
            mResultTvQuestion.setText("确认网络信号强度测试结果");
            mResultLlConfirm.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onBackPressed() {
        // 实现空，使得返回键无法返回
    }

}
