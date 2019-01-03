package com.leletc.cabinet.listener;

/**
 * 功能描述：二维码扫码监听器
 * <p>
 * 作者：李斌
 * <p>
 * 日期：2018/10/27 16:37
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public interface QrListener {

    /**
     * 二维码扫码接收结果回调
     *
     * @param data 返回数据
     */
    void onReceiveQrData(String data) throws Exception;

}
