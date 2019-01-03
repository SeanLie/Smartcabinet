package com.leletc.cabinet.task;

import java.util.Map;

/**
 * 功能描述：设备响应回调接口
 * <p>
 *
 * @author：李斌 <p>
 * @date 2018/10/30 12:19
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public interface IDeviceCallback {

    /**
     * 响应回调
     *
     * @param type   类型
     * @param msgMap 消息Map
     */
    void onDeviceCallback(int type, Map<Object, Object> msgMap);

}
