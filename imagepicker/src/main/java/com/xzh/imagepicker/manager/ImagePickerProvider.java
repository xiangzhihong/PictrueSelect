package com.xzh.imagepicker.manager;

import android.content.Context;
import android.support.v4.content.FileProvider;

/**
 * 自定义Provider，避免上层发生provider冲突
 */
public class ImagePickerProvider extends FileProvider {

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".provider";
    }

}
