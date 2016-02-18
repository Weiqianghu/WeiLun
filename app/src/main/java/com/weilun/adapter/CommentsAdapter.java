package com.weilun.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.weilun.bean.CommentBean;
import com.weilun.bean.PostsBean;
import com.weilun.loader.ImageLoader;
import com.weilun.weilun.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 胡伟强 on 2015/12/16.
 */
public class CommentsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private ListView commentsListView;
    private List<CommentBean> commentBeanList;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    private int start, end;
    public String[] URLS;
    private Context context;
    private int loadedCount = 0;
    private int totalCount = 0;
    private View loadingView;
    private int lastItem;
    private String postsObjectId;
    private  boolean firstIn = false;

    CommentsAdapter commentsAdapter = this;


    public CommentsAdapter(Context context, List<CommentBean> data, ListView listView,
                           int totalCount, View loadingView,String postsObjectId) {
        if (data == null || listView == null)
            return;
        this.context = context;
        this.totalCount = totalCount;
        commentsListView = listView;
        this.loadingView = loadingView;
        this.postsObjectId=postsObjectId;

        commentBeanList = data;
        layoutInflater = LayoutInflater.from(context);
        int length = data.size();
        URLS = new String[totalCount];
        for (int i = 0; i < length; i++) {
            URLS[i] = data.get(i).getAuthor().getImg();
        }
        imageLoader = new ImageLoader(listView, URLS);
        commentsListView.setOnScrollListener(this);
        firstIn = true;

    }


    @Override
    public int getCount() {
        return commentBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentBeanList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.posts_comments_item, null);
            viewHolder.mContent = (TextView) convertView.findViewById(R.id.PostsComments);
            viewHolder.mUserImg = (ImageView) convertView.findViewById(R.id.userImg);
            viewHolder.mUserName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.mUserSex = (ImageView) convertView.findViewById(R.id.userSex);
            viewHolder.mUpdateDate = (TextView) convertView.findViewById(R.id.commentsDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CommentBean comments = commentBeanList.get(position);

        imageLoader.showImageAsyncTask(viewHolder.mUserImg,
                comments.getAuthor().getImg());


        //viewHolder.mContent.setText(comments.getContent());
        EmojiUtil.displayTextView(viewHolder.mContent,comments.getContent(),context);
        viewHolder.mUserName.setText(comments.getAuthor().getUsername());

        Boolean tempSex = comments.getAuthor().getSex();
        if (tempSex == null) {
            viewHolder.mUserSex.setImageResource(R.drawable.unknow);
        } else if (tempSex == true) {
            viewHolder.mUserSex.setImageResource(R.drawable.male);
        } else {
            viewHolder.mUserSex.setImageResource(R.drawable.female);
        }
        viewHolder.mUpdateDate.setText(comments.getUpdatedAtStr().split("\\s")[0]);

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
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
            commentsListView.removeFooterView(loadingView);
        }
    }

    static class ViewHolder {
        private TextView mContent;
        private ImageView mUserImg;
        private TextView mUserName;
        private ImageView mUserSex;
        private TextView mUpdateDate;
    }


    public void loadMore(String postsObjectid) {

        PostsBean postsBean = new PostsBean();
        postsBean.setObjectId(postsObjectid);

        BmobQuery<CommentBean> query = new BmobQuery<>();
        query.setLimit(10);
        query.addWhereEqualTo("posts", new BmobPointer(postsBean));
        query.include("author");
        query.order("-updatedAt");
        query.setSkip(loadedCount);


        Log.d("bmob", "loadMore：执行");

        query.findObjects(context, new FindListener<CommentBean>() {

            @Override
            public void onSuccess(List<CommentBean> list) {
                int i = loadedCount;
                for (CommentBean comments : list) {
                    CommentBean commentBean = new CommentBean();
                    commentBean.setContent(comments.getContent());
                    commentBean.setAuthor(comments.getAuthor());
                    commentBean.setUpdatedAtStr(comments.getUpdatedAt());
                    commentBeanList.add(commentBean);
                    URLS[i++] = comments.getAuthor().getImg();
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int code, String msg) {
                Log.d("bmob", "查询失败：" + msg);
            }
        });
    }


}
