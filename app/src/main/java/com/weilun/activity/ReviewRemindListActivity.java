package com.weilun.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.weilun.adapter.ReviewRemindAdapter;
import com.weilun.bean.PostsBean;
import com.weilun.threadpool.ThreadPool;
import com.weilun.usermessage.UserReviewRemind;
import com.weilun.weilun.R;

import java.util.List;

public class ReviewRemindListActivity extends AppCompatActivity {

    private ListView mReviewRemindPostsListView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_remind_list);

        initView();

        new ReviewRemindPostsAsyncTask().executeOnExecutor(ThreadPool.getThreadPool());
    }

    private void initView(){
        mReviewRemindPostsListView= (ListView) findViewById(R.id.reviewRemindListView);
        mProgressBar= (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private class ReviewRemindPostsAsyncTask extends AsyncTask<Void,Void,List<PostsBean>>{

        @Override
        protected List<PostsBean> doInBackground(Void... params) {
            return UserReviewRemind.getPostsList(ReviewRemindListActivity.this);
        }

        @Override
        protected void onPostExecute(List<PostsBean> postsBeen) {
            super.onPostExecute(postsBeen);

            ReviewRemindAdapter reviewRemindAdapter=
                    new ReviewRemindAdapter(ReviewRemindListActivity.this,mReviewRemindPostsListView,postsBeen);
            mReviewRemindPostsListView.setAdapter(reviewRemindAdapter);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
