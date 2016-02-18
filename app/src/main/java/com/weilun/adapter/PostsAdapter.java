package com.weilun.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tb.emoji.EmojiUtil;
import com.weilun.activity.PostsDetailsActivity;
import com.weilun.bean.PostsBean;
import com.weilun.loader.ImageLoader;
import com.weilun.weilun.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 胡伟强 on 2015/12/9.
 */
public class PostsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private ListView postListView;
    private List<PostsBean> postsItemBeenList;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    private int start, end;
    public  String[] URLS;
    private  boolean firstIn = false;
    private Context context;
    private int loadedCount = 0;
    private int totalCount = 0;
    private View loadingView;
    private int lastItem;

    PostsAdapter postsAdapter = this;


    public PostsAdapter(Context context, List<PostsBean> data, ListView listView, int totalCount, View loadingView) {
        if (data == null || listView == null)
            return;
        this.context = context;
        this.totalCount = totalCount;
        postListView = listView;
        this.loadingView = loadingView;

        postsItemBeenList = data;
        layoutInflater = LayoutInflater.from(context);
        int length = data.size();
        URLS = new String[totalCount];
        for (int i = 0; i < length; i++) {
            URLS[i] = data.get(i).getAuthor().getImg();
        }
        imageLoader = new ImageLoader(listView, URLS);
        firstIn = true;
        postListView.setOnScrollListener(this);
        postListView.setOnItemClickListener(new PostsItemClickListener());

    }


    @Override
    public int getCount() {
        return postsItemBeenList.size();
    }

    @Override
    public Object getItem(int position) {
        return postsItemBeenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.posts_item, null);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.mContent = (TextView) convertView.findViewById(R.id.tv_conntent);
            viewHolder.mUserImg = (ImageView) convertView.findViewById(R.id.userImg);
            viewHolder.mUserName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.mUserSex = (ImageView) convertView.findViewById(R.id.userSex);
            viewHolder.mUserAge = (TextView) convertView.findViewById(R.id.userAge);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(position>=postsItemBeenList.size())
            return convertView;
        PostsBean posts = postsItemBeenList.get(position);

        imageLoader.showImageAsyncTask(viewHolder.mUserImg,
                posts.getAuthor().getImg());

        EmojiUtil.displayTextView(viewHolder.mTitle,posts.getTitle(),context);
        EmojiUtil.displayTextView(viewHolder.mContent,posts.getContent(),context);
        viewHolder.mUserName.setText(posts.getAuthor().getUsername());

        Boolean tempSex = posts.getAuthor().getSex();
        if (tempSex == null) {
            viewHolder.mUserSex.setImageResource(R.drawable.unknow);
        } else if (tempSex == true) {
            viewHolder.mUserSex.setImageResource(R.drawable.male);
        } else {
            viewHolder.mUserSex.setImageResource(R.drawable.female);
        }
        if (posts.getAuthor().getAge() == null) {
            viewHolder.mUserAge.setText("未知");
        } else {
            viewHolder.mUserAge.setText(posts.getAuthor().getAge() + "");
        }

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (lastItem >= loadedCount -1 && scrollState == SCROLL_STATE_IDLE && loadedCount < totalCount) {
            loadMore();
        }

        if (scrollState == SCROLL_STATE_IDLE) {
            // Log.d("hyy", "onScrollStateChanged事件调用," + "start:" + start + "end:" + end);
            imageLoader.loadImages(start, end);
        } else {
            imageLoader.cancelAllTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        start = firstVisibleItem;
        end = firstVisibleItem + visibleItemCount;
        lastItem = firstVisibleItem + visibleItemCount - 1;
        loadedCount = this.getCount();
        if (firstIn && visibleItemCount > 0) {
            Log.d("hwq", "loadImages执行了" + start + "," + end);
            imageLoader.loadImages(start, end);
            firstIn = false;
            notifyDataSetChanged();
        }
        if (loadedCount >= totalCount) {
            postListView.removeFooterView(loadingView);
        }
    }

   static class ViewHolder {
        private  TextView mTitle;
        private  TextView mContent;
        private  ImageView mUserImg;
        private  TextView mUserName;
        private  ImageView mUserSex;
        private  TextView mUserAge;
    }


    public void loadMore() {
        BmobQuery<PostsBean> query = new BmobQuery<PostsBean>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.setLimit(10);
        query.include("author");
        query.order("-createdAt");
        query.setSkip(loadedCount);


        query.findObjects(context, new FindListener<PostsBean>() {
            @Override
            public void onSuccess(List<PostsBean> object) {
                // TODO Auto-generated method stub
                Log.d("bmob", "查询成功：共" + object.size() + "条数据。");
                int i = loadedCount;
                for (PostsBean posts : object) {
                    PostsBean postsBean = new PostsBean();
                    postsBean.setObjectId(posts.getObjectId());
                    postsBean.setTitle(posts.getTitle());
                    postsBean.setContent(posts.getContent());
                    postsBean.setAuthor(posts.getAuthor());
                    postsBean.setUpdatedAtStr(posts.getUpdatedAt());
                    postsBean.setPostsImg(posts.getPostsImg());
                    postsItemBeenList.add(postsBean);
                    URLS[i++] = posts.getAuthor().getImg();
                }
                postsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int code, String msg) {
                Log.d("bmob", "查询失败：" + msg);
            }
        });
    }

    private class PostsItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position<postsItemBeenList.size()) {
                Intent intent = new Intent();
                intent.setClass(context, PostsDetailsActivity.class);
                PostsBean postsBean=postsItemBeenList.get(position);
                Bundle postsBundle=new Bundle();
                postsBundle.putSerializable("posts",postsBean);
                intent.putExtras(postsBundle);


                context.startActivity(intent);
            }
        }
    }

}
