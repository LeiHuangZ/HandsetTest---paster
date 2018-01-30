package com.handheld.huang.handsettest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author huang
 * @date $date$ $time$
 * @Describe $desc$
 */

public class EmBootupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("bootup", "onReceive: ");
        }
    }
}
