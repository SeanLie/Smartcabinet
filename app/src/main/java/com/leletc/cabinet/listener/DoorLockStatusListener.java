package com.leletc.cabinet.listener;

import com.leletc.cabinet.task.bo.NetReqBO;

/**
 * 功能描述：箱门锁状态监听器
 * <p>
 *
 * @author 李斌
 * <p>
 * @date 2018/11/20 11:52
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public interface DoorLockStatusListener {

    /**
     * 接收到结果时处理
     *
     * @param req 结果
     */
    void onReceiveLockStatus(NetReqBO req);

}
