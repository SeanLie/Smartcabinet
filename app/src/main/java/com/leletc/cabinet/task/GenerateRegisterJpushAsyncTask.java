package com.leletc.cabinet.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leletc.cabinet.client.SaveGetKeyActivity;
import com.leletc.cabinet.log.Logger;

import cn.jpush.android.api.JPushInterface;

/**
 * 功能描述：异步任务注册获得用户ID
 * <p>
 *
 * @author 李斌
 * <p>
 * @date 2018/11/24 12:16
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class GenerateRegisterJpushAsyncTask extends AsyncTask<Context, Void, Context> {

    private static final String TAG = GenerateRegisterJpushAsyncTask.class.getSimpleName();

    @Override
    protected Context doInBackground(Context... contexts) {
        // 如果被取消了，直接退出
        if (isCancelled()) {
            return (null);
        } else {
            try {
                Thread.sleep(1000);
                //Toast.makeText(contexts[0], "正在注册消息服务...", Toast.LENGTH_LONG).show();
                if (null == SaveGetKeyActivity.registrationID) {
                    SaveGetKeyActivity.registrationID = JPushInterface.getRegistrationID(contexts[0]);
                    Logger.i(TAG, "获得用户 - registrationID - " + SaveGetKeyActivity.registrationID);
                }
                /*Toast.makeText(contexts[0], "注册消息服务成功 - registrationID - "
                        + MainActivity.registrationID, Toast.LENGTH_LONG).show();*/
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return (null);
    }

    @Override
    protected void onPostExecute(Context context) {
        super.onPostExecute(context);
        if (null == SaveGetKeyActivity.registrationID) {
            Toast.makeText(context, "系统注册异常，暂时无法使用", Toast.LENGTH_LONG).show();
        }
    }
}
