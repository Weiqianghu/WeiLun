package com.weilun.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tb.emoji.EmojiUtil;
import com.weilun.adapter.CommentsAdapter;
import com.weilun.adapter.PostsImgAdapter;
import com.weilun.asynctask.ImgAsyncTask;
import com.weilun.bean.CommentBean;
import com.weilun.bean.PostsBean;
import com.weilun.bean.UserBean;
import com.weilun.loader.ImageLoader;
import com.weilun.threadpool.ThreadPool;
import com.weilun.ui.ListViewForScrollView;
import com.weilun.util.AddFriendUtil;
import com.weilun.weilun.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class PostsDetailsActivity extends AppCompatActivity {

    private TextView mPostsTitleTextView;
    private TextView mPostsContentTextView;
    private TextView mPostsUpdateDate;
    private TextView mPostsUserNmae;
    private ImageView mPostsUserSex;
    private ImageView mPostsUserImg;
    private ImageView mAddFriendByCommentsAuthor;
    private ImageView mAddFriendByPostsAuthor;

    private ListViewForScrollView mCommentsListView;
    private ScrollView mCommentsScrollView;

    private View loadingView;

    private String postsObjectId;

    private boolean isFirst = true;

    private UserBean author;

    private CommentsAdapter commentsAdapter;
    private int totalCount = 0;
    private List<CommentBean> resultCommentsList = new ArrayList<>();
    private Boolean isGetData = false;

    private ListViewForScrollView mPostsImgListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_details);
        if (isFirst == true) {
            initView();
        }

        setContent();

        refreshComment();
    }

    private void refreshComment() {
        new CommentsAsyncTask().executeOnExecutor(ThreadPool.getThreadPool(), postsObjectId);
        mCommentsListView.setAdapter(commentsAdapter);
        mCommentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PostsDetailsActivity.this,"添加好友成功",Toast.LENGTH_SHORT).show();
                UserBean currentUser= BmobUser.getCurrentUser(PostsDetailsActivity.this,UserBean.class);
                add(PostsDetailsActivity.this,currentUser, resultCommentsList.get(position).getAuthor());
            }
        });
    }

    private void setContent() {

        loadingView = getLayoutInflater().inflate(R.layout.loadnext, null);
        loadingView.setOnClickListener(new loadMoreClickListener());
        mCommentsListView.addFooterView(loadingView);

        Intent intent = this.getIntent();
        PostsBean postsBean= (PostsBean) intent.getSerializableExtra("posts");

        String postsTitleStr=postsBean.getTitle();
        String postsContentStr=postsBean.getContent();
        String postsUpdateDate=postsBean.getUpdatedAtStr().split("\\s")[0];
        author=postsBean.getAuthor();
        String postsUserName = author.getUsername();
        String postsUserImg = author.getImg();
        postsObjectId=postsBean.getObjectId();
        List<String> postsImgUrls=postsBean.getPostsImg();

        if(postsImgUrls!=null && postsImgUrls.size()>0){
            PostsImgAdapter postsImgAdapter=new PostsImgAdapter(PostsDetailsActivity.this,
                    postsImgUrls,mPostsImgListView);
            mPostsImgListView.setAdapter(postsImgAdapter);
        }

        Boolean tempSex = author.getSex();
        if (tempSex == null) {
            mPostsUserSex.setImageResource(R.drawable.unknow);
        } else if (tempSex == true) {
            mPostsUserSex.setImageResource(R.drawable.male);
        } else {
            mPostsUserSex.setImageResource(R.drawable.female);
        }

        if (postsUserImg != null) {
            ImgAsyncTask newsAsyncTask = new ImgAsyncTask(mPostsUserImg, PostsDetailsActivity.this);
            newsAsyncTask.executeOnExecutor(ThreadPool.getThreadPool(), postsUserImg);
        }

        EmojiUtil.displayTextView(mPostsTitleTextView,postsTitleStr,PostsDetailsActivity.this);
        EmojiUtil.displayTextView(mPostsContentTextView,postsContentStr,PostsDetailsActivity.this);
        mPostsUpdateDate.setText(postsUpdateDate);
        mPostsUserNmae.setText(postsUserName);

        mCommentsScrollView.smoothScrollBy(0, 0);

        refreshComment();
    }

    private void initView() {
        mPostsTitleTextView = (TextView) findViewById(R.id.postsTitle);
        mPostsContentTextView = (TextView) findViewById(R.id.postsContent);
        mPostsUpdateDate = (TextView) findViewById(R.id.postsDate);
        mPostsUserNmae = (TextView) findViewById(R.id.userName);
        mPostsUserSex = (ImageView) findViewById(R.id.userSex);
        mPostsUserImg = (ImageView) findViewById(R.id.userImg);
        mCommentsScrollView = (ScrollView) findViewById(R.id.commentsScrollView);

        mAddFriendByPostsAuthor= (ImageView) findViewById(R.id.addFriendByPostsAuthor);
        mAddFriendByPostsAuthor.setOnClickListener(new addFriendByPostsAuthorListener());

        mCommentsListView = (ListViewForScrollView) findViewById(R.id.postsComments);

        mPostsImgListView= (ListViewForScrollView) findViewById(R.id.postsImgListView);

    }


    class CommentsAsyncTask extends AsyncTask<String, Void, List<CommentBean>> {
        @Override
        protected List<CommentBean> doInBackground(String... params) {
            return getCommentsData(params[0]);
        }

        @Override
        protected void onPreExecute() {
            getCount(postsObjectId);
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(List<CommentBean> commentBean) {
            isGetData=false;
            commentsAdapter = new CommentsAdapter(PostsDetailsActivity.this, commentBean,
                    mCommentsListView, totalCount, loadingView, postsObjectId);
            mCommentsListView.setAdapter(commentsAdapter);
            //progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(commentBean);
        }
    }

    private List<CommentBean> getCommentsData(String postsObjectid) {
        PostsBean postsBean = new PostsBean();
        postsBean.setObjectId(postsObjectid);

        BmobQuery<CommentBean> query = new BmobQuery<>();
        query.setLimit(10);
        query.addWhereEqualTo("posts", new BmobPointer(postsBean));
        query.include("author");
        query.order("-updatedAt");

        query.findObjects(PostsDetailsActivity.this, new FindListener<CommentBean>() {
            @Override
            public void onSuccess(List<CommentBean> object) {
                // TODO Auto-generated method stub
                resultCommentsList.clear();
                for (CommentBean comments : object) {
                    CommentBean commentBean = new CommentBean();
                    commentBean.setContent(comments.getContent());
                    commentBean.setAuthor(comments.getAuthor());
                    commentBean.setUpdatedAtStr(comments.getUpdatedAt());
                    resultCommentsList.add(commentBean);
                }
                Log.d("bmob", "查询成功：共" + object.size() + "条数据。");
                isGetData = true;
            }

            @Override
            public void onError(int code, String msg) {
                Log.d("bmob", "帖子数据查询失败：" + msg);
            }
        });


        long currentTime = System.currentTimeMillis();
        while (!isGetData) {
            if (System.currentTimeMillis() - currentTime > 5000) {
                Log.d("if", "if");
                break;
            }
        }
        return resultCommentsList;
    }


    public void getCount(String postsObjectid) {
        PostsBean postsBean = new PostsBean();
        postsBean.setObjectId(postsObjectid);

        BmobQuery<CommentBean> query = new BmobQuery<CommentBean>();
        query.addWhereEqualTo("posts", new BmobPointer(postsBean));

        query.count(PostsDetailsActivity.this, CommentBean.class, new CountListener() {
            @Override
            public void onFailure(int i, String s) {
                Log.d("bmbo", "查询失败" + s);
            }

            @Override
            public void onSuccess(int i) {
                totalCount = i;
            }
        });
    }


    public void review(View view) {
        Intent intent = new Intent(PostsDetailsActivity.this, ReviewActivity.class);
        intent.putExtra("postsObjectId", postsObjectId);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 根据上面发送过去的请求吗来区别
        switch (requestCode) {
            case 0:
                refreshComment();
            default:
                break;
        }
    }

    class loadMoreClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(commentsAdapter!=null)
                commentsAdapter.loadMore(postsObjectId);
        }
    }

    private class addFriendByPostsAuthorListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Toast.makeText(PostsDetailsActivity.this,"添加好友成功",Toast.LENGTH_SHORT).show();
            UserBean currentUser= BmobUser.getCurrentUser(PostsDetailsActivity.this,UserBean.class);
            add(PostsDetailsActivity.this,currentUser,author);
        }
    }
    private void add(Context context,UserBean currentUser, UserBean friendBean){
        AddFriendUtil.addFriend(context,currentUser,friendBean);
    }
}
