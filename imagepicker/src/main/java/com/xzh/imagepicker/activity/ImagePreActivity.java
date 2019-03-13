package com.xzh.imagepicker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.xzh.imagepicker.R;
import com.xzh.imagepicker.adapter.ImagePreThumbAdapter;
import com.xzh.imagepicker.adapter.ImagePreViewAdapter;
import com.xzh.imagepicker.bean.MediaFile;
import com.xzh.imagepicker.manager.SelectionManager;
import com.xzh.imagepicker.utils.DataUtil;
import com.xzh.imagepicker.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 大图预览界面
 */
public class ImagePreActivity extends AppCompatActivity {

    private TextView mTvTitle;
    private TextView mTvCommit;
    private HackyViewPager mViewPager;
    private ImageView mIvPreCheck;
    private LinearLayout mLlPreSelect;
    private RecyclerView mRePreImage;

    public static final String IMAGE_POSITION = "imagePosition";
    private List<MediaFile> mMediaFileList;//全部媒体（图片和视频）
    private List<MediaFile> mSelectList;//选中列表
    private int mPosition = 0;
    private ImagePreViewAdapter mImagePreViewAdapter;
    private ImagePreThumbAdapter preViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_image);
        init();
    }

    private void init() {
        initView();
        initPreView();
        initThumbView();
        initData();
        initListener();
    }

    private void initView() {
        mTvTitle = findViewById(R.id.tv_title);
        mTvCommit = findViewById(R.id.tv_commit);
        mViewPager = findViewById(R.id.vp_main_preImage);
        mLlPreSelect = findViewById(R.id.ll_pre_select);
        mIvPreCheck = findViewById(R.id.iv_item_check);
        mRePreImage = findViewById(R.id.re_preImage);
    }

    private void initPreView() {
        mMediaFileList = DataUtil.getInstance().getMediaData();
        mPosition = getIntent().getIntExtra(IMAGE_POSITION, 0);

        mImagePreViewAdapter = new ImagePreViewAdapter(this, mMediaFileList);
        mViewPager.setAdapter(mImagePreViewAdapter);
        mViewPager.setCurrentItem(mPosition);
        mTvTitle.setText(String.format("%d/%d", mPosition + 1, mMediaFileList.size()));
    }

    private void initThumbView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRePreImage.setLayoutManager(layoutManager);

        mSelectList = new ArrayList<>();
        mSelectList.clear();
        preViewAdapter = new ImagePreThumbAdapter(this, mSelectList);
        mRePreImage.setAdapter(preViewAdapter);
        mRePreImage.smoothScrollToPosition(mPosition);
    }

    private void initData() {
        updateCheckbox(mMediaFileList.get(mPosition));
        updateCommitButton();
        updateRecycleCheck();
    }

    private void updateRecycleCheck() {
        //Recycle默认选中的
        List<MediaFile> mediaFiles = SelectionManager.getInstance().getSelects();
        for (MediaFile mf : mediaFiles) {
            initPreData(mf);
        }
        MediaFile mediaFile=mMediaFileList.get(mPosition);
        if (mediaFile!=null&&preViewAdapter!=null){
            preViewAdapter.setSelect(mediaFile);
        }
    }

    private void initPreData(MediaFile mediaFile) {
        boolean isSelect = SelectionManager.getInstance().isImageSelect(mediaFile);
        if (isSelect) {
            mSelectList.add(mediaFile);
        } else {
            mSelectList.remove(mediaFile);
        }
        preViewAdapter.notifyDataSetChanged();
    }


    private void initListener() {
        findViewById(R.id.iv_actionBar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //ViewPager滚动
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvTitle.setText(String.format("%d/%d", position + 1, mMediaFileList.size()));
                updateCheckbox(mMediaFileList.get(position));
                MediaFile mediaFile=mMediaFileList.get(position);
                if (mediaFile!=null&&preViewAdapter!=null){
                    preViewAdapter.setSelect(mediaFile);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mLlPreSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean addSuccess = SelectionManager.getInstance().addImageToSelectList(mMediaFileList.get(mViewPager.getCurrentItem()));
                if (addSuccess) {
                    MediaFile mediaFile = mMediaFileList.get(mViewPager.getCurrentItem());
                    updateCheckbox(mediaFile);
                    updateCommitButton();
                    updatePreData(mediaFile);
                } else {
                    Toast.makeText(ImagePreActivity.this, String.format(getString(R.string.select_image_max), SelectionManager.getInstance().getMaxCount()), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });

        //点击Recyle滚动到指定位置的大图
        preViewAdapter.setOnItemClickListener(new ImagePreThumbAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MediaFile mediaFile) {
                preViewAdapter.setSelect(mediaFile);
                int position = SelectionManager.getInstance().getSelectImagePosition(mMediaFileList, mediaFile);
                Toast.makeText(ImagePreActivity.this, "选中的位置："+position, Toast.LENGTH_LONG).show();
                mViewPager.setCurrentItem(position);
            }
        });
    }

    private void updatePreData(MediaFile mediaFile) {
        boolean isSelect = SelectionManager.getInstance().isImageSelect(mediaFile);
        int currentPos = mViewPager.getCurrentItem();
        if (isSelect) {
            mSelectList.add(mMediaFileList.get(currentPos));
        } else {
            mSelectList.remove(mMediaFileList.get(currentPos));
        }
        preViewAdapter.notifyDataSetChanged();
        mRePreImage.smoothScrollToPosition(mPosition);
    }

    private void updateCommitButton() {
        int maxCount = SelectionManager.getInstance().getMaxCount();
        int selectCount = SelectionManager.getInstance().getSelects().size();
        if (selectCount == 0) {
            mTvCommit.setEnabled(false);
            mTvCommit.setText(getString(R.string.confirm));
            return;
        }
        if (selectCount <= maxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.confirm_msg), selectCount, maxCount));
            return;
        }
    }

    private void updateCheckbox(MediaFile mediaFile) {
        boolean isSelect = SelectionManager.getInstance().isImageSelect(mediaFile);
        if (isSelect) {
            mIvPreCheck.setImageDrawable(getResources().getDrawable(R.mipmap.icon_image_checked));
        } else {
            mIvPreCheck.setImageDrawable(getResources().getDrawable(R.mipmap.icon_image_check));
        }
    }
}
