package com.ray.bitmap.download;

import java.io.OutputStream;

public interface IDownloader {
    /**
     * 请求网络下载
     *
     * @param urlString
     * @return
     */
    byte[] download(String urlString);
}
