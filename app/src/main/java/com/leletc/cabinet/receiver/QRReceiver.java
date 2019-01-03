package com.leletc.cabinet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leletc.cabinet.client.SaveGetKeyActivity;
import com.leletc.cabinet.listener.QrListener;

import org.apache.commons.lang.StringUtils;

/**
 * 功能描述：二维码扫码接收器
 * <p>
 * 作者：李斌
 * <p>
 * 日期：2018/10/27 16:33
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class QRReceiver extends BroadcastReceiver {

    private QrListener listener;

    /**
     * 设置监听器对象
     *
     * @param qrListener
     */
    public void setQrListener(QrListener qrListener) {
        this.listener = qrListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SaveGetKeyActivity.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
            String message = intent.getStringExtra(SaveGetKeyActivity.KEY_MESSAGE);
            String extras = intent.getStringExtra(SaveGetKeyActivity.KEY_EXTRAS);
            StringBuilder showMsg = new StringBuilder();
            showMsg.append("{").append(SaveGetKeyActivity.KEY_MESSAGE + " : " + message + "\n");
            if (!StringUtils.isEmpty(extras)) {
                showMsg.append(SaveGetKeyActivity.KEY_EXTRAS + " : " + extras + "\n");
            }
            showMsg.append("}");
            // 处理消息
            try {
                listener.onReceiveQrData(showMsg.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
