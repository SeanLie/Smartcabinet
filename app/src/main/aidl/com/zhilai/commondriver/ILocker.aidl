// ILocker.aidl
package com.zhilai.commondriver;

// Declare any non-default types here with import statements

interface ILocker {

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    /**
     * 开门
     * @param boxNo 1 、2、3 ...
     * @return json数据
     *          字段               类型          描述
     *          "result"          boolean       操作结果
     *          "msg"             String        当result=false时，此字段是错误信息
     */
    String openLocker(int boxNo);

    /**
     * 获取门状态
     * @param boxNo 1 、2、3 ...
     * @return json数据
     *          字段               类型          描述
     *          "result"          boolean       操作结果
     *          "opened"          boolean       在result=true时，此字段是门状态信息:true,代表门开   false门关
     *          "msg"             String        当result=false时，此字段是错误信息
     */
    String getLocker(int boxNo);

    /**
     * 获取物品状态 暂时不开放此接口
     * @param boxNo 1 、2、3 ...
     */
    String getOccupy(int boxNo);

}