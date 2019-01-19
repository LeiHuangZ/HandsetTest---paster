package com.handheld.huang.handsettest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;

/**
 * SP工具类,管理存储的string类型的信息
 *
 * @author huang
 * @date 2017/10/19
 */

public class SpUtils {
    private final SharedPreferences mPreferences;

    public SpUtils(Context context) {
        mPreferences = context.getSharedPreferences("check_in", Context.MODE_PRIVATE);
    }

    public void clearAll(){
        mPreferences.edit().clear().apply();
    }
    public int getCount(){
        return mPreferences.getAll().size();
    }
    /** 存储屏幕测试结果 */
    public void saveDisplayCheckResult(int result){
        mPreferences.edit().putInt("DisplayCheckResult", result).apply();
    }
    /** 获取屏幕测试结果  0 --> 通过，1 --> 不通过 */
    public int getDisplayCheckResult(){
        return mPreferences.getInt("DisplayCheckResult", 1);
    }

    /** 存储触摸测试结果 */
    public void saveToucheCheckResult(int result){
        mPreferences.edit().putInt("ToucheCheckResult", result).apply();
    }
    /** 获取触摸测试结果 */
    public int getTouchCheckResult(){
        return mPreferences.getInt("ToucheCheckResult", 1);
    }

    /** 存储背光测试结果 */
    public void saveLedCheckResult(int result){
        mPreferences.edit().putInt("LedCheckResult", result).apply();
    }
    /** 获取背光测试结果 */
    public int getLedCheckResult(){
        return mPreferences.getInt("LedCheckResult", 1);
    }

    /** 存储键盘测试结果 */
    public void saveKeyCheckResult(int result){
        mPreferences.edit().putInt("KeyCheckResult", result).apply();
    }
    /** 获取键盘测试结果 */
    public int getKeyCheckResult(){
        return mPreferences.getInt("KeyCheckResult", 1);
    }

    /** 存储相机测试结果 */
    public void saveCameraCheckResult(int result){
        mPreferences.edit().putInt("CameraCheckResult", result).apply();
    }
    /** 获取相机测试结果 */
    public int getCameraCheckResult(){
        return mPreferences.getInt("CameraCheckResult", 1);
    }

    /** 存储通话测试结果 */
    public void saveCallCheckResult(int result){
        mPreferences.edit().putInt("CallCheckResult", result).apply();
    }
    /** 获取通话测试结果 */
    public int getCallCheckResult(){
        return mPreferences.getInt("CallCheckResult", 1);
    }

    /** 存储SD测试结果 */
    public void saveSdCheckResult(int result){
        mPreferences.edit().putInt("SdCheckResult", result).apply();
    }
    /** 获取SD测试结果 */
    public int getSdCheckResult(){
        return mPreferences.getInt("SdCheckResult", 1);
    }

    /** 存储喇叭测试结果 */
    public void saveSpeakerCheckResult(int result){
        mPreferences.edit().putInt("SpeakerCheckResult", result).apply();
    }
    /** 获取喇叭测试结果 */
    public int getSpeakerCheckResult(){
        return mPreferences.getInt("SpeakerCheckResult", 1);
    }

    /** 存储MAC测试结果 */
    public void saveMacCheckResult(int result){
        mPreferences.edit().putInt("MacCheckResult", result).apply();
    }
    /** 获取MAC测试结果 */
    public int getMacCheckResult(){
        return mPreferences.getInt("MacCheckResult", 1);
    }

    /** 存储IMEI测试结果 */
    public void saveImeiCheckResult(int result){
        mPreferences.edit().putInt("ImeiCheckResult", result).apply();
    }
    /** 获取IMEI测试结果 */
    public int getImeiCheckResult(){
        return mPreferences.getInt("ImeiCheckResult", 1);
    }
    /**存储flash序列号*/
    public void saveFlashCheckResult(int result){
        mPreferences.edit().putInt("ImeiCheckResult", result).apply();
    }
    /**获取flash序列号*/
    public int getFlashCheckResult(){
        return mPreferences.getInt("ImeiCheckResult", 1);
    }
    /** 存储主板校准测试结果 */
    public void saveBoardCheckResult(int result){
        mPreferences.edit().putInt("BoardCheckResult", result).apply();
    }
    /** 获取主板校准测试结果 */
    public int getBoardCheckResult(){
        return mPreferences.getInt("BoardCheckResult", 1);
    }

    /** 存储wifi测试结果 */
    public void saveWifiCheckResult(int result){
        mPreferences.edit().putInt("WifiCheckResult", result).apply();
    }
    /** 获取wifi测试结果 */
    public int getWifiCheckResult(){
        return mPreferences.getInt("WifiCheckResult", 1);
    }

    /** 存储mic测试结果 */
    public void saveMicCheckResult(int result){
        mPreferences.edit().putInt("MicCheckResult", result).apply();
    }
    /** 获取mic测试结果 */
    public int getMicCheckResult(){
        return mPreferences.getInt("MicCheckResult", 1);
    }

    /** 存储蓝牙测试结果 */
    public void saveBluetoothCheckResult(int result){
        mPreferences.edit().putInt("BluetoothCheckResult", result).apply();
    }
    /** 获取蓝牙测试结果 */
    public int getBluetoothCheckResult(){
        return mPreferences.getInt("BluetoothCheckResult", 1);
    }

    /** 存储gps测试结果 */
    public void saveGpsCheckResult(int result){
        mPreferences.edit().putInt("GpsCheckResult", result).apply();
    }
    /** 获取gps测试结果 */
    public int getGpsCheckResult(){
        return mPreferences.getInt("GpsCheckResult", 1);
    }

    /** 存储身份证测试结果 */
    public void saveIDCheckResult(int result){
        mPreferences.edit().putInt("IDCheckResult", result).apply();
    }
    /** 获取身份证测试结果 */
    public int getIDCheckResult(){
        return mPreferences.getInt("IDCheckResult", 1);
    }

    /** 存储指示灯测试结果 */
    public void saveIndicatorCheckResult(int result){
        mPreferences.edit().putInt("IndicatorCheckResult", result).apply();
    }
    /** 获取指示灯测试结果 */
    public int getIndocatorCheckResult(){
        return mPreferences.getInt("IndicatorCheckResult", 1);
    }

    /** 存储开机电流测试结果 */
    public void saveOnElectricityCheckResult(int result){
        mPreferences.edit().putInt("OnElectricityCheckResult", result).apply();
    }
    /** 获取开机电流测试结果 */
    public int getOnElectricityCheckResult(){
        return mPreferences.getInt("OnElectricityCheckResult", 1);
    }

    /** 存储关机电流测试结果 */
    public void saveOffElectricityCheckResult(int result){
        mPreferences.edit().putInt("OffElectricityCheckResult", result).apply();
    }
    /** 获取关机电流测试结果 */
    public int getOffElectricityCheckResult(){
        return mPreferences.getInt("OffElectricityCheckResult", 1);
    }

    /** 存储USB测试结果 */
    public void saveUsbCheckResult(int result){
        mPreferences.edit().putInt("saveUsbCheckResult", result).apply();
    }
    /** 获取USB测试结果 */
    public int getUsbCheckResult(){
        return mPreferences.getInt("saveUsbCheckResult", 1);
    }

    /** 存储底座测试结果 */
    public void saveChargerCheckResult(int result){
        mPreferences.edit().putInt("saveChargerCheckResult", result).apply();
    }
    /** 获取底座测试结果 */
    public int getChargerCheckResult(){
        return mPreferences.getInt("saveChargerCheckResult", 1);
    }

    /** 背面螺丝 */
    public void saveBackScrewCheckResult(int result){
        mPreferences.edit().putInt("BackScrewCheckResult", result).apply();
    }
    /** 背面螺丝 */
    public int getBackScrewCheckResult(){
        return mPreferences.getInt("BackScrewCheckResult", 1);
    }

    /** 头部螺丝 */
    public void saveHeadScrewCheckResult(int result){
        mPreferences.edit().putInt("HeadScrewCheckResult", result).apply();
    }
    /** 头部螺丝 */
    public int getHeadScrewCheckResult(){
        return mPreferences.getInt("HeadScrewCheckResult", 1);
    }

    /** 底部螺丝 */
    public void saveBottomScrewCheckResult(int result){
        mPreferences.edit().putInt("BottomScrewCheckResult", result).apply();
    }
    /** 底部螺丝 */
    public int getBottomScrewCheckResult(){
        return mPreferences.getInt("BottomScrewCheckResult", 1);
    }

    /** 镜片logo */
    public void saveLensLogoCheckResult(int result){
        mPreferences.edit().putInt("LensLogoCheckResult", result).apply();
    }
    /** 镜片logo */
    public int getLensLogoCheckResult(){
        return mPreferences.getInt("LensLogoCheckResult", 1);
    }

    /** 镜片前舱盖 */
    public void saveLensHatchesCheckResult(int result){
        mPreferences.edit().putInt("LensHatchesCheckResult", result).apply();
    }
    /** 镜片前舱盖 */
    public int getLensHatchesCheckResult(){
        return mPreferences.getInt("LensHatchesCheckResult", 1);
    }

    /** 腕带 */
    public void saveSpireCheckResult(int result){
        mPreferences.edit().putInt("SpireCheckResult", result).apply();
    }
    /** 腕带 */
    public int getSpireCheckResult(){
        return mPreferences.getInt("SpireCheckResult", 1);
    }

    /** 壳体 */
    public void saveShellCheckResult(int result){
        mPreferences.edit().putInt("ShellCheckResult", result).apply();
    }
    /** 壳体 */
    public int getShellCheckResult(){
        return mPreferences.getInt("ShellCheckResult", 1);
    }
}
