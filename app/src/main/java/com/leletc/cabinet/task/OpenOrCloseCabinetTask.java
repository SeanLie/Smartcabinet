package com.leletc.cabinet.task;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.leletc.cabinet.client.SaveGetKeyActivity;
import com.leletc.cabinet.task.bo.NetReqBO;
import com.leletc.cabinet.util.HttpRequestUtil;
import com.leletc.cabinet.util.SystemConstants;

/**
 * 功能描述：打开、关闭箱门的后台任务
 * <p>
 *
 * @author 李斌
 * <p>
 * @date 2018/11/02 20:34
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class OpenOrCloseCabinetTask extends AsyncTask<NetReqBO, Void, String> {

    @Override
    protected String doInBackground(NetReqBO... netReqBOs) {
        String params = JSON.toJSONString(netReqBOs[0]);
        // 请求服务器，进行关闭箱门处理
        String url = SystemConstants.getUrlSchema() + "://" + SystemConstants.getUrlHost() + ":"
                + SystemConstants.getUrlPort() + SaveGetKeyActivity.CLOSE_BOX_REQUEST_URL;
        // 请求
        return HttpRequestUtil.sendPost(url, params);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}
