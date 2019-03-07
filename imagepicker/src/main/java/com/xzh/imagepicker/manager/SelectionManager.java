package com.xzh.imagepicker.manager;

import com.xzh.imagepicker.bean.MediaFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体选择集合管理类
 */
public class SelectionManager {

    private static volatile SelectionManager mSelectionManager;
    private List<MediaFile> mSelectImages = new ArrayList<>();
    private int mMaxCount = 1;

    public static SelectionManager getInstance() {
        if (mSelectionManager == null) {
            synchronized (SelectionManager.class) {
                if (mSelectionManager == null) {
                    mSelectionManager = new SelectionManager();
                }
            }
        }
        return mSelectionManager;
    }


    public void setMaxCount(int maxCount) {
        this.mMaxCount = maxCount;
    }


    public int getMaxCount() {
        return this.mMaxCount;
    }

    //获取当前所选图片集合path
    public List<MediaFile> getSelects() {
        return mSelectImages;
    }

    //添加图片到选择集合
    public boolean addImageToSelectList(MediaFile mediaFile) {
        if (mSelectImages.contains(mediaFile)) {
            return mSelectImages.remove(mediaFile);
        } else {
            if (mSelectImages.size() < mMaxCount) {
                return mSelectImages.add(mediaFile);
            } else {
                return false;
            }
        }
    }

    //添加图片到选择集合
    public void addImagePathsToSelectList(List<MediaFile> mediaFiles) {
        if (mediaFiles != null) {
            for (int i = 0; i < mediaFiles.size(); i++) {
                MediaFile mediaFile = mediaFiles.get(i);
                if (!mSelectImages.contains(mediaFile) && mSelectImages.size() < mMaxCount) {
                    mSelectImages.add(mediaFile);
                }
            }
        }
    }

    //判断当前图片是否被选择
    public boolean isImageSelect(MediaFile mediaFile) {
        if (mSelectImages.contains(mediaFile)) {
            return true;
        } else {
            return false;
        }
    }


    //获取选中的图片在图库列表的位置
    public int getSelectImagePosition(List<MediaFile> mMediaFileList,MediaFile mediaFile) {
        if (mMediaFileList.contains(mediaFile)){
            for(int i=0;i<mMediaFileList.size();i++){
                if (mMediaFileList.get(i).equals(mediaFile)){
                    return i;
                }
            }
        }
        return 0;
    }

    //是否还可以继续选择图片
    public boolean isCanChoose() {
        if (getSelects().size() < mMaxCount) {
            return true;
        }
        return false;
    }

    //清除已选图片
    public void removeAll() {
        mSelectImages.clear();
    }

}
