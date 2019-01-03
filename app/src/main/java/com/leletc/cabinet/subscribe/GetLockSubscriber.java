package com.leletc.cabinet.subscribe;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leletc.cabinet.log.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 功能描述：获得锁状态的订阅类
 * <p>
 *
 * @author 李斌
 * <p>
 * @date 2018/10/30 13:53
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class GetLockSubscriber implements Observer<String> {

    private static final String TAG = GetLockSubscriber.class.getSimpleName() + "  - Locker";

    private boolean opened;
    private Context context;

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onComplete() {
        this.opened = true;
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onError(Throwable e) {
        Logger.e(TAG, "获取锁信息异常：" + e.getMessage());
    }

    @Override
    public void onNext(String s) {
        JSONObject response = JSON.parseObject(s);
        int result = response.getInteger("result_code");
        if (result == 0) {
            setOpened(response.getBoolean("is_opened"));
            //Toast.makeText(getContext(), "箱门状态:" + opened, Toast.LENGTH_LONG).show();
        } else {
            String msg = response.getString("msg");
            //Toast.makeText(getContext(), "获取箱门状态失败:" + msg, Toast.LENGTH_LONG).show();
            Logger.e(TAG, "获取箱门状态失败:" + msg);
        }
    }

}
