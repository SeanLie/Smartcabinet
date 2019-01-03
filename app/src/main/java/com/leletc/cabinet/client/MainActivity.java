package com.leletc.cabinet.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leletc.cabinet.R;
import com.leletc.cabinet.log.Logger;
import com.leletc.cabinet.subscribe.GetLockSubscriber;
import com.leletc.cabinet.subscribe.OpenLockSubscriber;
import com.leletc.cabinet.task.DoorGetLockerAsyncTask;
import com.leletc.cabinet.task.bo.NetReqBO;
import com.zhilai.commondriver.ILockerInterface;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 智能框主业务类
 *
 * @author Sean
 * @date 2018/10/21 10:00:23
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName() + " - Main";

    /* For receive customer msg from jpush server */
    //private MessageReceiver mMessageReceiver;
    private static ILockerInterface iLocker;
    public static final String LOCKER_ACTION = "android.intent.action.ZLDRIVER";
    //public static final String MONITOR_ACTION = "com.zhilai.driver.ACTION.monitor";
    public static final String ZHILAI_PACKAGE = "com.zhilai.commondriver";
    /**
     * 锁连接
     */
    private ServiceConnection lockerConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化消息推送器
        // FIXME 正式版本需改为false
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Logger.i(TAG, "初始化JPush消息服务...");
        //new GenerateRegisterJpushAsyncTask().execute(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 初始化锁控服务
        initLocker();
    }

    @Override
    protected void onDestroy() {
        if (null != lockerConnection) {
            unbindService(lockerConnection);
            Logger.i(TAG, "Locker Connection onStop: stopped");
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (null != lockerConnection) {
            unbindService(lockerConnection);
            Logger.i(TAG, "Locker Connection onStop: stopped");
        }*/
    }

    /**
     * 初始化锁服务
     */
    private void initLocker() {
        // 连接监控服务
        /*Intent intent = new Intent(MONITOR_ACTION);
        intent.setPackage(ZHILAI_PACKAGE);
        monitorConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Logger.i(TAG, "onServiceConnected: connected");
                Toast.makeText(MainActivity.this, "服务已连接", Toast.LENGTH_LONG).show();
                iMonitor = IMonitor.Stub.asInterface(service);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Logger.i(TAG, "onServiceDisconnected: disconnected");
                iMonitor = null;
            }
        };
        bindService(intent, monitorConnection, BIND_AUTO_CREATE);*/
        // 连接锁控服务
        Intent intent = new Intent(LOCKER_ACTION);
        intent.setPackage(ZHILAI_PACKAGE);
        if (null == lockerConnection) {
            lockerConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    iLocker = ILockerInterface.Stub.asInterface(service);
                    if (null != iLocker) {
                        Logger.i(TAG, "onServiceConnected: Connected");
                        Toast.makeText(MainActivity.this, "智能柜服务已连接",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Logger.i(TAG, "onServiceConnected: disconnected");
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Logger.i(TAG, "onServiceDisconnected: disconnected");
                    iLocker = null;
                }
            };
        }
        bindService(intent, lockerConnection, BIND_AUTO_CREATE);
    }

    /**
     * 打开存钥匙页面
     *
     * @param view View
     */
    public void onOpenSaveKeyPageClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SaveGetKeyActivity.class);
        /* 通过Bundle对象存储需要传递的数据 */
        Bundle bundle = new Bundle();
        /*字符、字符串、布尔、字节数组、浮点数等等，都可以传*/
        bundle.putInt("bizType", 1);// 存
        /*把bundle对象assign给Intent*/
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 打开取钥匙页面
     *
     * @param view View
     */
    public void onOpenGetKeyPageClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SaveGetKeyActivity.class);
        /* 通过Bundle对象存储需要传递的数据 */
        Bundle bundle = new Bundle();
        /*字符、字符串、布尔、字节数组、浮点数等等，都可以传*/
        bundle.putInt("bizType", 2);// 取
        /*把bundle对象assign给Intent*/
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 开箱门:false:打开失败，true:打开成功
     *
     * @param boxNo                  箱门号
     * @param doorGetLockerAsyncTask
     * @param req                    请求对象
     * @param messageTextView
     * @param qrCodeImageView
     * @return Boolean
     */
    public static boolean openLocker(final String boxNo, Activity activity,
                                     DoorGetLockerAsyncTask doorGetLockerAsyncTask,
                                     NetReqBO req, TextView messageTextView,
                                     ImageView qrCodeImageView) {
        OpenLockSubscriber openLockSubscriber = new OpenLockSubscriber();
        Observable.create((ObservableEmitter<String> observer) -> {
            String response = iLocker.openLocker(boxNo);
            observer.onNext(response);
            observer.onComplete();
            if (!doorGetLockerAsyncTask.isRunning()) {
                doorGetLockerAsyncTask.execute(req);
            }
            activity.runOnUiThread(() -> {
                final String openMsg = String.format("格口 %1$s 已打开", boxNo);
                messageTextView.setText(openMsg);
                messageTextView.setVisibility(View.VISIBLE);
                qrCodeImageView.setVisibility(View.GONE);
            });
            TimeUnit.SECONDS.sleep(1);
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(openLockSubscriber);

        return openLockSubscriber.isOpened();
    }

    /**
     * 获取箱门状态:false：关，true：开
     *
     * @param boxNo 箱门号
     * @return Boolean
     */
    public static boolean getLocker(final String boxNo) {
        GetLockSubscriber lockSubscriber = new GetLockSubscriber();
        String response;
        try {
            response = iLocker.getLocker(boxNo);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        lockSubscriber.onNext(response);
        lockSubscriber.onComplete();
        return lockSubscriber.isOpened();
    }

    /*
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
