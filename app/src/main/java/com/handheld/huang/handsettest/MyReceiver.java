package com.handheld.huang.handsettest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.handheld.huang.handsettest.activity.MainActivity;
import com.handheld.huang.handsettest.activity.PingActivity;

/**
 * @author huang
 * @date $date$ $time$
 * @Describe 接受拨号键99999广播，启动APP  原因：隐藏应用，没有图标，让测试人员测试专用
 */

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = MyReceiver.class.getSimpleName();
    private static final String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";
    /** process *#*#99999#*#*  */
    private final Uri mEmUri = Uri.parse("android_secret_code://99999");
    private final Uri mEmUri2 = Uri.parse("android_secret_code://666");

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null){
            Log.i(TAG, "onReceive, Null action");
            return;
        }
        if (intent.getAction().equals(SECRET_CODE_ACTION)){
            Uri uri = intent.getData();
            Log.i(TAG, "onReceive, getIntent success in if ");
            if (uri.equals(mEmUri)){
                Intent intentEm = new Intent(context, MainActivity.class);
                intentEm.setData(Uri.parse("com.android.example://AuthActivity"));
                intentEm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.i(TAG, "onReceive, Before start Main activity");
                context.startActivity(intentEm);
            } else if (uri.equals(mEmUri2)){
                Intent intentEm = new Intent(context, PingActivity.class);
                intentEm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.i(TAG, "onReceive, Before start Main activity");
                context.startActivity(intentEm);
            }else {
                Log.i(TAG, "onReceive, Not matched URI");
            }
        }else {
            Log.i(TAG, "onReceive, Not SECRET_CODE_ACTION");
        }
    }
}
