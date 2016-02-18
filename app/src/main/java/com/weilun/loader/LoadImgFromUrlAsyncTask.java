package com.weilun.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.weilun.lrucache.LruCacheStaticObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 胡伟强 on 2016/1/9.
 */
public class LoadImgFromUrlAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private String url;
    private static LruCache<String, Bitmap> lruCache;

    static {
        lruCache= LruCacheStaticObject.getLruCache();
    }

    public LoadImgFromUrlAsyncTask(String url) {
        this.url = url;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        if(url==null){
            return null;
        }
        Bitmap bitmap = getBitmapFromUrl(url);
        if (bitmap != null) {
            addBitmapToCache(url, bitmap);
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromUrl(String urlStr) {
        InputStream inputStream = null;
        Bitmap bitmap;
        HttpURLConnection httpURLConnection;
        try {
            URL url = new URL(urlStr);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(inputStream);
            httpURLConnection.disconnect();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            lruCache.put(url, bitmap);
        }
    }

    public static Bitmap getBitmapFromCache(String url) {
        return lruCache.get(url);
    }
}
