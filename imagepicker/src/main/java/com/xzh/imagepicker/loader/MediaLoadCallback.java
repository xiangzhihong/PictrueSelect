package com.xzh.imagepicker.loader;



import com.xzh.imagepicker.bean.MediaFolder;

import java.util.List;


public interface MediaLoadCallback {
    void loadMediaSuccess(List<MediaFolder> mediaFolderList);
}
