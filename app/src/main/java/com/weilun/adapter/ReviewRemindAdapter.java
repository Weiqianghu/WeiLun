package com.weilun.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tb.emoji.EmojiUtil;
import com.weilun.activity.PostsDetailsActivity;
import com.weilun.bean.PostsBean;
import com.weilun.bean.UserBean;
import com.weilun.usermessage.UserReviewRemind;
import com.weilun.weilun.R;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by 胡伟强 on 2016/1/2.
 */
public class ReviewRemindAdapter extends BaseAdapter {
    private Context mContext;
    private List<PostsBean> mPostsList;

    private LayoutInflater layoutInflater;

    public ReviewRemindAdapter(Context context, ListView listView, List<PostsBean> data) {
        this.mContext = context;
        this.mPostsList = data;
        layoutInflater = LayoutInflater.from(context);
        listView.setOnItemClickListener(new PostsItemClickListener());
    }

    @Override
    public int getCount() {
        return mPostsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPostsList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.review_remind_item, null);

            viewHolder.postsTitle = (TextView) convertView.findViewById(R.id.postsTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PostsBean postsBean = mPostsList.get(position);
        EmojiUtil.displayTextView(viewHolder.postsTitle, postsBean.getTitle(), mContext);
        return convertView;
    }

    static class ViewHolder {
        private TextView postsTitle;
    }


    private class PostsItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.setClass(mContext, PostsDetailsActivity.class);
            PostsBean postsBean = mPostsList.get(position);
            Bundle postsBundle = new Bundle();
            postsBundle.putSerializable("posts", postsBean);
            intent.putExtras(postsBundle);
            mContext.startActivity(intent);
            UserReviewRemind.saveReviewCountByPostsId(mContext,
                    BmobUser.getCurrentUser(mContext, UserBean.class).getObjectId(),postsBean.getObjectId());
        }
    }
}
