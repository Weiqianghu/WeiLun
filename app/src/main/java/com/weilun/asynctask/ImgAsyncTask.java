package com.weilun.asynctask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.weilun.lrucache.LruCacheStaticObject;
import com.weilun.util.FileUtil;
import com.weilun.weilun.R;

/**
 * Created by 胡伟强 on 2015/12/15.
 */
public class ImgAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;
    private Context context;
    private LruCache<String, Bitmap> lruCache;

    public ImgAsyncTask(ImageView imageView,Context context) {
        this.imageView = imageView;
        this.context=context;
        lruCache= LruCacheStaticObject.getLruCache();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        Bitmap bitmap = FileUtil.getBitmapFromUrl(url);
        if (bitmap != null) {
            addBitmapToCache(url, bitmap);
        }
        else
        {
            Resources res=context.getResources();
            bitmap= BitmapFactory.decodeResource(res, R.drawable.userimg);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imageView.setImageBitmap(bitmap);
    }

    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            lruCache.put(url, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String url) {
        return lruCache.get(url);
    }
}