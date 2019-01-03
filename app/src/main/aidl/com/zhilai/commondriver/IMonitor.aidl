// IMonitor.aidl
package com.zhilai.commondriver;

// Declare any non-default types here with import statements

interface IMonitor {

    /**
     * 主灯箱控制
     * @param onnOff  1开  0关
     *
     * @return json数据
     *          字段               类型          描述
     *          "result"          boolean       操作结果
     *          "msg"             String        当result=false时，此字段是错误信息
     */
    String majorLamp(int onnOff);

    /**
     * 辅灯箱控制
     * @param onnOff  1开  0关
     *
     * @return json数据
     *          字段               类型          描述
     *          "result"          boolean       操作结果
     *          "msg"             String        当result=false时，此字段是错误信息
     */
    String minorLamp(int onnOff);

}

