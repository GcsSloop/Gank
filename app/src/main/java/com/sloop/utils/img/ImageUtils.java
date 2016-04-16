package com.sloop.utils.img;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <ul type="disc">
 * <li>Author: Sloop</li>
 * <li>Version: v1.0.0</li>
 * <li>Date: 2016/3/8</li>
 * <li>Copyright (c) 2015 GcsSloop</li>
 * <li><a href="http://weibo.com/GcsSloop" target="_blank">WeiBo</a>      </li>
 * <li><a href="https://github.com/GcsSloop" target="_blank">GitHub</a>   </li>
 * </ul>
 */
public class ImageUtils {


    /**
     * 保存图片到内存卡并通知图库更新
     * <p/>
     * 请注意添加内存卡读写权限
     *
     * @param context 上下文
     * @param bitmap  要保存的图片
     * @param dir     保存图片的文件夹路径(可缺省)
     * @param imgName 文件名(可缺省)
     */
    public static boolean saveImageToGallery(@NonNull Context context, @NonNull Bitmap bitmap, String dir, String imgName) {

        // 1.保存图片

        File imgDir = getCorrectDir(dir);
        String fileName = getCorrectName(imgName);
        File file = new File(imgDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);   // 默认压缩为png格式
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // 2.通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));

        return true;
    }


    /**
     * 获取正确的图片保存文件夹
     *
     * @param dir 文件夹
     * @return 文件夹的file对象
     */
    private static File getCorrectDir(String dir) {
        // 如果没有指定dir 则默认为/sdcard/Picture/Sl
        if (TextUtils.isEmpty(dir)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Sloop";
        }

        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        return fileDir;
    }


    /**
     * 获取正确的文件名
     *
     * @param fileName 文件名
     * @param exName   扩展名
     * @return
     */
    private static String getCorrectName(String fileName) {
        // 没有指定文件名则用当前时间作为文件名
        if (TextUtils.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + "";
        }

        return fileName + ".png";
    }

}
