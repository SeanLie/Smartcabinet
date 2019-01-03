package com.leletc.cabinet.task;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.Map;

/**
 * 功能描述：刷新状态
 * <p>
 *
 * @author 李斌
 * <p>
 * @date 2018/10/30 12:54
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public abstract class OnViewRefreshStatus<VIEW extends View, VALUE> {

    private long key;

    public OnViewRefreshStatus(long key) {
        this.key = key;
    }

    /**
     * 更新
     *
     * @param view
     * @param map
     */
    public void update(final @NonNull VIEW view, Map<Long, VALUE> map) {
        final VALUE value = map.get(key);
        if (value == null) {
            return;
        }
        refreshStatus(view, value);
    }

    /**
     * 刷新状态
     *
     * @param view
     * @param value
     */
    public abstract void refreshStatus(VIEW view, VALUE value);

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

}
