package com.leletc.cabinet.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;

import java.io.IOException;
import java.io.InputStream;

/**
 * 功能描述：基础工具类
 * <p>
 * 作者：李斌
 * <p>
 * 日期：2018/10/25 01:02
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class BaseUtils {

    /**
     * 获得资源文件绝对路径
     * @param context 上下文
     * @param id 文件ID
     * @return 绝对路径
     */
    public static String getResourcesUri(Context context, @DrawableRes int id) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }

    /**
     * 通过读取Assets目录下面的文件,将其转为Bitmap对象
     *
     * @param context 上下文
     * @param fileName 文件名
     * @return
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName){
        Bitmap image = null;
        AssetManager manager = context.getResources().getAssets();
        try {
            InputStream inputStream = manager.open(fileName);
            image = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}
