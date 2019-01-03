package com.leletc.cabinet.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * 功能描述：系统常量类
 * <p>
 * 作者：李斌
 * <p>
 * 日期：2018/10/25 01:14
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class SystemConstants {

    private static final Properties properties = new Properties();

    /** 测试环境 */
    public static final String ENV_TEST = "1";
    /** 生产环境 */
    public static final String ENV_ONLINE = "2";

    /**
     * URL前缀：http或https
     */
    public static String urlSchema = "http";
    /**
     * URL的IP地址
     */
    public static String urlHost = "127.0.0.1";
    /**
     * URL的端口
     */
    public static String urlPort = "8090";

    /**
     * 获得当前环境
     *
     * @return
     */
    public static String getEnv() {
        return getProperty("env.config");
    }

    /**
     * 获得URL前缀
     *
     * @return
     */
    public static String getUrlSchema() {
        urlSchema = getProperty("server.url_schema");
        return urlSchema;
    }

    /**
     * 获得URL IP
     *
     * @return
     */
    public static String getUrlHost() {
        urlHost = getProperty("server.url_host");
        return urlHost;
    }

    /**
     * 获得URL端口
     *
     * @return
     */
    public static String getUrlPort() {
        urlPort = getProperty("server.url_port");
        return urlPort;
    }

    /**
     * 获得柜子的编号
     *
     * @return
     */
    public static String getCabinetNo() {
        return getProperty("cabinet.no");
    }

    /**
     * 加载资源文件
     *
     * @param context Android上下文对象
     * @return Properties对象
     */
    public static Properties loadProperties(Context context) {
        try {
            InputStream is = context.getAssets().open("systemConfig.properties");
            properties.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }

    /**
     * 取出Property。
     */
    private static String getValue(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        return properties.getProperty(key);
    }

    /**
     * 取出String类型的Property,如果都為Null则抛出异常.
     */
    public static String getProperty(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return value;
    }

    /**
     * 取出String类型的Property.如果都為Null則返回Default值.
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 取出Integer类型的Property.如果都為Null或内容错误则抛出异常.
     */
    public static Integer getInteger(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Integer.valueOf(value);
    }

    /**
     * 取出Integer类型的Property.如果都為Null則返回Default值，如果内容错误则抛出异常
     */
    public static Integer getInteger(String key, Integer defaultValue) {
        String value = getValue(key);
        return value != null ? Integer.valueOf(value) : defaultValue;
    }

    /**
     * 取出Long类型的Property.如果都為Null或内容错误则抛出异常.
     */
    public static Long getLong(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Long.valueOf(value);
    }

    /**
     * 取出Long类型的Property.如果都為Null則返回Default值，如果内容错误则抛出异常
     */
    public static Long getLong(String key, Long defaultValue) {
        String value = getValue(key);
        return value != null ? Long.valueOf(value) : defaultValue;
    }

    /**
     * 取出Double类型的Property.如果都為Null或内容错误则抛出异常.
     */
    public static Double getDouble(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Double.valueOf(value);
    }

    /**
     * 取出Double类型的Property.如果都為Null則返回Default值，如果内容错误则抛出异常
     */
    public static Double getDouble(String key, Integer defaultValue) {
        String value = getValue(key);
        return value != null ? Double.valueOf(value) : defaultValue;
    }

    /**
     * 取出Boolean类型的Property.如果都為Null抛出异常,如果内容不是true/false则返回false.
     */
    public static Boolean getBoolean(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Boolean.valueOf(value);
    }

    /**
     * 取出Boolean类型的Propert.如果都為Null則返回Default值,如果内容不为true/false则返回false.
     */
    public static Boolean getBoolean(String key, boolean defaultValue) {
        String value = getValue(key);
        return value != null ? Boolean.valueOf(value) : defaultValue;
    }

}
