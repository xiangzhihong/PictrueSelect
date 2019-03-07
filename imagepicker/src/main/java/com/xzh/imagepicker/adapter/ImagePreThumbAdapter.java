package com.xzh.imagepicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.xzh.imagepicker.R;
import com.xzh.imagepicker.bean.ItemType;
import com.xzh.imagepicker.bean.MediaFile;
import com.xzh.imagepicker.manager.ConfigManager;
import com.xzh.imagepicker.view.SquareImageView;

import java.util.List;


public class ImagePreThumbAdapter extends RecyclerView.Adapter {


    private Context mContext = null;
    private List<MediaFile> mMediaFileList = null;
    public OnItemClickListener mOnItemClickListener;
    public MediaFile checkMediaFile=null;

    public ImagePreThumbAdapter(Context context, List<MediaFile> list) {
        this.mContext = context;
        this.mMediaFileList = list;
    }


    @Override
    public int getItemViewType(int position) {
        if (mMediaFileList.get(position).getDuration() > 0) {
            return ItemType.ITEM_TYPE_VIDEO;
        } else {
            return ItemType.ITEM_TYPE_IMAGE;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        if (viewType == ItemType.ITEM_TYPE_IMAGE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_pre_image, null);
            return new ThumbImageHolder(view);
        }
        if (viewType == ItemType.ITEM_TYPE_VIDEO) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_pre_video, null);
            return new ThumbVideoHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int itemType = getItemViewType(position);
        MediaFile mediaFile = mMediaFileList.get(position);
        if (itemType == ItemType.ITEM_TYPE_IMAGE) {
            ThumbImageHolder imageHolder = (ThumbImageHolder) viewHolder;
            bindImageView(imageHolder, mediaFile);
        } else if (itemType == ItemType.ITEM_TYPE_VIDEO) {
            ThumbVideoHolder videoHolder = (ThumbVideoHolder) viewHolder;
            bindVideoView(videoHolder, mediaFile);
        }
    }


    private void bindImageView(final ThumbImageHolder imageHolder, final MediaFile mediaFile) {
        try {
            ConfigManager.getInstance().getImageLoader().loadImage(imageHolder.itemPreImage, mediaFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //选中后边框效果
        if (mediaFile.equals(checkMediaFile)){
            imageHolder.itemBorder.setVisibility(View.VISIBLE);
        }else {
            imageHolder.itemBorder.setVisibility(View.GONE);
        }

        imageHolder.itemPreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(imageHolder.itemPreImage, mediaFile);
                }
            }
        });
    }

    private void bindVideoView(final ThumbVideoHolder videoHolder, final MediaFile mediaFile) {
        try {
            ConfigManager.getInstance().getImageLoader().loadImage(videoHolder.itemPreImage, mediaFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //选中后边框效果
        if (mediaFile.equals(checkMediaFile)){
            videoHolder.itemBorder.setVisibility(View.VISIBLE);
        }else {
            videoHolder.itemBorder.setVisibility(View.GONE);
        }

        videoHolder.itemPreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(videoHolder.itemPreImage, mediaFile);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mMediaFileList != null && mMediaFileList.size() > 0) {
            return mMediaFileList.size();
        }
        return 0;
    }


    //缩略图片
    class ThumbImageHolder extends RecyclerView.ViewHolder {

        SquareImageView itemPreImage;
        SquareImageView itemBorder;

        ThumbImageHolder(View itemView) {
            super(itemView);
            itemPreImage = itemView.findViewById(R.id.item_pre_image);
            itemBorder = itemView.findViewById(R.id.item_image_border);
        }
    }

    //缩略视频
    class ThumbVideoHolder extends RecyclerView.ViewHolder {

        SquareImageView itemPreImage;
        SquareImageView itemBorder;

        ThumbVideoHolder(View itemView) {
            super(itemView);
            itemPreImage = itemView.findViewById(R.id.item_pre_image);
            itemBorder = itemView.findViewById(R.id.item_image_border);
        }
    }

    public void setSelect(MediaFile mediaFile) {
        this.checkMediaFile=mediaFile;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, MediaFile mediaFile);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickLitener) {
        this.mOnItemClickListener = mOnItemClickLitener;
    }
}

