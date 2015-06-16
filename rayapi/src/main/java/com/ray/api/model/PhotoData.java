package com.ray.api.model;

import java.io.Serializable;

/**
 * 照片实体类
 * Created by dangdang on 15/6/15.
 */
public class PhotoData implements Serializable {

    private int index;
    private String uri;
    private boolean isSelect = false;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
