package com.leletc.cabinet.task.handler;

import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.leletc.cabinet.client.MainActivity;
import com.leletc.cabinet.client.SaveGetKeyActivity;
import com.leletc.cabinet.log.Logger;
import com.leletc.cabinet.task.IDeviceCallback;
import com.leletc.cabinet.task.OnViewRefreshStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：箱门状态处理类
 * <p>
 *
 * @author 李斌
 * <p>
 * @date 2018/10/30 12:28
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class CabinetDoorStatusHandler implements IDeviceCallback {

    private static final String TAG = CabinetDoorStatusHandler.class.getSimpleName();

    private static volatile CabinetDoorStatusHandler instance = null;
    private ScheduledExecutorService executorService;
    public static final int TYPE_MATCH = 1;

    @Override
    public void onDeviceCallback(int type, Map<Object, Object> msgMap) {
        if (type == TYPE_MATCH) {
            matchMap = msgMap;
        }
        updateView(type);
    }

    private Map<Object, Object> matchMap = new HashMap<>();
    private List<WeakReference<View>> list = new ArrayList<>();
    /**
     * 处理器
     */
    private Handler handler;

    private CabinetDoorStatusHandler() {
        getHandlerInstance();
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            doTasks();
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 获得当前类的实例
     *
     * @return
     */
    public static CabinetDoorStatusHandler getInstance() {
        // if already initialized, no need to get lock always
        if (null == instance) {
            synchronized (CabinetDoorStatusHandler.class) {
                instance = new CabinetDoorStatusHandler();
            }
        }
        return instance;
    }

    /**
     * 执行任务
     */
    private void doTasks() {
        Map<Object, Object> map = new HashMap<>();
        map.put(SaveGetKeyActivity.clientID, MainActivity.getLocker(SaveGetKeyActivity.getBoxNo()));
        Logger.i(TAG, "doTask:" + JSON.toJSONString(map));
        //CabinetDoorStatusHandler.getInstance().onDeviceCallback(TYPE_MATCH, map);
    }

    /**
     * 订阅指定任务
     *
     * @param view
     * @param onViewRefreshStatus
     * @param <VIEW>
     */
    public <VIEW extends View> void subscribeMatch(VIEW view, OnViewRefreshStatus onViewRefreshStatus) {
        subscribe(TYPE_MATCH, view, onViewRefreshStatus);
    }

    /**
     * 任务订阅
     *
     * @param type
     * @param view
     * @param v
     * @param <VIEW>
     */
    private <VIEW extends View> void subscribe(int type, @NonNull VIEW view, OnViewRefreshStatus v) {
        view.setTag(v);
        if (type == TYPE_MATCH) {
            v.update(view, matchMap);
        }
        for (WeakReference<View> viewSoftReference : list) {
            View textView = viewSoftReference.get();
            if (textView == view) {
                return;
            }
        }
        WeakReference<View> viewSoftReference = new WeakReference<>(view);
        list.add(viewSoftReference);
    }

    /**
     * 更新视图
     *
     * @param type
     */
    public void updateView(final int type) {
        Iterator<WeakReference<View>> iterator = list.iterator();
        while (iterator.hasNext()) {
            WeakReference<View> next = iterator.next();
            final View view = next.get();
            if (view == null) {
                iterator.remove();
                continue;
            }
            Object tag = view.getTag();
            if (tag == null || !(tag instanceof OnViewRefreshStatus)) {
                continue;
            }
            final OnViewRefreshStatus refreshStatus = (OnViewRefreshStatus) tag;
            getHandlerInstance().post(() -> {
                if (type == TYPE_MATCH) {
                    refreshStatus.update(view, matchMap);
                }
            });
        }
    }

    /**
     * 销毁处理器
     */
    public void destroy() {
        executorService.shutdown();
        instance = null;
    }

    /**
     * 定义处理器
     *
     * @return
     */
    private Handler getHandlerInstance() {
        if (handler == null && Looper.myLooper() == Looper.getMainLooper()) {
            handler = new Handler();
        }
        return handler;
    }

}
