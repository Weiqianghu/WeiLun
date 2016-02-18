package com.weilun.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tb.emoji.EmojiUtil;
import com.weilun.bean.ChatMessageBean;
import com.weilun.bean.UserBean;
import com.weilun.loader.LoadImgFromUrlAsyncTask;
import com.weilun.util.DateUtil;
import com.weilun.weilun.R;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by 胡伟强 on 2016/1/8.
 */
public class ChatAdapter extends BaseAdapter {

    private List<ChatMessageBean> mMessageList;
    private Context mContext;

    private LayoutInflater mLayoutInflater;
    private UserBean mCurrentUser;

    private String userImgUrl, friendImgUrl;

    public ChatAdapter(Context context, List<ChatMessageBean> data, String userImgUrl, String friendImgUrl) {
        this.mContext = context;
        this.mMessageList = data;
        mLayoutInflater = LayoutInflater.from(context);
        mCurrentUser = BmobUser.getCurrentUser(context, UserBean.class);

        this.userImgUrl = userImgUrl;
        this.friendImgUrl = friendImgUrl;
    }


    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMessageBean chatMessageBean = mMessageList.get(position);
        Bitmap bitmap = null;
        boolean isComMsg = isComMsg(chatMessageBean);
        ViewHolder viewHolder = null;
        if (isComMsg) {
            convertView = mLayoutInflater.inflate(R.layout.chatting_item_msg_text_left, null);
            if (friendImgUrl != null) {
                bitmap = LoadImgFromUrlAsyncTask.getBitmapFromCache(friendImgUrl);
            }
        } else {
            convertView = mLayoutInflater.inflate(R.layout.chatting_item_msg_text_right, null);
            if (friendImgUrl != null) {
                bitmap = LoadImgFromUrlAsyncTask.getBitmapFromCache(userImgUrl);
            }
        }
        viewHolder = new ViewHolder();
        viewHolder.isComMsg = isComMsg;
        viewHolder.ivUserImg = (ImageView) convertView.findViewById(R.id.iv_userhead);
        viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
        convertView.setTag(viewHolder);

        if (bitmap != null) {
            viewHolder.ivUserImg.setImageBitmap(bitmap);
        }
        viewHolder.tvSendTime.setText(DateUtil.getChatTime(Long.parseLong(chatMessageBean.getSendTime())));
        EmojiUtil.displayTextView(viewHolder.tvContent, chatMessageBean.getContent(), mContext);
        return convertView;
    }

    static class ViewHolder {
        public TextView tvSendTime;
        private ImageView ivUserImg;
        public TextView tvContent;
        public boolean isComMsg = true;
    }

    private boolean isComMsg(ChatMessageBean chatMessageBean) {
        if (chatMessageBean.getBelongUserMobileNo().equals(mCurrentUser.getMobilePhoneNumber())) {
            return false;
        }
        return true;
    }

    public void add(ChatMessageBean msg) {
        this.mMessageList.add(msg);
        notifyDataSetChanged();
    }
}
