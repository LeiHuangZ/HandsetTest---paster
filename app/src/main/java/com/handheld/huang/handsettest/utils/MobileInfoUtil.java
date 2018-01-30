package com.handheld.huang.handsettest.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author huang
 * @package com.handheld.huang.handsettest.utils
 * @fileName MobileInfoUtil
 * @email huanglei1252@qq.com
 * @date 2018/1/27  14:15
 * @Describe 获取设备信息的工具类，IMEI号
 */

public class MobileInfoUtil {
    /**
     * 获取手机IMEI
     *
     * @param context 上下文环境
     * @return 返回IMEI号
     */
    public static String getIMEI(Context context) {
        String imei;
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                imei = telephonyManager.getDeviceId(0);
            } else {
                imei = telephonyManager.getDeviceId();
            }
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (
                Exception e)

        {
            e.printStackTrace();
            return "";
        }

    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02x:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    private static Class<?> mClassType = null;
    private static Method mGetMethod = null;
    private static Method mGetIntMethod = null;
    /**
     * 根据键值，利用反射获取属性值 String SystemProperties.get(String key){}
     * @param key 键值
     * @return 返回获取的属性值
     */
    public static String get(String key) {
        init();

        String value = null;

        try {
            value = (String) mGetMethod.invoke(mClassType, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }
    private static int getInt(String key, int def) {
        init();
        int value = def;
        try {
            value = (Integer) mGetIntMethod.invoke(mClassType, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    public static int getSdkVersion() {
        return getInt("ro.build.version.sdk", -1);
    }
    @SuppressLint("PrivateApi")
    private static void init() {
        try {
            if (mClassType == null) {
                mClassType = Class.forName("android.os.SystemProperties");
                mGetMethod = mClassType.getDeclaredMethod("get", String.class);
                mGetIntMethod = mClassType.getDeclaredMethod("getInt", String.class, int.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
