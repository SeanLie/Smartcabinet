package com.leletc.cabinet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.leletc.cabinet.client.SaveGetKeyActivity;
import com.leletc.cabinet.client.TestActivity;
import com.leletc.cabinet.log.Logger;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 功能描述：消息接收器
 * <p>
 * 作者：李斌
 * <p>
 * 日期：2018/10/29 12:27
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class MessageReceiver extends BroadcastReceiver {

    private static final String TAG = "Leletc - " + MessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (null == bundle) {
                Logger.e(TAG, "未接收到任何消息");
                return;
            }
            Logger.d(TAG, "[MessageReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Logger.d(TAG, "[MessageReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Logger.d(TAG, "[MessageReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processMessage(context, bundle);
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Logger.d(TAG, "[MessageReceiver] 接收到推送下来的通知");
                int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Logger.d(TAG, "[MessageReceiver] 接收到推送下来的通知的ID: " + notificationId);
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Logger.d(TAG, "[MessageReceiver] 用户点击打开了通知");
                //打开自定义的Activity
                Intent i = new Intent(context, TestActivity.class);
                i.putExtras(bundle);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Logger.d(TAG, "[MessageReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Logger.w(TAG, "[MessageReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Logger.d(TAG, "[MessageReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理二维码消息
     *
     * @param context 上下文
     * @param bundle 绑定
     */
    private void processMessage(Context context, Bundle bundle) {
        if (SaveGetKeyActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(SaveGetKeyActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(SaveGetKeyActivity.KEY_MESSAGE, message);
            if (!StringUtils.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(SaveGetKeyActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        }
    }

    /**
     * 打印所有的 intent extra 数据
     *
     * @param bundle 绑定的参数
     * @return String
     */
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            switch (key) {
                case JPushInterface.EXTRA_NOTIFICATION_ID:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
                    break;
                case JPushInterface.EXTRA_CONNECTION_CHANGE:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
                    break;
                case JPushInterface.EXTRA_EXTRA:
                    if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                        Logger.i(TAG, "This message has no Extra data");
                        continue;
                    }
                    try {
                        JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                        Iterator<String> it = json.keys();

                        while (it.hasNext()) {
                            String myKey = it.next();
                            sb.append("\nkey:").append(key).append(", value: [").append(myKey).append(
                                    " - ").append(json.optString(myKey)).append("]");
                        }
                    } catch (JSONException e) {
                        Logger.e(TAG, "Get message extra JSON error!");
                    }
                    break;
                default:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.get(key));
                    break;
            }
        }
        return sb.toString();
    }

}
