package com.ray.api.model;

/**
 * Created by dangdang on 15/6/3.
 * 图片列表对象
 */
public class PhotoFolder {

    //文件夹路径
    private String directory;
    //第一张照片路径
    private String firstPhotoPath;
    //文件名
    private String fileName;
    //图片数量
    private int photoCount;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
        //文件夹名字，在获取路径的时候自动截取
        int lastIndexOf = this.directory.lastIndexOf("/");
        this.fileName = this.directory.substring(lastIndexOf);
    }

    public String getFirstPhotoPath() {
        return firstPhotoPath;
    }

    public void setFirstPhotoPath(String firstPhotoPath) {
        this.firstPhotoPath = firstPhotoPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }
}
