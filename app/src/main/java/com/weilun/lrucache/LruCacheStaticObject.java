package com.weilun.lrucache;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by 胡伟强 on 2016/1/10.
 */
public class LruCacheStaticObject {
    private static LruCache<String, Bitmap> lruCache;
    static {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        lruCache = new LruCache<String, Bitmap>(maxMemory * 5 / 16) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public static LruCache<String, Bitmap> getLruCache(){
        return lruCache;
    }
}
