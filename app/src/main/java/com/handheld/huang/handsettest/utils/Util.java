package com.handheld.huang.handsettest.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.util.SparseIntArray;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.handheld.huang.handsettest.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 播放声音，初始化线程池
 *
 * @author huang
 */
public class Util {

    private SoundPool sp;
    private SparseIntArray suondMap;
    private Context mContext;

    public Util(Context context) {
        mContext = context;
    }

    public void initAudio() {
        AudioAttributes.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new AudioAttributes.Builder();
            builder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            AudioAttributes audioAttributes = builder.build();
            sp = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build();
        } else {
            sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }
        suondMap = new SparseIntArray();
        suondMap.put(1, sp.load(mContext, R.raw.msg, 1));
        suondMap.put(2, sp.load(mContext, R.raw.warning, 1));
    }

    public void playAudio(int key) {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        sp.play(suondMap.get(key), audioCurrentVolume, audioCurrentVolume, 1, 0, 1);
    }

    public void closeAudio() {
        sp.pause(0);
        sp.release();
    }

    /**
     * 获取线程池
     */
    public ExecutorService getExecutorService() {
        //初始化线程池
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        return new ThreadPoolExecutor(3, 200, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 根据uri获取真实路径
     */
    public String getDataColumn(Context context, Uri uri) {
        Cursor cursor = null;
        //路径保存在downloads表中的_data字段
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }
}
