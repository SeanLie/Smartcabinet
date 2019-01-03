package com.leletc.cabinet.subscribe;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leletc.cabinet.log.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 功能描述：开锁对象订阅者
 * <p>
 *
 * @author 李斌
 * <p>
 * @date 2018/10/31 14:25
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class OpenLockSubscriber implements Observer<String> {

    private static final String TAG = OpenLockSubscriber.class.getSimpleName() + "  - Locker";

    private boolean opened;
    private Context context;

    public boolean isOpened() {
        return opened;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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
        Logger.e(TAG, "开门异常：" + e.getMessage());
    }

    @Override
    public void onNext(String s) {
        JSONObject response = JSON.parseObject(s);
        int result = response.getInteger("result_code");
        if (result == 0) {
            this.opened = true;
            //setOpened(result);
//            Toast.makeText(getContext(), "开门成功!", Toast.LENGTH_LONG).show();
        } else {
            String msg = response.getString("msg");
            Logger.e(TAG, "开门失败：" + msg);
            //Toast.makeText(getContext(), "开门失败：" + msg, Toast.LENGTH_LONG).show();
        }
    }
}
