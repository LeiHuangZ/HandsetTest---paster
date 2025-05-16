package com.handheld.huang.handsettest.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
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
    private static final String TAG = "MobileInfoUtil";
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

    public static String getMacAddr(Context context) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                || Build.VERSION.SDK_INT == 35) {
            // F1, F2, 获取WiFi的物理地址
            return getWifiFactoryMacAddresses(context);
        }
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!"wlan0".equalsIgnoreCase(nif.getName())) {
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

    /**
     * 获取WiFi的物理地址，如果获取失败则返回"02:00:00:00:00:00"
     *
     * @return WiFi的物理地址，带英文冒号的形式
     */
    public static String getWifiFactoryMacAddresses(Context context) {
        String macAddress = "02:00:00:00:00:00";
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                || Build.VERSION.SDK_INT == 35) {
            WifiManager wifiManager = context.getSystemService(WifiManager.class);
            if (wifiManager != null) {
                try {
                    Method method = WifiManager.class.getMethod("getFactoryMacAddresses");
                    final String[] macAddresses = (String[]) method.invoke(wifiManager);
                    if (macAddresses != null && macAddresses.length > 0) {
                        macAddress = macAddresses[0];
                    } else {
                        Log.e(TAG, "getWifiFactoryMacAddresses: macAddresses is null or empty");
                    }
                } catch (NoSuchMethodException | IllegalAccessException |
                         InvocationTargetException e) {
                    Log.e(TAG, "getWifiFactoryMacAddresses: ", e);
                }
            } else {
                Log.e(TAG, "getWifiFactoryMacAddresses: wifiManager is null");
            }
        } else {
            Log.e(TAG, "getWifiFactoryMacAddresses: SDK_INT is not UPSIDE_DOWN_CAKE or VANILLA_ICE_CREAM");
        }
        return macAddress;
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
