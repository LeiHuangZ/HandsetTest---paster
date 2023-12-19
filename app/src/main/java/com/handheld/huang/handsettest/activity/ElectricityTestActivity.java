package com.handheld.huang.handsettest.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityElectricityTestBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

/**
 * @author huang
 */
public class ElectricityTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = ElectricityTestActivity.class.getSimpleName();

    private int onCheckResult;
    private int offCheckResult;
    private SpUtils mSpUtils;
    private com.handheld.huang.handsettest.databinding.ActivityElectricityTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityElectricityTestBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        mSpUtils = new SpUtils(this);
        binding.resultTvNext.setClickable(false);

        binding.onElectricityImgOk.setOnClickListener(this);
        binding.onElectricityImgCross.setOnClickListener(this);
        binding.offElectricityImgOk.setOnClickListener(this);
        binding.offElectricityImgCross.setOnClickListener(this);
        binding.resultTvNext.setOnClickListener(this);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.equals(binding.onElectricityImgOk)) {
            binding.onElectricityImgOk.setImageResource(R.drawable.check_ok_selected);
            binding.onElectricityImgCross.setImageResource(R.drawable.check_cross_unselected);
            onCheckResult = 0;
            binding.resultTvNext.setClickable(true);
        } else if (view.equals(binding.onElectricityImgCross)) {
            binding.onElectricityImgCross.setImageResource(R.drawable.check_cross_selected);
            binding.onElectricityImgOk.setImageResource(R.drawable.check_ok_unselected);
            onCheckResult = 1;
            binding.resultTvNext.setClickable(true);
        } else if (view.equals(binding.offElectricityImgOk)) {
            binding.offElectricityImgOk.setImageResource(R.drawable.check_ok_selected);
            binding.offElectricityImgCross.setImageResource(R.drawable.check_cross_unselected);
            offCheckResult = 0;
            binding.resultTvNext.setClickable(true);
        } else if (view.equals(binding.offElectricityImgCross)) {
            binding.offElectricityImgCross.setImageResource(R.drawable.check_cross_selected);
            binding.offElectricityImgOk.setImageResource(R.drawable.check_ok_unselected);
            offCheckResult = 1;
            binding.resultTvNext.setClickable(true);
        } else if (view.equals(binding.resultTvNext)) {
            mSpUtils.saveOnElectricityCheckResult(onCheckResult);
            mSpUtils.saveOffElectricityCheckResult(offCheckResult);
            Log.i(TAG, "OnElectricityCheckResult: " + mSpUtils.getOnElectricityCheckResult() + "---------" + "OffElectricityCheckResult:  " + mSpUtils.getOffElectricityCheckResult());
            startActivity(new Intent(ElectricityTestActivity.this, TestConclusionActivity.class));
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }

    }
}
