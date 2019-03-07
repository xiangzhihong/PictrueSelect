package com.xzh.imagepicker;

import android.app.Activity;
import android.content.Intent;

import com.xzh.imagepicker.activity.ImagePickerActivity;
import com.xzh.imagepicker.bean.MediaFile;
import com.xzh.imagepicker.manager.ConfigManager;
import com.xzh.imagepicker.utils.ImageLoader;

import java.util.ArrayList;

public class ImagePicker {

    public static final String EXTRA_SELECT_IMAGES = "selectItems";
    private static volatile ImagePicker mImagePicker;


    public static ImagePicker getInstance() {
        if (mImagePicker == null) {
            synchronized (ImagePicker.class) {
                if (mImagePicker == null) {
                    mImagePicker = new ImagePicker();
                }
            }
        }
        return mImagePicker;
    }


    //设置标题
    public ImagePicker setTitle(String title) {
        ConfigManager.getInstance().setTitle(title);
        return mImagePicker;
    }

    //是否展示图片
    public ImagePicker showImage(boolean showImage) {
        ConfigManager.getInstance().setShowImage(showImage);
        return mImagePicker;
    }

    //是否展示视频
    public ImagePicker showVideo(boolean showVideo) {
        ConfigManager.getInstance().setShowVideo(showVideo);
        return mImagePicker;
    }


    //图片最大选择数
    public ImagePicker setMaxCount(int maxCount) {
        ConfigManager.getInstance().setMaxCount(maxCount);
        return mImagePicker;
    }


    //设置图片加载器
    public ImagePicker setImageLoader(ImageLoader imageLoader) {
        ConfigManager.getInstance().setImageLoader(imageLoader);
        return mImagePicker;
    }

    //设置图片选择历史记录
    public ImagePicker setImages(ArrayList<MediaFile> images) {
        ConfigManager.getInstance().setImages(images);
        return mImagePicker;
    }


    public void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
