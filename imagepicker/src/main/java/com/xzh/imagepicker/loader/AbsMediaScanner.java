package com.xzh.imagepicker.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;


public abstract class AbsMediaScanner<T> {

    //查询URI
    protected abstract Uri getScanUri();

    //查询列名
    protected abstract String[] getProjection();

    //查询条件
    protected abstract String getSelection();

    //查询条件值
    protected abstract String[] getSelectionArgs();

    //查询排序
    protected abstract String getOrder();

    //对外暴露游标
    protected abstract T parse(Cursor cursor);

    private Context mContext;

    public AbsMediaScanner(Context context) {
        this.mContext = context;
    }

    //根据查询条件进行媒体库查询
    public ArrayList<T> queryMedia() {
        ArrayList<T> list = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(getScanUri(), getProjection(), getSelection(), getSelectionArgs(), getOrder());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                T t = parse(cursor);
                list.add(t);
            }
            cursor.close();
        }
        return list;
    }

}
