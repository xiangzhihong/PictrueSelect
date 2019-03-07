package com.xzh.imagepicker.task;

import android.content.Context;


import com.xzh.imagepicker.bean.MediaFile;
import com.xzh.imagepicker.loader.ImageScanner;
import com.xzh.imagepicker.loader.MediaHandler;
import com.xzh.imagepicker.loader.MediaLoadCallback;

import java.util.ArrayList;

/**
 * 媒体库扫描任务（图片）
 */
public class ImageLoadTask implements Runnable {

    private Context mContext;
    private ImageScanner mImageScanner;
    private MediaLoadCallback mMediaLoadCallback;

    public ImageLoadTask(Context context, MediaLoadCallback mediaLoadCallback) {
        this.mContext = context;
        this.mMediaLoadCallback = mediaLoadCallback;
        mImageScanner = new ImageScanner(context);
    }

    @Override
    public void run() {
        //存放所有照片
        ArrayList<MediaFile> imageFileList = new ArrayList<>();
        if (mImageScanner != null) {
            imageFileList = mImageScanner.queryMedia();
        }
        if (mMediaLoadCallback != null) {
            mMediaLoadCallback.loadMediaSuccess(MediaHandler.getImageFolder(mContext, imageFileList));
        }


    }

}
