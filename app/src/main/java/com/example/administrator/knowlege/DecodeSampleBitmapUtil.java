package com.example.administrator.knowlege;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by Administrator on 2016/10/18.
 */
public final class DecodeSampleBitmapUtil {
    private DecodeSampleBitmapUtil() {
    }

    ;

    public static Bitmap loadBitmapWithScale(
            File imageFile,
            int reqWith,
            int reqHeight
    ) {
        Bitmap ret = null;
        if (imageFile != null && imageFile.exists() && imageFile.canRead()) {
            if (reqWith > 0 && reqHeight > 0) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                String filePath = imageFile.getAbsolutePath();
                BitmapFactory.decodeFile(filePath, options);
                options.inSampleSize = calInsampleSize(options, reqWith, reqHeight);
                options.inJustDecodeBounds = false;
//                // TODO: 2016/10/18 inPreferredConfig 降低内存
//                所有的png支持透明
//                所有的jpg不支持透明
                String type = options.outMimeType;
                if (type != null) {
//                    根据它判断图片文件格式
                    if (type.endsWith("png")) {
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    } else if (type.endsWith("jpeg")) {
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                    }
                }


                BitmapFactory.decodeFile(filePath, options);
            }
        }

        return ret;
    }

    private static int calInsampleSize(
            BitmapFactory.Options options,
            int reqWith,
            int reqHeight
    ) {
        int inSampleSize = 1;
        int height = options.outHeight;
        int width = options.outWidth;
        if (height>reqHeight||width>reqWith){
            int halW = width >> 1;
            int halH = height >> 1;
            while ((halW / inSampleSize) > reqWith && (halH / inSampleSize > reqHeight)) {
                inSampleSize /= 2;
            }
        }


        return inSampleSize;
    }
}
