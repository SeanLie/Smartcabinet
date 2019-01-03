// ILockerInterface.aidl
package com.zhilai.commondriver;

// Declare any non-default types here with import statements

import com.zhilai.commondriver.IScannerCallback;

interface ILockerInterface {

    /**
     *
     * 获取基础信息
     *
     */
    String getInfo();

    /**
     *
     * 开格口
     * @param boxID 格口编号  A01、B19、C05...
     *                      也可以是1、2、3、4...
     */
    String openLocker(String boxId);


    /**
     *
     * 查询格口门的状态
     * @param boxID 格口编号  A01、B19、C05...
     *                      也可以是1、2、3、4...
     */
    String getLocker(String boxId);


    /**
     * 查询格口内红外感应的状态
     * @param boxID 格口编号  A01、B19、C05...
     *                      也可以是1、2、3、4...
     */
    String getOccupy(String boxId);

    /**
     *
     * 打开中控门格口
     */
    String openPanelLocker();

    /**
     *
     * 获取中控门格口门的状态
     */
    String getPanelLocker();

    /**
     * 主灯箱开关
     * @param onOff 1:开  0:关
     *
     */
    String majorLight(int onOff);

    /**
     * 副灯箱开关
     * @param onOff 1:开  0:关
     *
     */
    String minorLight(int onOff);

    /**
     * 重启所有开门板
     *
     */
    String rebootPcb();

    /**
     * 重启系统
     * @param onOff 1:开  0:关
     *
     */
    String rebootSystem();

    /**
     * 开启扫描器
     * @param scannerMode 扫描器模式 触发模式:0  常亮模式:1
     * @param timeOutMills 超时时间,单位毫秒.(只有在scannerMode=0时有效)
     * @param IScannerCallback 扫描数据回调函数
     *
     */
    String turnOnScanner(int scannerMode,int timeOutMills,IScannerCallback callback);


    /**
     * 关闭扫描器
     *
     */
    String turnOffScanner();

}
