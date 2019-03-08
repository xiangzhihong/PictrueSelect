package com.xzh.pictrueselect;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CameraDialog extends Dialog {

    private LinearLayout llView;
    private TextView tvCamera;
    private TextView tvAlbum;
    private TextView tvCancel;

    private Context mContext = null;
    private View view = null;
    private LayoutInflater layoutInflater = null;
    private OnItemClickListener onClickListener = null;

    public CameraDialog(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        initWindow();
        initView();
        findViewById();
        initClick();
    }

    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window dialogWindow = getWindow();
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        dialogWindow.setWindowAnimations(R.style.animation_bottom_to_top);
    }

    private void initView() {
        layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.camera_dialog, null);
        setContentView(view);
    }

    private void findViewById() {
        llView = findViewById(R.id.ll_view);
        tvCamera = findViewById(R.id.tv_camera);
        tvAlbum = findViewById(R.id.tv_album);
        tvCancel = findViewById(R.id.tv_cancel);
    }

    private void initClick() {
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                album(v);
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(v);
            }
        });
    }


    public void camera(View v) {
        dismiss();
        if (onClickListener != null) {
            onClickListener.onClick(view, ClickType.CAMERA);
        }
    }

    public void album(View v) {
        dismiss();
        if (onClickListener != null) {
            onClickListener.onClick(view, ClickType.ALBUM);
        }
    }

    public void cancel(View v) {
        dismiss();
        if (onClickListener != null) {
            onClickListener.onClick(view, ClickType.CANCEL);
        }
    }

    public CameraDialog setOnClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnItemClickListener {
        void onClick(View v, ClickType type);
    }

    public enum ClickType {
        CAMERA, ALBUM, CANCEL;

        @Override
        public String toString() {
            return name();
        }
    }

}