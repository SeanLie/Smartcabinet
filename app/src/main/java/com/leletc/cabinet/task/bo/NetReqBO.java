package com.leletc.cabinet.task.bo;

/**
 * 功能描述：网络请求对象
 * <p>
 *
 * @author 李斌
 * <p>
 * @date 2018/11/02 20:36
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class NetReqBO {

    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 业务类型(1:存钥匙，2:取钥匙)
     */
    private Integer bizType;
    /**
     * 柜子标识码
     */
    private String token;
    /**
     * 格口号
     */
    private String boxNo;
    /**
     * 柜子编号
     */
    private String cabinetNo;
    //private String wxUserToken;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(String boxNo) {
        this.boxNo = boxNo;
    }

    public String getCabinetNo() {
        return cabinetNo;
    }

    public void setCabinetNo(String cabinetNo) {
        this.cabinetNo = cabinetNo;
    }
/*public String getWxUserToken() {
        return wxUserToken;
    }

    public void setWxUserToken(String wxUserToken) {
        this.wxUserToken = wxUserToken;
    }*/

}
