package com.weilun.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.weilun.asynctask.ImgAsyncTask;
import com.weilun.loader.ImageLoader;
import com.weilun.threadpool.ThreadPool;
import com.weilun.weilun.R;

import java.util.List;

/**
 * Created by 胡伟强 on 2015/12/25.
 */
public class PostsImgAdapter extends BaseAdapter{
    private Context mContext;
    private List<String> mImgUrls;
    private ListView mListView;

    private LayoutInflater mLayoutInflater;

    private ImageLoader imageLoader;

    private int start, end;

    private  boolean firstIn = false;
    public  String[] URLS;

    public PostsImgAdapter(Context context, List<String> data, ListView listView){
        this.mContext=context;
        this.mImgUrls=data;
        this.mListView=listView;
        this.mLayoutInflater=LayoutInflater.from(context);

        URLS = new String[9];
        for (int i = 0,length=data.size(); i < length; i++) {
            URLS[i] = data.get(i);
        }
        imageLoader = new ImageLoader(listView, URLS);
        firstIn = true;
    }

    @Override
    public int getCount() {
        return mImgUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        ImgAsyncTask imgAsyncTask =null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=mLayoutInflater.inflate(R.layout.posts_img_item,null);
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.postImg);
            convertView.setTag(viewHolder);

            imgAsyncTask= new ImgAsyncTask(viewHolder.imageView, mContext);
            imgAsyncTask.executeOnExecutor(ThreadPool.getThreadPool(), mImgUrls.get(position));
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
    private class ViewHolder{
        public ImageView imageView;
    }
}
