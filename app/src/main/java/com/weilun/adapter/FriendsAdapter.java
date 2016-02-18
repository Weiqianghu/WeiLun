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
import android.widget.Toast;


import com.readystatesoftware.viewbadger.BadgeView;
import com.weilun.activity.FriendsChatActivity;
import com.weilun.bean.UserBean;
import com.weilun.fragment.FriendFragment;
import com.weilun.loader.ImageLoader;
import com.weilun.ui.PullRefreshListView;
import com.weilun.weilun.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import imsdk.data.IMMyself;
import imsdk.data.recentcontacts.IMMyselfRecentContacts;


/**
 * Created by 胡伟强 on 2015/12/9.
 */
public class FriendsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private List<UserBean> friendsList;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    private int start, end;
    public static String[] URLS;
    private boolean firstIn = false;

    private Context mcontext;

    private UserBean currentUser;

    public FriendsAdapter(Context context, List<UserBean> data, ListView listView) {
        this.mcontext = context;
        friendsList = data;
        layoutInflater = LayoutInflater.from(context);
        int length = data.size();
        URLS = new String[length];
        for (int i = 0; i < length; i++) {
            URLS[i] = data.get(i).getImg();
        }
        imageLoader = new ImageLoader(listView, URLS);
        firstIn = true;
        listView.setOnScrollListener(this);
        listView.setOnItemClickListener(new FriendsItemClickListener());

        currentUser= BmobUser.getCurrentUser(context,UserBean.class);

    }

    private class FriendsItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UserBean userBean = friendsList.get(position);

            IMMyselfRecentContacts.clearUnreadChatMessage(userBean.getMobilePhoneNumber());

            Intent intent = new Intent(mcontext, FriendsChatActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putSerializable("friends", userBean);
            intent.putExtras(userBundle);

            mcontext.startActivity(intent);
        }
    }


    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.friend_item, null);
            viewHolder.mFriendImageView = (ImageView) convertView.findViewById(R.id.friendImg);
            viewHolder.mFriendNameTextView = (TextView) convertView.findViewById(R.id.friendName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        imageLoader.showImageAsyncTask(viewHolder.mFriendImageView,
                friendsList.get(position).getImg());
        viewHolder.mFriendNameTextView.setText(friendsList.get(position).getUsername());

        long unreadMsgCount = IMMyselfRecentContacts.getUnreadChatMessageCount(friendsList.get(position).getMobilePhoneNumber());

        if (unreadMsgCount > 0) {
            BadgeView badgeView = new BadgeView(mcontext, viewHolder.mFriendNameTextView);
            badgeView.setText(String.valueOf(unreadMsgCount));
            badgeView.show();
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView mFriendImageView;
        private TextView mFriendNameTextView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            imageLoader.loadImages(start, end);
        } else {
            imageLoader.cancelAllTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        start = firstVisibleItem;
        end = firstVisibleItem + visibleItemCount;
        if (firstIn && visibleItemCount > 0) {
            imageLoader.loadImages(start, end);
            firstIn = false;
            notifyDataSetChanged();
        }
    }
}
