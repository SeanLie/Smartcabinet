package com.leletc.cabinet.task;

import android.os.AsyncTask;

import com.leletc.cabinet.client.MainActivity;
import com.leletc.cabinet.client.SaveGetKeyActivity;
import com.leletc.cabinet.listener.DoorLockStatusListener;
import com.leletc.cabinet.log.Logger;
import com.leletc.cabinet.task.bo.NetReqBO;
import com.leletc.cabinet.util.SystemConstants;

/**
 * 功能描述：获得箱门状态异步任务
 * <p>
 *
 * @author 李斌
 * <p>
 * @date 2018/11/20 11:22
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class DoorGetLockerAsyncTask extends AsyncTask<NetReqBO, NetReqBO, NetReqBO> {

    private static final String TAG = DoorGetLockerAsyncTask.class.getSimpleName() + " - ";

    private DoorLockStatusListener lockStatusListener;
    private boolean running;

    public void setLockStatusListener(DoorLockStatusListener lockStatusListener) {
        this.lockStatusListener = lockStatusListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Logger.i(TAG, "开始执行获取箱门状态...");
    }

    public boolean isRunning(){
        return running;
    }

    @Override
    protected NetReqBO doInBackground(NetReqBO... netReqBOs) {
        this.running = true;
        while (true) {
            boolean locker = true;
            if (SystemConstants.getEnv().equals(SystemConstants.ENV_TEST)) {
                locker = false;
                try {
                    // 测试时设置为5秒
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (SystemConstants.getEnv().equals(SystemConstants.ENV_ONLINE)) {
                locker = MainActivity.getLocker(netReqBOs[0].getBoxNo());
                try {
                    // 正式环境下为200毫秒
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!locker) {
                // 设置箱门状态为关
                SaveGetKeyActivity.setIsCurrentDoorOpened(false);
                break;
            }
        }
        return netReqBOs[0];
    }

    @Override
    protected void onPostExecute(NetReqBO netReqBO) {
        super.onPostExecute(netReqBO);
        Logger.i(TAG, "获取箱门状态成功");
        try {
            lockStatusListener.onReceiveLockStatus(netReqBO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
