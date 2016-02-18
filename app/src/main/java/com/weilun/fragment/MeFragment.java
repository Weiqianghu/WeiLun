package com.weilun.fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;
import com.weilun.activity.ReviewRemindListActivity;
import com.weilun.asynctask.ImgAsyncTask;
import com.weilun.bean.UserBean;
import com.weilun.threadpool.ThreadPool;
import com.weilun.ui.CircleImageView;
import com.weilun.usermessage.UserReviewRemind;
import com.weilun.util.FileUtil;
import com.weilun.weilun.R;

import cn.bmob.v3.BmobUser;

public class MeFragment extends Fragment {

    private Button btnSetting;
    private TextView username;
    private View view;
    private TextView age, sex;
    private LruCache<String, Bitmap> lruCache;
    private CircleImageView userImg;
    private TextView editMyInfo;
    private boolean isFrist=true;

    private TextView mMyReview;

    private BadgeView badge;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(isFrist==true) {
            view = inflater.inflate(R.layout.fragment_me, container, false);
            initView();
            isFrist=false;
        }
        new ShowMessageRemindAsyncTask().executeOnExecutor(ThreadPool.getThreadPool());
        return view;
    }

    private void initView() {
        btnSetting = (Button) view.findViewById(R.id.btnSetting);
        userImg = (CircleImageView) view.findViewById(R.id.userImg);
        age = (TextView) view.findViewById(R.id.age);
        sex = (TextView) view.findViewById(R.id.sex);
        editMyInfo= (TextView) view.findViewById(R.id.editMyInfo);
        username= (TextView) view.findViewById(R.id.userName);

        mMyReview = (TextView) view.findViewById(R.id.myReview);
        mMyReview.setOnClickListener(new showUserReviewClickListener());

        UserBean userBean = BmobUser.getCurrentUser(getActivity(), UserBean.class);
        String url = userBean.getImg();
        if (url != null) {
            ImgAsyncTask imgAsyncTask = new ImgAsyncTask( userImg,getActivity());
            imgAsyncTask.executeOnExecutor(ThreadPool.getThreadPool(),url);
        }
        else
        {
            userImg.setImageResource(R.drawable.userimg);
        }
        String ageStr;
        String sexStr;
        if (userBean.getAge() == null)
            ageStr = "未知";
        else
            ageStr = userBean.getAge() + "";
        if (userBean.getSexStr() == null)
            sexStr = "未知";
        else
            sexStr = userBean.getSexStr();
        age.setText(ageStr);
        sex.setText(sexStr);
        username.setText(userBean.getUsername());
    }

    class ShowMessageRemindAsyncTask extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(Integer.valueOf(s)>0) {
                badge = new BadgeView(getActivity(), mMyReview);
                badge.setText(s);
                badge.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            return UserReviewRemind.getUserReviewCount(getActivity());
        }
    }

    private class showUserReviewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(badge!=null && badge.isShown()){
                badge.hide();
                UserReviewRemind.saveReviewCount(getActivity());
            }

            Intent intent=new Intent(getActivity(), ReviewRemindListActivity.class);
            startActivity(intent);

        }
    }
}
