package com.ray.api.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dangdang on 15/6/18.
 */
public class PhotoSelectData implements Serializable {

    private String dir;

    public String getDir() {
        if (TextUtils.isEmpty(dir)) {
            dir = "";
        } else
            dir += "/";
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    private ArrayList<PhotoData> selectData = new ArrayList<PhotoData>();

    public ArrayList<PhotoData> getSelectData() {
        return selectData;
    }

}
