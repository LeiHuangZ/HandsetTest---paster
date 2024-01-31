/*
 * SPDX-FileCopyrightText: 2016-2023 Unisoc (Shanghai) Technologies Co., Ltd
 * SPDX-License-Identifier: LicenseRef-Unisoc-General-1.0
 */

package com.sprd.validationtools.itemstest.sysinfo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.handheld.huang.handsettest.R;
import com.sprd.validationtools.TelephonyManagerSprd;
import com.sprd.validationtools.utils.IATUtils;

public class RFCALITest extends AppCompatActivity {

    private static final String TAG = "RFCALITest";
    //This is only for 9620

    private String str = "loading...";
    private TextView txtViewlabel01;
    private Handler mUiHandler = new Handler();
    private DataInputStream mInputStream=null;
    private static final int ADCBYTES = 56;
    byte[] buffer = new byte[ADCBYTES];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rf_cali_test);
        txtViewlabel01 = (TextView) findViewById(R.id.rfc_id);
        txtViewlabel01.setTextSize(18);
        txtViewlabel01.setText(str);

        initial();
    }

    private void initial() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int modemType = TelephonyManagerSprd.getModemType();
                Log.d(TAG,"initial modemType="+modemType);
                if (modemType == TelephonyManagerSprd.MODEM_TYPE_TDSCDMA
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.TDD_CSFB
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.CSFB ) {
                    str = "GSM/TD ";
                } else {
                    str = "GSM ";
                }
                Log.d(TAG,"initial str="+str);
                str += IATUtils.sendATCmd("AT+SGMR=0,0,3,0", "atchannel0");
                //Support WCDMA
                if (modemType == TelephonyManagerSprd.MODEM_TYPE_WCDMA
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.FDD_CSFB
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.CSFB
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.LWLW
                        /*SPRd bug 830737:Add for support WCDMA*/
                        || TelephonyManagerSprd.IsSupportWCDMA()) {
                    str += "WCDMA ";
                    str += IATUtils.sendATCmd("AT+SGMR=0,0,3,1,1", "atchannel0");
                } else if(modemType == TelephonyManagerSprd.MODEM_TYPE_LTE) {
                    /*SPRD bug 773421:Supprt WCDMA*/
                    if(TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG){
                        str += "WCDMA ";
                        str += IATUtils.sendATCmd("AT+SGMR=0,0,3,1,1", "atchannel0");
                    }
                }
                if(modemType == TelephonyManagerSprd.MODEM_TYPE_LTE || TelephonyManagerSprd.IsSupportLTE()) {
                    //WG not support LTE
                    if(TelephonyManagerSprd.getRadioCapbility() != TelephonyManagerSprd.RadioCapbility.WG){
                        str += "LTE ";
                        //New at cmd for LTE band
                        String temp = IATUtils.sendAtCmd("AT+SGMR=1,0,3,3,1");
                        if(!IATUtils.AT_FAIL.equalsIgnoreCase(temp)){
                            str += temp;
                        }else{
                            str += IATUtils.sendATCmd("AT+SGMR=1,0,3,3", "atchannel0");
                        }
                    }
                }
                if(TelephonyManagerSprd.IsSupportCDMA()) {
                    str += "CDMA2000 ";
                    str += IATUtils.sendATCmd("AT+SGMR=0,0,3,2", "atchannel0");
                }
                if(TelephonyManagerSprd.IsSupportNR()) {
                    str += "NR ";
                    str += IATUtils.sendATCmd("AT+SGMR=1,0,3,4", "atchannel0");
                }
                mUiHandler.post(new Runnable() {
                    public void run() {
                        showCaliStr(str);
                    }
                });
            }
        }).start();
    }

    /*UNSOC bug1780225:APK MMI califlag display optimization*/
    private void showCaliStr(String str) {
        String allStr = "";
        String[] strs = str.split("\n");
        for (int i=0;i<strs.length;i++) {
            if (strs[i].toLowerCase().contains("pass")){
                if (strs[i].toLowerCase().contains("not")) {
                    allStr += setStringColor(strs[i], "red");
                } else {
                    allStr += setStringColor(strs[i], "green");
                }
            } else if (strs[i].toLowerCase().contains("error")) {
                allStr += setStringColor(strs[i], "red");
            } else {
                allStr += setStringColor(strs[i], "white");
            }
        }
        txtViewlabel01.setText(Html.fromHtml(allStr));
    }

    private String setStringColor(String str, String color) {
        if (color.equals("red")) {
            return "<font color=\"#ff0000\">" + str + "</font><br/>";
        } else if (color.equals("green")) {
            return "<font color=\"#00ff00\">" + str + "</font><br/>";
        } else if (color.equals("white")) {
            return "<font color=\"#ffffff\">" + str + "</font><br/>";
        } else {
            return "<font color=\"#ffff00\">" + str + "</font><br/>";
        }
    }
    /*@}*/

    @Override
    protected void onDestroy() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}
