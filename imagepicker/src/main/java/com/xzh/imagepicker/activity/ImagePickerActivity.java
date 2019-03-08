package com.xzh.imagepicker.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xzh.imagepicker.ImagePicker;
import com.xzh.imagepicker.R;
import com.xzh.imagepicker.adapter.ImageFoldersAdapter;
import com.xzh.imagepicker.adapter.ImagePickerAdapter;
import com.xzh.imagepicker.bean.MediaFile;
import com.xzh.imagepicker.bean.MediaFolder;
import com.xzh.imagepicker.loader.MediaLoadCallback;
import com.xzh.imagepicker.manager.CommonExecutor;
import com.xzh.imagepicker.manager.ConfigManager;
import com.xzh.imagepicker.manager.SelectionManager;
import com.xzh.imagepicker.task.ImageLoadTask;
import com.xzh.imagepicker.task.MediaLoadTask;
import com.xzh.imagepicker.task.VideoLoadTask;
import com.xzh.imagepicker.utils.DataUtil;
import com.xzh.imagepicker.utils.PermissionUtil;
import com.xzh.imagepicker.view.FolderPopupWindow;

import java.util.ArrayList;
import java.util.List;

public class ImagePickerActivity extends AppCompatActivity implements ImagePickerAdapter.OnItemClickListener, ImageFoldersAdapter.OnImageFolderChangeListener {

    private String mTitle;
    private boolean isShowImage;
    private boolean isShowVideo;
    private int mMaxCount;

    private TextView mTvTitle;
    private TextView mTvCommit;
    private ImageView mIvBack;
    private TextView mTvPreview;
    private RecyclerView mRecyclerView;
    private TextView mTvFolders;
    private FolderPopupWindow mImageFolderPopupWindow;
    private RelativeLayout mRlBottom;

    private int horizontalCount = 4;
    private GridLayoutManager mGridLayoutManager;
    private ImagePickerAdapter mImagePickerAdapter;
    private List<MediaFile> mMediaFileList;
    private List<MediaFolder> mMediaFolderList;
    private static final int REQUEST_SELECT_IMAGES_CODE = 0x01;
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 0x03;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepicker);
        init();
    }

    private void init() {
        checkPermission();
        initConfig();
        initView();
        initRecycle();
        initClick();
    }

    //动态权限
    private void checkPermission() {
        boolean hasPermission = PermissionUtil.checkPermission(this);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA_CODE);
        } else {
            startScannerTask();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            if (grantResults.length >= 1) {
                int cameraResult = grantResults[0];//相机权限
                int sdResult = grantResults[1];//sd卡权限
                boolean cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                boolean sdGranted = sdResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                if (cameraGranted && sdGranted) {
                    //具有拍照权限，sd卡权限，开始扫描任务
                    startScannerTask();
                } else {
                    //没有权限
                    Toast.makeText(this, getString(R.string.permission_tip), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void startScannerTask() {
        Runnable mediaLoadTask = null;
        //照片、视频全部加载
        if (isShowImage && isShowVideo) {
            mediaLoadTask = new MediaLoadTask(this, new MediaLoader());
        }
        //只加载视频
        if (!isShowImage && isShowVideo) {
            mediaLoadTask = new VideoLoadTask(this, new MediaLoader());
        }
        //只加载图片
        if (isShowImage && !isShowVideo) {
            mediaLoadTask = new ImageLoadTask(this, new MediaLoader());
        }
        //不符合以上场景，采用照片、视频全部加载
        if (mediaLoadTask == null) {
            mediaLoadTask = new MediaLoadTask(this, new MediaLoader());
        }
        CommonExecutor.getInstance().execute(mediaLoadTask);
    }

    protected void initConfig() {
        mTitle = ConfigManager.getInstance().getTitle();
        isShowImage = ConfigManager.getInstance().isShowImage();
        isShowVideo = ConfigManager.getInstance().isShowVideo();
        mMaxCount = ConfigManager.getInstance().getMaxCount();
        SelectionManager.getInstance().setMaxCount(mMaxCount);
        //如果是多选模式，载入历史记录
        if (ConfigManager.getInstance().getSelectionMode() == ConfigManager.SELECT_MODE_MULTI) {
           ArrayList<MediaFile> mImages = ConfigManager.getInstance().getImages();
            if (mImages != null && mImages.size()>0) {
                SelectionManager.getInstance().addImagePathsToSelectList(mImages);
            }
        }
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_actionBar_back);
        mTvTitle = findViewById(R.id.tv_title);
        mTvCommit = findViewById(R.id.tv_commit);
        mTvPreview = findViewById(R.id.tv_preview);
        mRecyclerView = findViewById(R.id.rv_images);
        mTvFolders = findViewById(R.id.tv_folders);
        mRlBottom = findViewById(R.id.rl_bottom);

        if (TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText(getString(R.string.image_picker));
        } else {
            mTvTitle.setText(mTitle);
        }
    }

    private void initRecycle() {
        mGridLayoutManager = new GridLayoutManager(this, horizontalCount);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(50);

        mMediaFileList = new ArrayList<>();
        mImagePickerAdapter = new ImagePickerAdapter(this, mMediaFileList);
        mRecyclerView.setAdapter(mImagePickerAdapter);
        mImagePickerAdapter.setOnItemClickListener(this);
    }


    private void initClick() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectionManager.getInstance().removeAll();
                finish();
            }
        });
        //选择Folder
        mTvFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageFolderPopupWindow != null) {
                    setLightMode(0.7f);
                    mImageFolderPopupWindow.showAsDropDown(mRlBottom, 0, 0);
                }
            }
        });

        //发送
        mTvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitSelectPic();
            }
        });

        //预览
        mTvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpPreView();
            }
        });
    }

    //预览选中图片列表
    private void jumpPreView() {
        ArrayList<MediaFile> selectList = new ArrayList<>(SelectionManager.getInstance().getSelects());
        if (selectList != null&&selectList.size()>0) {
            DataUtil.getInstance().setMediaData(selectList);
            Intent intent = new Intent(this, ImagePreActivity.class);
            //获取选中列表的第一个作为默认位置
            intent.putExtra(ImagePreActivity.IMAGE_POSITION, 0);
            startActivityForResult(intent, REQUEST_SELECT_IMAGES_CODE);
        }
    }

    private void commitSelectPic() {
        ArrayList<MediaFile> list = new ArrayList<>(SelectionManager.getInstance().getSelects());
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePicker.EXTRA_SELECT_IMAGES, list);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        SelectionManager.getInstance().removeAll();
        finish();
    }

    @Override
    public void onImageFolderChange(View view, int position) {
        MediaFolder mediaFolder = mMediaFolderList.get(position);
        //更新当前文件夹名
        String folderName = mediaFolder.getFolderName();
        if (!TextUtils.isEmpty(folderName)) {
            mTvFolders.setText(folderName);
        }
        //更新图片列表数据源
        mMediaFileList.clear();
        mMediaFileList.addAll(mediaFolder.getMediaFileList());
        mImagePickerAdapter.notifyDataSetChanged();

        mImageFolderPopupWindow.dismiss();
    }

    class MediaLoader implements MediaLoadCallback {
        @Override
        public void loadMediaSuccess(final List<MediaFolder> mediaFolderList) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //默认加载全部照片
                    mMediaFileList.addAll(mediaFolderList.get(0).getMediaFileList());
                    mImagePickerAdapter.notifyDataSetChanged();

                    //图片文件夹数据
                    mMediaFolderList = new ArrayList<>(mediaFolderList);
                    mImageFolderPopupWindow = new FolderPopupWindow(ImagePickerActivity.this, mMediaFolderList);
                    mImageFolderPopupWindow.setAnimationStyle(R.style.imageFolderAnimator);
                    mImageFolderPopupWindow.getAdapter().setOnImageFolderChangeListener(ImagePickerActivity.this);
                    mImageFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            setLightMode(1.0f);
                        }
                    });
                    updateCommitButton();
                }
            });
        }
    }

    private void updateCommitButton() {
        int selectCount = SelectionManager.getInstance().getSelects().size();
        //预览Size
        mTvPreview.setText(selectCount>0?"预览("+selectCount+")":"预览");

        if (selectCount == 0) {
            mTvCommit.setEnabled(false);
            mTvCommit.setText(getString(R.string.confirm));
            return;
        }else if (selectCount <= mMaxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, mMaxCount));
            return;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
                commitSelection();
            }
        }
    }

    @Override
    public void onMediaClick(View view, int position) {
        if (mMediaFileList != null) {
            DataUtil.getInstance().setMediaData(mMediaFileList);
            Intent intent = new Intent(this, ImagePreActivity.class);
            intent.putExtra(ImagePreActivity.IMAGE_POSITION, position);
            startActivityForResult(intent, REQUEST_SELECT_IMAGES_CODE);
        }
    }

    @Override
    public void onMediaCheck(View view, int position) {
        //执行选中/取消操作
        MediaFile mediaFile = mImagePickerAdapter.getMediaFile(position);
        if (mediaFile != null) {
            boolean addSuccess = SelectionManager.getInstance().addImageToSelectList(mediaFile);
            if (addSuccess) {
                mImagePickerAdapter.notifyItemChanged(position);
            } else {
                Toast.makeText(this, String.format(getString(R.string.select_image_max), mMaxCount), Toast.LENGTH_SHORT).show();
            }
        }

        if (ConfigManager.getInstance().getSelectionMode() == ConfigManager.SELECT_MODE_SINGLE) {
            commitSelection();
        } else {
            updateCommitButton();
        }
    }

    private void commitSelection() {
        ArrayList<MediaFile> list = new ArrayList<>(SelectionManager.getInstance().getSelects());
        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putSerializable(ImagePicker.EXTRA_SELECT_IMAGES, list);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        //清空选中记录
        SelectionManager.getInstance().removeAll();
        finish();
    }


    private void setLightMode(float lightMode) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = lightMode;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mImagePickerAdapter != null) {
            mImagePickerAdapter.notifyDataSetChanged();
            updateCommitButton();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ConfigManager.getInstance().getImageLoader().clearMemoryCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
