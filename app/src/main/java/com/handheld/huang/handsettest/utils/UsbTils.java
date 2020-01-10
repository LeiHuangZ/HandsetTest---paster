package com.handheld.huang.handsettest.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * description : DESCRIPTION
 * update : 2019/8/15 11:04,LeiHuang,DESCRIPTION
 *
 * @author : LeiHuang
 * @version : VERSION
 * @date : 2019/8/15 11:04
 */
public class UsbTils {

    private static final String TAG = UsbTils.class.getSimpleName();

    public static boolean getGpioState(int gpio) {
        boolean isHigh = false;
        try {
            Process process = Runtime.getRuntime().exec("cat  /sys/bus/platform/drivers/mediatek-pinctrl/1000b000.pinctrl/mt_gpio");
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String gpioStr = getGpioStr(gpio);
            Log.e(TAG, "getGpioState, gpioStr:" + gpioStr);
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                s = s.replace("-", "");
                if (s.startsWith(gpioStr)) {
                    s = s.substring(s.indexOf(gpioStr), s.indexOf(gpioStr) + 15);
                    Log.e(TAG, "getGpioState, s:" + s);
                    isHigh = s.charAt(8) == '1';
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isHigh;
    }

    /**
     * Returns the GPIO flag value, greater than or equal to 100, directly returns its String value, and others return one or two Spaces plus GPIO's String value
     *
     * @param gpio Int value of gpio port to be processed
     * @return Gpio String flag value, read the status of the flag basis
     */
    private static String getGpioStr(int gpio) {
        int oneb = 9;
        int twob = 99;
        String s;
        if (gpio <= oneb) {
            s = "   " + gpio;
        } else if (gpio <= twob) {
            s = "  " + gpio;
        } else {
            s = " " + String.valueOf(gpio);
        }
        return s;
    }
}
