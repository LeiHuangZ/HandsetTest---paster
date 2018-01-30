package com.handheld.huang.handsettest.activity.identification.handheld.instance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handheld.huang.handsettest.R;
import com.handheld.huang.handsettest.activity.IndicatorTestActivity;
import com.handheld.huang.handsettest.activity.identification.handheld.idcard.IDCardManager;
import com.handheld.huang.handsettest.activity.identification.handheld.idcard.IDCardModel;
import com.handheld.huang.handsettest.utils.SpUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * @author huang
 */
public class IDReadActivity extends AppCompatActivity {
    private static String TAG = IDReadActivity.class.getSimpleName();

    @BindView(R.id.editText_name)
    TextView editTextName;
    @BindView(R.id.editText_sex)
    TextView editTextSex;
    @BindView(R.id.editText_nation)
    TextView editTextNation;
    @BindView(R.id.editText_year)
    TextView editTextYear;
    @BindView(R.id.editText_month)
    TextView editTextMonth;
    @BindView(R.id.editText_day)
    TextView editTextDay;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.editText_address)
    TextView editTextAddress;
    @BindView(R.id.editText_IDCard)
    TextView editTextIDCard;
    @BindView(R.id.editText_office)
    TextView editTextOffice;
    @BindView(R.id.editText_effective)
    TextView editTextEffective;
    @BindView(R.id.button_next)
    Button buttonFinish;
    @BindView(R.id.rl_1)
    RelativeLayout mRl1;
    @BindView(R.id.rl_2)
    RelativeLayout mRl2;
    @BindView(R.id.result_tv_question)
    TextView mResultTvQuestion;
    @BindView(R.id.result_img_ok)
    ImageView mResultImgOk;
    @BindView(R.id.result_img_cross)
    ImageView mResultImgCross;
    @BindView(R.id.result_tv_next)
    TextView mResultTvNext;
    @BindView(R.id.result_ll_confirm)
    LinearLayout mResultLlConfirm;

    private IDCardManager manager;
    private Toast toast;
    private Bitmap photoBitmap = null;
    private boolean findFlag = true;
    private SpUtils mSpUtils;

    @SuppressLint("ShowToast")
    private void showToast(String info) {
        if (toast == null) {
            toast = Toast.makeText(IDReadActivity.this, info, Toast.LENGTH_SHORT);
        } else {
            toast.setText(info);
        }
        toast.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idread_improve);

        ButterKnife.bind(this);
        manager = new IDCardManager(IDReadActivity.this);
        mSpUtils = new SpUtils(IDReadActivity.this);

        Util.initSoundPool(IDReadActivity.this);

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService mExecutorService = new ThreadPoolExecutor(3, 200, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                while (findFlag) {
                    if (manager.findCard()) {
                        IDCardModel model;
                        handler.sendEmptyMessage(1);
                        model = manager.getData(2000);
                        if (model != null) {
                            sendMessage(model.getName(), model.getSex(), model.getNation(),
                                    model.getYear(), model.getMonth(), model.getDay(),
                                    model.getAddress(), model.getIDCardNumber().trim(), model.getOffice(),
                                    model.getBeginTime(), model.getEndTime(), model.getOtherData(),
                                    model.getPhotoBitmap());
                        }
                        if (model != null) {
                            mSpUtils.saveShellCheckResult(0);
                        }
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private MyHandler handler = new MyHandler(this);
    int checkResult = 0;
    @OnClick({R.id.result_img_ok, R.id.result_img_cross, R.id.result_tv_next, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.result_img_ok:
                mResultImgOk.setImageResource(R.drawable.check_ok_selected);
                mResultImgCross.setImageResource(R.drawable.check_cross_unselected);
                checkResult = 0;
                mResultTvNext.setClickable(true);
                break;
            case R.id.result_img_cross:
                mResultImgCross.setImageResource(R.drawable.check_cross_selected);
                mResultImgOk.setImageResource(R.drawable.check_ok_unselected);
                checkResult = 1;
                mResultTvNext.setClickable(true);
                break;
            case R.id.result_tv_next:
                mSpUtils.saveIDCheckResult(checkResult);
                Log.i(TAG, "IDCheckResult: " + mSpUtils.getIDCheckResult());
                startActivity(new Intent(IDReadActivity.this, IndicatorTestActivity.class));
                overridePendingTransition(R.animator.activity_start_rigth, 0);
                finish();
                break;
            case R.id.button_next:
                mRl1.setVisibility(View.GONE);
                mRl2.setVisibility(View.GONE);
                buttonFinish.setVisibility(View.GONE);
                mResultTvQuestion.setText(getResources().getString(R.string.idread_test_confirm));
                mResultLlConfirm.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<IDReadActivity> mIDReadActivityWeakReference;

        MyHandler(IDReadActivity idReadActivity) {
            mIDReadActivityWeakReference = new WeakReference<>(idReadActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            IDReadActivity idReadActivity = mIDReadActivityWeakReference.get();
            switch (msg.what) {
                case 0:
                    Util.play(1, 0);
                    idReadActivity.showToast("读取完成！");
                    Bundle bundle = msg.getData();
                    String name = bundle.getString("name");
                    String sex = bundle.getString("sex");
                    String nation = bundle.getString("nation");
                    String year = bundle.getString("year");
                    String month = bundle.getString("month");
                    String day = bundle.getString("day");
                    String address = bundle.getString("address");
                    String id = bundle.getString("id");
                    String office = bundle.getString("office");
                    String start = bundle.getString("begin");
                    String stop = bundle.getString("end");
                    idReadActivity.updateView(name, sex, nation, year, month, day, address, id, office, start, stop);
                    break;
                case 1:
                    idReadActivity.clear();
                    idReadActivity.showToast("找到身份证֤\n正在读取");
                    break;
                case 2:
                    idReadActivity.showToast("");
                    break;
                case 3:

                    break;
                default:
                    break;
            }
        }
    }

    private void updateView(String name, String sex, String nation,
                            String year, String month, String day, String address, String id,
                            String office, String start, String stop) {
        imageView.setImageBitmap(photoBitmap);
        editTextName.setText(name);
        editTextSex.setText(sex);
        editTextNation.setText(nation);
        editTextYear.setText(year);
        editTextMonth.setText(month);
        editTextDay.setText(day);
        editTextAddress.setText(address);
        editTextIDCard.setText(id);
        editTextOffice.setText(office);
        editTextEffective.setText(format(start));
        editTextEffective.append("-" + format(stop));
        buttonFinish.setEnabled(true);
    }

    private String format(String str) {
        StringBuilder buffer = new StringBuilder(str);
        StringBuilder buffer1 = buffer.insert(4, ".");
        return buffer1.insert(7, ".").toString();
    }

    private void clear() {
        editTextName.setText("");
        editTextSex.setText("");
        editTextNation.setText("");
        editTextYear.setText("");
        editTextMonth.setText("");
        editTextDay.setText("");
        editTextAddress.setText("");
        editTextIDCard.setText("");
        editTextOffice.setText("");
        editTextEffective.setText("");
        imageView.setImageResource(R.drawable.photo);
    }

    private void sendMessage(String name, String sex, String nation,
                             String year, String month, String day, String address, String id,
                             String office, String start, String stop, String newaddress
            , Bitmap bitmap) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("sex", sex);
        bundle.putString("nation", nation);
        bundle.putString("year", year);
        bundle.putString("month", month);
        bundle.putString("day", day);
        bundle.putString("address", address);
        bundle.putString("id", id);
        bundle.putString("office", office);
        bundle.putString("begin", start);
        bundle.putString("end", stop);
        bundle.putString("newaddress", newaddress);
        bundle.putString("fp1", "");
        bundle.putString("fp2", "");
        photoBitmap = bitmap;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        findFlag = false;
        if (manager != null) {
            manager.close();
            manager = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        findFlag = false;
        if (manager != null) {
            manager.close();
        }
    }
}
