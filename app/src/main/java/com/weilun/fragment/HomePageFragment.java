package com.weilun.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.weilun.activity.NetContConnectActivity;
import com.weilun.activity.PublishNewPostsActivity;
import com.weilun.adapter.PostsAdapter;
import com.weilun.bean.PostsBean;
import com.weilun.bean.UserBean;
import com.weilun.threadpool.ThreadPool;
import com.weilun.weilun.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.update.BmobUpdateAgent;


public class HomePageFragment extends Fragment {


    private ListView mPostsListView;

    private ImageButton refresh;
    private ImageView mPublishNewPosts;
    private  PostsAdapter postsAdapter;
    private TextView mTitle;
    private TextView mContent;
    private ImageView mUserImg;
    private TextView mUserName;
    private ImageView mUserSex;
    private TextView mUserAge;
    private ProgressBar progressBar;
    private  View view;
    private  Boolean isGetData = false;
    private  boolean isFresh = true;
    private  View loadingView;
    private  List<PostsBean> resultPostsList = new ArrayList<>();

    private  int totalCount=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (isFresh == true) {
            refreshPosts();
            isFresh = false;
            view = inflater.inflate(R.layout.fragment_home_page, container, false);
            initView(inflater);
        } else {
            cacheView();
        }

        return view;
    }

    private void refreshPosts(){
        new PostsAsyncTask().executeOnExecutor(ThreadPool.getThreadPool());
    }

    private void initView(LayoutInflater inflater) {
        mPostsListView = (ListView) view.findViewById(R.id.lv_posts);

        mTitle = (TextView) view.findViewById(R.id.tv_title);
        mContent = (TextView) view.findViewById(R.id.tv_conntent);
        mUserImg = (ImageView) view.findViewById(R.id.userImg);
        mUserName = (TextView) view.findViewById(R.id.userName);
        mUserSex = (ImageView) view.findViewById(R.id.userSex);
        mUserAge = (TextView) view.findViewById(R.id.userAge);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        refresh= (ImageButton) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new refreshPostsListener());

        loadingView=inflater.inflate(R.layout.loading,null);

        mPublishNewPosts= (ImageView) view.findViewById(R.id.publishNewPosts);
        mPublishNewPosts.setOnClickListener(new publishNewPostsListener());

        mPostsListView.addFooterView(loadingView);

    }


    private void cacheView() {
        mPostsListView.setAdapter(postsAdapter);
    }


     class PostsAsyncTask extends AsyncTask<Void, Void, List<PostsBean>> {
        @Override
        protected void onPreExecute() {
            getCount();
            super.onPreExecute();
        }

        @Override
        protected List<PostsBean> doInBackground(Void... params) {
            return getPostsData();
        }

        @Override
        protected void onPostExecute(List<PostsBean> newsBeen) {
            if(newsBeen==null || newsBeen.size()==0){
                Toast.makeText(getActivity(),"网络状况不佳",Toast.LENGTH_SHORT).show();
            }
            postsAdapter = new PostsAdapter(getActivity(), newsBeen, mPostsListView,totalCount,loadingView);
            mPostsListView.setAdapter(postsAdapter);
            progressBar.setVisibility(View.INVISIBLE);
            BmobUpdateAgent.update(getActivity());
            super.onPostExecute(newsBeen);
        }
    }

    private List<PostsBean> getPostsData() {
        BmobQuery<PostsBean> query = new BmobQuery<>();
        query.setLimit(10);
        query.include("author");
        query.order("-updatedAt");

        query.findObjects(getActivity(), new FindListener<PostsBean>() {
            @Override
            public void onSuccess(List<PostsBean> object) {
                // TODO Auto-generated method stub
                resultPostsList.clear();
                for (PostsBean posts : object) {
                    PostsBean postsBean = new PostsBean();
                    postsBean.setObjectId(posts.getObjectId());
                    postsBean.setTitle(posts.getTitle());
                    postsBean.setContent(posts.getContent());
                    postsBean.setAuthor(posts.getAuthor());
                    postsBean.setUpdatedAtStr(posts.getUpdatedAt());
                    postsBean.setPostsImg(posts.getPostsImg());
                    resultPostsList.add(postsBean);
                }
                Log.d("bmob", "查询成功：共" + object.size() + "条数据。");
                isGetData = true;
            }

            @Override
            public void onError(int code, String msg) {
                Log.d("bmob", "帖子数据查询失败：" + msg);
            }
        });


        long currentTime=System.currentTimeMillis();
        while (!isGetData) {
            if(System.currentTimeMillis()-currentTime>5000){
                Log.d("if","if");
                break;
            }
        }
        return resultPostsList;
    }


    public  void getCount(){
        BmobQuery<PostsBean> query = new BmobQuery<PostsBean>();
        query.count(getActivity(), PostsBean.class,new CountListener() {
            @Override
            public void onFailure(int i, String s) {
                Log.d("bmbo","查询失败"+s);
            }

            @Override
            public void onSuccess(int i) {
                totalCount=i;
            }
        });
    }

    private class refreshPostsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            refreshPosts();
        }
    }

    private class publishNewPostsListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(getActivity(), PublishNewPostsActivity.class);
            startActivityForResult(intent,0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                refreshPosts();
            default:
                break;
        }
    }
}
