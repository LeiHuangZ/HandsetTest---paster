package com.handheld.huang.handsettest.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.handheld.huang.handsettest.BuildConfig;
import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author huang
 */
public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private PackageInfo mPackageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        new SpUtils(this).clearAll();
    }

    @Override
    protected void onStart() {
        checkGpsTest();
        super.onStart();
    }

    @OnClick({R.id.main_btn_all_test, R.id.main_btn_mac_test, R.id.main_btn_gps_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_btn_all_test:
//                startActivity(new Intent(MainActivity.this, DisplayTestActivity.class));
                startActivity(new Intent(MainActivity.this, DisplayTestActivity.class));
//                overridePendingTransition(R.animator.activity_start_rigth,0);
                finish();

                break;
            case R.id.main_btn_mac_test:
                startActivity(new Intent(MainActivity.this, MacTestActivity.class));
                break;
            case R.id.main_btn_gps_test:
                startActivity(new Intent(MainActivity.this, GpsTestActivity.class));
                break;
            default:
                break;
        }
    }

    private void checkGpsTest(){
        Log.i(TAG, "checkGpsTest！！！！");
        /*
         * 判断GPSTest软件是否已经安装
         */
        try {
            mPackageInfo = getPackageManager().getPackageInfo(
                    "com.chartcross.gpstest", 0);
            Log.i(TAG, "checkGpsTest！！！！");
        } catch (PackageManager.NameNotFoundException e) {
            mPackageInfo = null;
            e.printStackTrace();
        }
        if (mPackageInfo == null) {
            // 启用安装新线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "installThread, run >>>>>> GPSTest 未安装将进行安装");
                    boolean b = silentInstall();
                    Log.i(TAG, "installThread, installResult >>>>>> " + b);
                }
            }).start();
        } else {
            Log.e(TAG, "onCreate,  >>>>>> GPSTest 已经安装");
        }
    }

    /**
     * 静默安装
     * @return 是否安装成功
     */
    public boolean silentInstall() {
        // 进行资源的转移 将assets下的文件转移到可读写文件目录下
        createFile();
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/temp.apk");

        boolean result = false;
        Process process;
        OutputStream out;
        if (file.exists()) {
//            try {
//                process = Runtime.getRuntime().exec("su");
//                out = process.getOutputStream();
//                DataOutputStream dataOutputStream = new DataOutputStream(out);
//                // 获取文件所有权限
//                dataOutputStream.writeBytes("chmod 777 " + file.getPath() + "\n");
//                // 进行静默安装命令
//                dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " + file.getPath());
//                // 提交命令
//                dataOutputStream.flush();
//                // 关闭流操作
//                dataOutputStream.close();
//                out.close();
//                int value = process.waitFor();
//
//                // 代表成功
//                if (value == 0) {
//                    Log.e(TAG, "silentInstall,  >>>>>> 安装成功！");
//                    result = true;
//                // 失败
//                } else if (value == 1) {
//                    Log.e(TAG, "silentInstall,  >>>>>> 安装失败！");
//                    result = false;
//                // 未知情况
//                } else {
//                    Log.e(TAG, "silentInstall,  >>>>>> 未知情况！");
//                    result = false;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            if (!result) {
                Log.e(TAG, "silentInstall,  >>>>>> root权限获取失败，将进行普通安装");
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(android.content.Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
                result = true;
            }
        }
        return result;
    }

    public void createFile() {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = MainActivity.this.getAssets().open("GpsTest.apk");
            File file = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/temp.apk");
            boolean newFile = file.createNewFile();
            Log.e(TAG, "createFile, newFile >>>>>> " + newFile);
            fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }
}
