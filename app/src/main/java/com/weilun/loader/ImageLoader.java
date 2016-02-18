package com.weilun.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.weilun.adapter.PostsAdapter;
import com.weilun.lrucache.LruCacheStaticObject;
import com.weilun.threadpool.ThreadPool;
import com.weilun.weilun.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 胡伟强 on 2015/12/5.
 */
public class ImageLoader {

    private static LruCache<String, Bitmap> lruCache;
    private static ListView listView;
    private static Set<LoadImgFromUrlAsyncTask> mTask;
    private String[] URLS;

    public ImageLoader(ListView listView, String[] URLS) {

        this.URLS = URLS;
        this.listView = listView;
        mTask = new HashSet<>();


        lruCache= LruCacheStaticObject.getLruCache();
    }

    public static void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            lruCache.put(url, bitmap);
        }
    }

    public static Bitmap getBitmapFromCache(String url) {
        return lruCache.get(url);
    }


    public void loadImages(int start, int end) {
        for (int i = start, length = URLS.length; i < length && i < end; i++) {
            String url = URLS[i];
            if (url == null)
                return;
            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null) {
                LoadImgFromUrlAsyncTask loadImgFromUrlAsyncTask = new LoadImgFromUrlAsyncTask(url);
                loadImgFromUrlAsyncTask.executeOnExecutor(ThreadPool.getThreadPool(), url);
                mTask.add(loadImgFromUrlAsyncTask);
            } else {
                ImageView imageView = (ImageView) listView.findViewWithTag(url);
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void showImageAsyncTask(ImageView imageView, final String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
    }

    public void cancelAllTask() {
        if (mTask != null) {
            for (LoadImgFromUrlAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }

    private static class LoadImgFromUrlAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private String url;

        public LoadImgFromUrlAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = getBitmapFromUrl(url);
            if (bitmap != null) {
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) listView.findViewWithTag(url);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
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
}
