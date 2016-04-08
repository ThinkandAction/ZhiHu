package com.example.wujie.zhihu.cache;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wujie on 2016/4/6.
 */
public class LevelTwoCache implements ImageLoader.ImageCache {

    private LruImageCache mLruImageCache;

    private DiskLruImageCache mDiskLruImageCache;

    public LevelTwoCache(Context context, String uniqueName, int diskCacheSize, int memCacheSize,
                         Bitmap.CompressFormat compressFormat, int quality) {

        mLruImageCache = new LruImageCache(memCacheSize);
        mDiskLruImageCache = new DiskLruImageCache(context, uniqueName, diskCacheSize, compressFormat,
                quality);

    }

    /**
     * 先从内存获取图片，如果内存找不到就从磁盘里找，找到了存在内存里并返回
     */
    @Override
    public Bitmap getBitmap(String url) {
        String key = createKey(url);
        Bitmap bitmap = null;
//源码逻辑有错误，更改
        if (mLruImageCache.getBitmap(key) == null) {
            if (mDiskLruImageCache.containsKey(key)) {
                bitmap = mDiskLruImageCache.getBitmap(key);
                mLruImageCache.putBitmap(key, bitmap);
            } else {
                return null;
            }
        } else {
            bitmap = mLruImageCache.getBitmap(key);
        }

        return bitmap;
    }

    /**
     * 首次图片从网络下载下来后分别保存在内存和磁盘缓存里
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        String key = createKey(url);

        mLruImageCache.putBitmap(key, bitmap);
        mDiskLruImageCache.putBitmap(key, bitmap);
    }

    /**
     * 把url转成MD5
     */
    private String createKey(String url) {
        return getBitmapMDKey(url);
    }

    public String getBitmapMDKey(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 清除内存缓存
     */
    /*
    public void cleanMemCache() {
        mLruImageCache.evictAll();
    }

    /**
     * 清除磁盘缓存
     */
    public void cleanDiskCache() {
        mDiskLruImageCache.clearCache();
    }

}