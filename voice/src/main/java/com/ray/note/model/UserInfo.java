package com.ray.note.model;

import cn.bmob.v3.BmobUser;

/**
 * Created by zhangleilei on 15/5/9.
 * 用户表
 */
public class UserInfo extends BmobUser{

    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
