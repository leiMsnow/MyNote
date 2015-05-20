package com.ray.bitmap.download;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import android.util.Log;

/**
 * 默认下载器
 *
 * @author zhangleilei
 *
 */
public class DefaultDownloader implements IDownloader {

    private static final String TAG = DefaultDownloader.class.getSimpleName();
    private static final int IO_BUFFER_SIZE = 8 * 1024; // 8k

    @Override
    public byte[] download(String urlString) {
        if (urlString == null)
            return null;

        // 下载网络图片
        if (urlString.trim().toLowerCase().startsWith("http")) {
            return getFromHttp(urlString);
        }
        // 读取本地图片
        else if (urlString.trim().toLowerCase().startsWith("file:")) {
            try {
                File f = new File(new URI(urlString));
                if (f.exists() && f.canRead()) {
                    return getFromFile(f);
                }
            } catch (URISyntaxException e) {
                Log.e(TAG, "Error in read from file - " + urlString + " : " + e);
            }
        } else {
            File f = new File(urlString);
            if (f.exists() && f.canRead()) {
                return getFromFile(f);
            }
        }
        return null;
    }

    /**
     * 获取本地资源
     *
     * @param file
     * @return
     */
    private byte[] getFromFile(File file) {
        if (file == null)
            return null;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            Log.e(TAG, "Error in read from file - " + file + " : " + e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    fis = null;
                } catch (IOException e) {

                }
            }
        }

        return null;
    }

    /**
     * 下载网络图片
     *
     * @param urlString
     * @return
     */
    private byte[] getFromHttp(String urlString) {
        HttpURLConnection urlConnection = null;
        ByteArrayOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            out = new ByteArrayOutputStream();
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return out.toByteArray();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
