package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityGpsTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author huang
 */
public class GpsTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = GpsTestActivity.class.getSimpleName();

    private int checkResult;
    private SpUtils mSpUtils;
    private com.handheld.huang.handsettest.databinding.ActivityGpsTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGpsTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.gpsTvFinish.setClickable(false);
        mSpUtils = new SpUtils(this);

        binding.gpsImgOk.setOnClickListener(this);
        binding.gpsImgCross.setOnClickListener(this);
        binding.gpsTvFinish.setOnClickListener(this);
        binding.gpsBtnTest.setOnClickListener(this);

        binding.gpsBtnTest.callOnClick();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.equals(binding.gpsImgOk)) {
            binding.gpsImgOk.setImageResource(R.drawable.check_ok_selected);
            binding.gpsImgCross.setImageResource(R.drawable.check_cross_unselected);
            checkResult = 0;
            binding.gpsTvFinish.setClickable(true);
        } else if (view.equals(binding.gpsImgCross)) {
            binding.gpsImgCross.setImageResource(R.drawable.check_cross_selected);
            binding.gpsImgOk.setImageResource(R.drawable.check_ok_unselected);
            checkResult = 1;
            binding.gpsTvFinish.setClickable(true);
        } else if (view.equals(binding.gpsTvFinish)) {
            mSpUtils.saveGpsCheckResult(checkResult);
            Log.i(TAG, "GpsCheckResult: " + mSpUtils.getGpsCheckResult());
            finish();
        } else if (view.equals(binding.gpsBtnTest)) {
            PackageManager packageManager = getPackageManager();
            try {
                packageManager.getPackageInfo("com.chartcross.gpstestplus", 0);
                // gpstestplus应用已安装
                ComponentName localComponentName = new ComponentName(
                        "com.chartcross.gpstestplus",
                        "com.chartcross.gpstestplus.GPSTestPlus");
                Intent localIntent = new Intent();
                localIntent.setComponent(localComponentName);
                startActivity(localIntent);
            } catch (PackageManager.NameNotFoundException e) {
                // gpstestplus应用未安装
                ComponentName localComponentName = new ComponentName(
                        "com.chartcross.gpstest",
                        "com.chartcross.gpstest.MainActivity");
                Intent localIntent = new Intent();
                localIntent.setComponent(localComponentName);
                startActivity(localIntent);
            }
            binding.gpsLlEnter.setVisibility(View.GONE);
            binding.gpsLlConfirm.setVisibility(View.VISIBLE);
        }

    }
}
