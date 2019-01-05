package com.leletc.cabinet.client;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.leletc.cabinet.R;
import com.leletc.cabinet.listener.DoorLockStatusListener;
import com.leletc.cabinet.listener.QrListener;
import com.leletc.cabinet.log.Logger;
import com.leletc.cabinet.receiver.QRReceiver;
import com.leletc.cabinet.task.DoorGetLockerAsyncTask;
import com.leletc.cabinet.task.OpenOrCloseCabinetTask;
import com.leletc.cabinet.task.bo.NetReqBO;
import com.leletc.cabinet.util.BaseUtils;
import com.leletc.cabinet.util.QRCodeUtils;
import com.leletc.cabinet.util.SystemConstants;

import org.apache.commons.lang.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * 存取钥匙业务控制类
 *
 * @author Sean
 * @date 2018/10/21
 */
public class SaveGetKeyActivity extends AppCompatActivity implements QrListener, DoorLockStatusListener {

    public static final String TAG = SaveGetKeyActivity.class.getSimpleName();
    /**
     * 客户端运行实时ID
     */
    public static long clientID;
    public static boolean isForeground = false;

    public static final String MESSAGE_RECEIVED_ACTION = "com.leletc.cabinet.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    /**
     * 消息推送用户注册ID
     */
    public static String registrationID;

    /**
     * 扫描柜子处理的URL
     */
    public static final String SCAN_CODE_REQUEST_URL = "/jeecg/rest/smartCabinetCodeScanner/scanSmartCabinetQRCode";
    /**
     * 关闭柜子处理的URL
     */
    public static final String CLOSE_BOX_REQUEST_URL = "/jeecg/rest/smartCabinetCodeScanner/closeSmartCabinetDoor";

    /**
     * 业务类型(1:存钥匙，2：取钥匙)
     */
    private static int bizType = 0;
    /**
     * 二维码消息接收器
     */
    private QRReceiver qRReceiver;
    /**
     * 开箱门指令：1：开
     */
    private static final int OPEN_BOX_DOOR_COMMAND = 1;
    /**
     * 当前箱门号
     */
    private static String boxNo;

    public static String getBoxNo() {
        return boxNo;
    }

    /**
     * 当前箱门状态(false:关闭，true:打开)
     */
    private static Boolean isCurrentDoorOpened = false;

    /**
     * 计时器
     */
    private TextView timerTextView;
    /**
     * 二维码显示
     */
    private ImageView qrCodeImageView;
    /**
     * 提示信息
     */
    private TextView messageTextView;

    private Timer autoReturnTimer;
    private TimerTask autoReturnTimerTask;

    private DoorGetLockerAsyncTask doorGetLockerAsyncTask;

    public static Boolean getIsCurrentDoorOpened() {
        return isCurrentDoorOpened;
    }

    public static void setIsCurrentDoorOpened(Boolean isCurrentDoorOpened) {
        SaveGetKeyActivity.isCurrentDoorOpened = isCurrentDoorOpened;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientID = System.currentTimeMillis();
        // 延时获取用户ID及生成二维码
        registrationID = JPushInterface.getRegistrationID(this);
        Logger.d(TAG, "获取JPush消息服务ID - registrationID - " + registrationID);
        // 加载页面 */
        setContentView(R.layout.activity_scan_code);
        timerTextView = findViewById(R.id.timerTextView);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        messageTextView = findViewById(R.id.messageTextView);
        //* 获取Intent中的Bundle对象*/
        Bundle extras = this.getIntent().getExtras();
        //* 获取Bundle中的数据，注意类型和key */
        if (null != extras) {
            bizType = extras.getInt("bizType");
        }
        // 初始化二维码
        initQRCode();
        // 初始化二维码扫码监听器
        initReceiver();
        // 计时自动返回至首页
        resetTimer(DEFAULT_RETURN_TIME_NO_ACTION);
        setAutoReturnAsyncTask();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(qRReceiver);
        cancelAutoReturnTask();
        // 如果箱门打开过，提示未关箱门
        if (StringUtils.isNotBlank(boxNo) && getIsCurrentDoorOpened()) {
            Toast.makeText(SaveGetKeyActivity.this, "格口 " + boxNo + " 未关闭",
                    Toast.LENGTH_LONG).show();
        } else {
            super.onDestroy();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*---------------------------- 柜子业务处理 */

    /**
     * 初始化二维码
     */
    private void initQRCode() {
        // 获取组件
        TextView saveGetView = findViewById(R.id.titleSaveGetKeyView);
        // 获得服务器URL
        SystemConstants.loadProperties(getBaseContext());
        StringBuilder url = new StringBuilder(SystemConstants.getUrlSchema()).append("://")
                .append(SystemConstants.getUrlHost())
                .append(SCAN_CODE_REQUEST_URL)
                .append("/").append(bizType)
                .append("/").append(registrationID)
                .append("/").append(SystemConstants.getCabinetNo());
        // 设置页面相关元素
        if (bizType == 1) {
            // 存钥匙
            saveGetView.setText(R.string.save_key_title_biz_name);
        } else if (bizType == 2) {
            // 取钥匙
            saveGetView.setText(R.string.get_key_title_biz_name);
        }
        Bitmap qrCodeMap = null;
        try {
            qrCodeMap = QRCodeUtils.createQRCode(url.toString(), 200, 200,
                    BaseUtils.getImageFromAssetsFile(this, "logo_50x50.png"));
            Logger.i(TAG, "Generate qr code - url = " + url.toString());
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (null != qrCodeMap) {
            // 设置二维码
            qrCodeImageView.setImageBitmap(qrCodeMap);
        } else {
            Toast.makeText(SaveGetKeyActivity.this, "系统出现故障，请联系客服。",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 初始化扫码接收器
     */
    private void initReceiver() {
        // 二维码接收器
        qRReceiver = new QRReceiver();
        qRReceiver.setQrListener(this);
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(qRReceiver, filter);
        /*lockStatusReceiver = new DoorLockStatusReceiver();
        lockStatusReceiver.setLockStatusListener(this);
        filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(lockStatusReceiver, filter);*/
        // 格口状态异步任务
        doorGetLockerAsyncTask = new DoorGetLockerAsyncTask();
        doorGetLockerAsyncTask.setLockStatusListener(this);
    }

    @Override
    public void onReceiveQrData(String data) {
        // 扫码之后，接收服务端的响应数据
        JSONObject json = JSON.parseObject(data.replaceAll("\n", ""));
        JSONObject message = (JSONObject) json.get("message");
        if (null == message) {
            return;
        }
        // 获取指令
        String command = message.getString("lockInstruct");
        // 格口号
        boxNo = message.getString("boxNo");
        String userId = message.getString("lockUser");
        String orderId = message.getString("orderId");
        NetReqBO req = new NetReqBO();
        req.setOrderId(orderId);
        req.setUserId(userId);
        req.setBizType(bizType);
        req.setToken(registrationID);
        req.setBoxNo(boxNo);
        req.setCabinetNo(SystemConstants.getCabinetNo());
        if (StringUtils.isNotBlank(command)) {
            int responseCommand = Integer.parseInt(command);
            if (responseCommand == OPEN_BOX_DOOR_COMMAND) {
                // 打开格口，测试时默认打开，正式环境需调用开锁接口
                if (SystemConstants.getEnv().equals(SystemConstants.ENV_TEST)) {
                    setIsCurrentDoorOpened(true);
                } else if (SystemConstants.getEnv().equals(SystemConstants.ENV_ONLINE)) {
                    setIsCurrentDoorOpened(MainActivity.openLocker(boxNo, this,
                            doorGetLockerAsyncTask, req, messageTextView, qrCodeImageView));
                }
                if (getIsCurrentDoorOpened()) {
                    // 格口已打开 */
                    final String openMsg = getString(R.string.door_open_success, boxNo);
                    Logger.i(TAG, openMsg);
                    messageTextView.setText(openMsg);
                    messageTextView.setVisibility(View.VISIBLE);
                    qrCodeImageView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onReceiveLockStatus(NetReqBO req) {
        //-------------------- 接收到格口状态
        // 请求服务器进行关门操作
        OpenOrCloseCabinetTask task = new OpenOrCloseCabinetTask();
        AsyncTask asyncTask = task.execute(req);
        Logger.i(TAG, "正在关闭格口 - " + asyncTask.getStatus().name());
        // 在界面上显示消息
        String closeMsg = getString(R.string.door_close_success, boxNo);
        Logger.i(TAG, closeMsg);
        messageTextView.setText(closeMsg);
        messageTextView.setVisibility(View.VISIBLE);
        // 隐藏二维码
        qrCodeImageView.setVisibility(View.GONE);
        // 重新计时自动返回
        resetTimer(DEFAULT_RETURN_TIME_SUCCESS);
        cancelAutoReturnTask();
        setAutoReturnAsyncTask();
    }

    /**
     * 默认的自动返回时间 （秒）,无操作的
     */
    private static final int DEFAULT_RETURN_TIME_NO_ACTION = 120;
    /**
     * 默认的自动返回时间 （秒）,操作成功的
     */
    private static final int DEFAULT_RETURN_TIME_SUCCESS = 30;
    // 计时瞬时变量
    private int timerTemp;
    // 当前显示秒数
    private static String currentSecondsTemp;

    /**
     * 计时60s自动返回至首页
     */
    public void resetTimer(int time) {
        timerTemp = time;
    }

    public int getTimerTemp() {
        return timerTemp;
    }

    /**
     * 倒计时自动返回至首页
     */
    private void setAutoReturnAsyncTask() {
        autoReturnTimer = new Timer();
        autoReturnTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    timerTemp--;
                    if (getTimerTemp() < 10) {
                        currentSecondsTemp = "0" + getTimerTemp();
                    } else {
                        currentSecondsTemp = "" + getTimerTemp();
                    }
                    timerTextView.setText(getString(R.string.saveGetKey_auto_return_name, currentSecondsTemp));
                    countTimer();
                });
            }
        };
        autoReturnTimer.scheduleAtFixedRate(autoReturnTimerTask, 1000, 1000);
    }

    /**
     * 计算计时器
     */
    private void countTimer() {
        if (getTimerTemp() <= 0) {
            // 返回至主界面
            returnToMain();
        }
    }

    /**
     * 点击返回按钮
     *
     * @param view 视图
     */
    public void onReturnActivity(View view) {
        returnToMain();
    }

    /**
     * 返回
     */
    private void returnToMain() {
        if (StringUtils.isNotBlank(boxNo) && getIsCurrentDoorOpened()) {
            Toast.makeText(SaveGetKeyActivity.this,
                    "箱门 " + boxNo + " 未关闭，请先关闭，谢谢！", Toast.LENGTH_LONG).show();
        } else {
            cancelAutoReturnTask();
            SaveGetKeyActivity.this.finish();
        }
    }

    private void cancelAutoReturnTask() {
        // 取消任务
        if (null != autoReturnTimerTask) {
            autoReturnTimerTask.cancel();
            autoReturnTimerTask = null;
        }
        // 取消计时器
        if (null != autoReturnTimer) {
            autoReturnTimer.cancel();
            autoReturnTimer.purge();
            autoReturnTimer = null;
        }
    }

}
