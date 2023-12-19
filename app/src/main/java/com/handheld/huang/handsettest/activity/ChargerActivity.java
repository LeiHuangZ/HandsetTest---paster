package com.handheld.huang.handsettest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.databinding.ActivityChargerBinding;
import com.handheld.huang.handsettest.utils.SpUtils;

public class ChargerActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = ChargerActivity.class.getSimpleName();

    private int onCheckResult;
    private SpUtils mSpUtils;
    private com.handheld.huang.handsettest.databinding.ActivityChargerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChargerBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        mSpUtils = new SpUtils(this);
        binding.resultTvNext.setClickable(false);

        binding.onElectricityImgOk.setOnClickListener(this);
        binding.onElectricityImgCross.setOnClickListener(this);
        binding.resultTvNext.setOnClickListener(this);
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
        } else if (view.equals(binding.resultTvNext)) {
            mSpUtils.saveChargerCheckResult(onCheckResult);
            Log.i(TAG, "getChargerCheckResult: " + mSpUtils.getChargerCheckResult());
            startActivity(new Intent(ChargerActivity.this, FlashlightActivity.class));
            overridePendingTransition(R.animator.activity_start_rigth, 0);
            finish();
        }
    }
}
