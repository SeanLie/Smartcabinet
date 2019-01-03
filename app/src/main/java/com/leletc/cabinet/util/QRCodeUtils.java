package com.leletc.cabinet.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 功能描述：二维码工具类
 * <p>
 * 作者：李斌
 * <p>
 * 日期：2018/10/27 18:47
 * <p>
 * 修改记录：修改内容 修改人 修改时间
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Copyright © 2016-2018, 深圳市乐乐网络科技有限公司, All Rights Reserved
 * <p>
 */
public class QRCodeUtils {

    /**
     * 黑点颜色
     */
    private static final int BLACK = 0xFF000000;
    /**
     * 白色
     */
    private static final int WHITE = 0xFFFFFFFF;
    /**
     * 正方形二维码宽度
     */
    //private static final int WIDTH = 200;
    /**
     * LOGO宽度值,最大不能大于二维码20%宽度值,大于可能会导致二维码信息失效
     */
    //private static final int LOGO_WIDTH_MAX = WIDTH / 5;
    /**
     *LOGO宽度值,最小不能小于二维码10%宽度值,小于影响Logo与二维码的整体搭配
     */
    //private static final int LOGO_WIDTH_MIN = WIDTH / 10;

    // 用于设置QR二维码参数
    /*private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
        private static final long serialVersionUID = 1L;
        {
            // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//设置容错级别,H为最高
            put(EncodeHintType.MAX_SIZE, LOGO_WIDTH_MAX);// 设置图片的最大值
            put(EncodeHintType.MIN_SIZE, LOGO_WIDTH_MIN);// 设置图片的最小值
            // 设置编码方式
            put(EncodeHintType.CHARACTER_SET, "utf-8");
            put(EncodeHintType.MARGIN, 0);//设置白色边距值
        }
    };*/

    /**
     * 创建带Logo的二维码
     *
     * @param content 内容
     * @param logoBitmap logo图片
     * @return
     * @throws WriterException
     */
    public static Bitmap createQRCode(String content,  int qrWidth, int qrHeight, Bitmap logoBitmap)
            throws WriterException {
        int logoWidth = logoBitmap.getWidth();
        int logoHeight = logoBitmap.getHeight();
        /*
         * LOGO宽度值,最大不能大于二维码20%宽度值,大于可能会导致二维码信息失效
         */
        int LOGO_WIDTH_MAX = qrWidth / 5;
        /*
         *LOGO宽度值,最小不能小于二维码10%宽度值,小于影响Logo与二维码的整体搭配
         */
        int LOGO_WIDTH_MIN = qrHeight / 10;
        int logoHaleWidth = logoWidth >= qrWidth ? LOGO_WIDTH_MIN : LOGO_WIDTH_MAX;
        int logoHaleHeight = logoHeight >= qrHeight ? LOGO_WIDTH_MIN : LOGO_WIDTH_MAX;
        // 将logo图片按Matrix设置的信息缩放
        Matrix logoMatrix = new Matrix();
        float sx = (float) logoHaleWidth / logoWidth;
        float sy = (float) logoHaleHeight / logoHeight;
        logoMatrix.setScale(sx, sy);// 设置缩放信息
        Bitmap newLogoBitmap = Bitmap.createBitmap(logoBitmap, 0, 0, logoWidth,
                logoHeight, logoMatrix, false);
        int newLogoWidth = newLogoBitmap.getWidth();
        int newLogoHeight = newLogoBitmap.getHeight();
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
            private static final long serialVersionUID = 1L;
            {
                // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//设置容错级别,H为最高
                put(EncodeHintType.MAX_SIZE, LOGO_WIDTH_MAX);// 设置图片的最大值
                put(EncodeHintType.MIN_SIZE, LOGO_WIDTH_MIN);// 设置图片的最小值
                // 设置编码方式
                put(EncodeHintType.CHARACTER_SET, "utf-8");
                put(EncodeHintType.MARGIN, 0);//设置白色边距值
            }
        };
        BitMatrix qrMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,
                qrWidth, qrHeight, hints);
        int width = qrMatrix.getWidth();
        int height = qrMatrix.getHeight();
        int halfW = width / 2;
        int halfH = height / 2;
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                /*
                 * 取值范围,可以画图理解下
                 * halfW + newLogoWidth / 2 - (halfW - newLogoWidth / 2) = newLogoWidth
                 * halfH + newLogoHeight / 2 - (halfH - newLogoHeight) = newLogoHeight
                 */
                // 该位置用于存放图片信息
                if (x > halfW - newLogoWidth / 2 && x < halfW + newLogoWidth / 2
                        && y > halfH - newLogoHeight / 2 && y < halfH + newLogoHeight / 2) {
                    /*
                     *  记录图片每个像素信息
                     *  halfW - newLogoWidth / 2 < x < halfW + newLogoWidth / 2
                     *  --> 0 < x - halfW + newLogoWidth / 2 < newLogoWidth
                     *   halfH - newLogoHeight / 2  < y < halfH + newLogoHeight / 2
                     *   -->0 < y - halfH + newLogoHeight / 2 < newLogoHeight
                     *   刚好取值newLogoBitmap。getPixel(0-newLogoWidth,0-newLogoHeight);
                     */
                    pixels[y * width + x] = newLogoBitmap.getPixel(
                            x - halfW + newLogoWidth / 2, y - halfH + newLogoHeight / 2);
                } else {
                    pixels[y * width + x] = qrMatrix.get(x, y) ? BLACK : WHITE;// 设置信息
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 创建二维码位图
     *
     * @param content 字符串内容(支持中文)
     * @param width 位图宽度(单位:px)
     * @param height 位图高度(单位:px)
     * @return
     */
    @Nullable
    public static Bitmap createQRCodeBitmap(String content, int width, int height){
        return createQRCodeBitmap(content, width, height, "UTF-8",
                "H", "2", Color.BLACK, Color.WHITE);
    }

    /**
     * 创建二维码位图 (支持自定义配置和自定义样式)
     *
     * @param content 字符串内容
     * @param width 位图宽度,要求>=0(单位:px)
     * @param height 位图高度,要求>=0(单位:px)
     * @param character_set 字符集/字符转码格式 (支持格式:{@link CharacterSetECI })。
     *                      传null时,zxing源码默认使用 "ISO-8859-1"
     * @param error_correction 容错级别 (支持级别:{@link ErrorCorrectionLevel })。
     *                         传null时,zxing源码默认使用 "L"
     * @param margin 空白边距 (可修改,要求:整型且>=0), 传null时,zxing源码默认使用"4"。
     * @param color_black 黑色色块的自定义颜色值
     * @param color_white 白色色块的自定义颜色值
     * @return
     */
    @Nullable
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            @Nullable String character_set,
                                            @Nullable String error_correction,
                                            @Nullable String margin,
                                            @ColorInt int color_black,
                                            @ColorInt int color_white){
        /** 1.参数合法性判断 */
        if(TextUtils.isEmpty(content)){ // 字符串内容判空
            return null;
        }
        if(width < 0 || height < 0){ // 宽和高都需要>=0
            return null;
        }
        try {
            /** 2.设置二维码相关配置,生成BitMatrix(位矩阵)对象 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            if(!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set); // 字符转码格式设置
            }
            if(!TextUtils.isEmpty(error_correction)){
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction); // 容错级别设置
            }
            if(!TextUtils.isEmpty(margin)){
                hints.put(EncodeHintType.MARGIN, margin); // 空白边距设置
            }
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE,
                    width, height, hints);
            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    if(bitMatrix.get(x, y)){
                        pixels[y * width + x] = color_black; // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white; // 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,之后返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成带白色边框的Logo
     *
     * @param bgBitmap
     * @param logoBitmap
     * @return
     */
    public static Bitmap generateLogo(Bitmap bgBitmap, Bitmap logoBitmap) {
        int bgWidth = bgBitmap.getWidth();
        int bgHeigh = bgBitmap.getHeight();
        //通过ThumbnailUtils压缩原图片，并指定宽高为背景图的3/4
        logoBitmap = ThumbnailUtils.extractThumbnail(logoBitmap,bgWidth*3/4,
                bgHeigh*3/4, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        Bitmap cvBitmap = Bitmap.createBitmap(bgWidth, bgHeigh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cvBitmap);
        // 开始合成图片
        canvas.drawBitmap(bgBitmap, 0, 0, null);
        canvas.drawBitmap(logoBitmap,(bgWidth - logoBitmap.getWidth()) /2,
                (bgHeigh - logoBitmap.getHeight()) / 2, null);
        // 保存
        canvas.save();
        canvas.restore();
        if(cvBitmap.isRecycled()){
            cvBitmap.recycle();
        }
        return cvBitmap;
    }

    /**
     * 保存图片
     *
     * @param bitmap
     * @param imageFile
     * @return
     */
    public static Uri saveBitmap(Bitmap bitmap, File imageFile){
        Uri imageFileUri = Uri.fromFile(imageFile);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(imageFile);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
        }
        return imageFileUri;
    }

}
