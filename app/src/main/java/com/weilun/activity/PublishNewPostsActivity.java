package com.weilun.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobPro;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.LocalThumbnailListener;
import com.bmob.btp.callback.ThumbnailListener;
import com.bmob.btp.callback.UploadListener;
import com.tb.emoji.Emoji;
import com.tb.emoji.EmojiUtil;
import com.tb.emoji.FaceFragment;
import com.weilun.adapter.SelectImgGridAdapter;
import com.weilun.bean.Constant;
import com.weilun.bean.PostsBean;
import com.weilun.bean.SelectImgBean;
import com.weilun.bean.UserBean;
import com.weilun.threadpool.ThreadPool;
import com.weilun.util.ImgUtil;
import com.weilun.util.UserUtil;
import com.weilun.weilun.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;


public class PublishNewPostsActivity extends AppCompatActivity implements FaceFragment.OnEmojiClickListener {

    private Button mSubmit;
    private EditText mPostsTitle;
    private EditText mPostsContent;

    private GridView mSelectImgGridView;
    private List<SelectImgBean> imgBeanList = new ArrayList<>();

    private ImageView mSelectImg;

    private ProgressBar mPublishProgress;


    private String[] postsImgList = null;
    private List<String> imgUrlList = new ArrayList<>();

    private ImageView mEmoji;

    private FaceFragment faceFragment;
    private FragmentTransaction transaction;

    Boolean isUploadFinsh = false;


    private static final int REQUEST_IMAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_new_posts);

        initView();
    }

    private void initView() {
        mSubmit = (Button) findViewById(R.id.btn_Submit);
        mPostsTitle = (EditText) findViewById(R.id.postsTitle);
        mPostsContent = (EditText) findViewById(R.id.postsContent);
        mSelectImg = (ImageView) findViewById(R.id.selectImg);

        mSelectImgGridView = (GridView) findViewById(R.id.selectImgGridView);

        mPublishProgress = (ProgressBar) findViewById(R.id.publishProgress);

        mEmoji = (ImageView) findViewById(R.id.emoji);

        faceFragment = FaceFragment.Instance();


        mSubmit.setOnClickListener(new publishNewPostsClickListener());

        registerBroadCastReceiver();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (faceFragment.isAdded() && !faceFragment.isHidden()) {
                transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.hide(faceFragment).commit();
                mEmoji.setImageResource(R.drawable.emoji);
            } else {
                back(null);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void back(View view) {
        PublishNewPostsActivity.this.finish();
    }

    @Override
    public void onEmojiDelete() {
        EditText editText=getFocusEditText();
        int index=editText.getSelectionStart();
        int length=EmojiUtil.deleteEmoji(editText,index);
        String str=editText.getText().toString();
        EmojiUtil.displayTextView(editText,str ,PublishNewPostsActivity.this);
        editText.setSelection(index-length);
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        if (emoji != null) {
            EditText editText = getFocusEditText();
            StringBuffer editString = new StringBuffer(editText.getText());
            int index = editText.getSelectionStart();
            if (index < 0) {
                editString.append(emoji.getContent());
            } else {
                editString.insert(index, emoji.getContent());
            }

            EmojiUtil.displayTextView(editText, editString.toString(),PublishNewPostsActivity.this);
            editText.setSelection(index + emoji.getContent().length());
        }
    }

    private EditText getFocusEditText() {
        if (mPostsTitle.hasFocus()) {
            return mPostsTitle;
        } else {
            return mPostsContent;
        }
    }

    private class publishNewPostsClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            String title = mPostsTitle.getText().toString();
            String content = mPostsContent.getText().toString();
            if (content == null || content.length() < 1 || title == null ||
                    title.length() < 1) {
                return;
            }
            mSubmit.setClickable(false);


            mPublishProgress.setVisibility(View.VISIBLE);


            new publishPostsAsyncTask(v).executeOnExecutor(ThreadPool.getThreadPool(), imgBeanList);
        }
    }

    public void selectImg(View view) {
        if (imgBeanList.size() >= 9) {
            return;
        }
        Intent intent = new Intent(PublishNewPostsActivity.this, MultiImageSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

        // 最大图片选择数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9 - imgBeanList.size());

        // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public void selectEmoji(View view) {
        transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!faceFragment.isAdded()) {
            transaction.add(R.id.Container, faceFragment).commit();
            mEmoji.setImageResource(R.drawable.emoji_open);
        } else if (faceFragment.isHidden()) {
            transaction.show(faceFragment).commit();
            mEmoji.setImageResource(R.drawable.emoji_open);
        } else {
            transaction.hide(faceFragment).commit();
            mEmoji.setImageResource(R.drawable.emoji);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

                int length = paths.size();
                if (length < 1)
                    return;

                for (int i = 0; i < length && imgBeanList.size() < 9; i++) {
                    SelectImgBean imgBean = new SelectImgBean();
                    imgBean.setImgPath(paths.get(i));
                    imgBeanList.add(imgBean);
                }

                if (imgBeanList.size() > 8) {
                    mSelectImg.setImageResource(R.drawable.select_img_unclick);
                }

                SelectImgGridAdapter selectImgGridAdapter = new SelectImgGridAdapter(PublishNewPostsActivity.this
                        , mSelectImgGridView, imgBeanList);
                mSelectImgGridView.setAdapter(selectImgGridAdapter);


            }
        }
    }


    public void registerBroadCastReceiver() {
        SelectImgBroadCatsReceiver receiver = new SelectImgBroadCatsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.FLAG_SELECT_IMG);
        registerReceiver(receiver, filter);
    }

    private class SelectImgBroadCatsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSelectImg.setImageResource(R.drawable.select_img);
        }
    }


    private class publishPostsAsyncTask extends AsyncTask<List<SelectImgBean>, Void, List<String>> {

        View view;

        public publishPostsAsyncTask(View view) {
            this.view = view;
        }

        @Override
        protected List<String> doInBackground(List<SelectImgBean>... params) {
            final boolean[] isFinish = {false};
            List<SelectImgBean> imgPathList = params[0];
            final int length = imgPathList.size();

            if (length < 1)
                return imgUrlList;

            postsImgList = new String[length];
            Bitmap bitmap = null;
            for (int i = 0; i < length; i++) {
                bitmap = ImgUtil.getSmallBitmapForPostsImg(imgPathList.get(i).getImgPath());
                postsImgList[i] = ImgUtil.saveBitmapTo(bitmap);
            }

            BmobProFile.getInstance(PublishNewPostsActivity.this).
                    uploadBatch(postsImgList, new com.bmob.btp.callback.UploadBatchListener() {
                        @Override
                        public void onSuccess(boolean isFinish, String[] fileNames, String[] urls, BmobFile[] files) {
                            if (isFinish) {
                                for (int i = 0, length = files.length; i < length; i++) {
                                    imgUrlList.add(files[i].getUrl());
                                }
                                Log.d("shishi", "file:" + files[0].getUrl());
                                isUploadFinsh = true;
                            }
                        }

                        @Override
                        public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                            Log.i("bmob", "onProgress :" + curIndex + "---" + curPercent + "---" + total + "----" + totalPercent);
                        }

                        @Override
                        public void onError(int statuscode, String errormsg) {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "批量上传出错：" + statuscode + "--" + errormsg);
                            isUploadFinsh = true;
                        }
                    });
            while (!isUploadFinsh) {
            }

            return imgUrlList;
        }

        @Override
        protected void onPostExecute(List<String> imgUrlList) {

            String title = mPostsTitle.getText().toString();
            String content = mPostsContent.getText().toString();
            if (content == null || content.length() < 1 || title == null ||
                    title.length() < 1) {
                return;
            }
            mSubmit.setClickable(false);


            mPublishProgress.setVisibility(View.VISIBLE);

            UserBean currentUser = BmobUser.getCurrentUser(PublishNewPostsActivity.this, UserBean.class);

            PostsBean posts = new PostsBean();
            posts.setTitle(title);
            posts.setContent(content);
            posts.setAuthor(currentUser);
            posts.addAll("postsImg", imgUrlList);

            posts.save(PublishNewPostsActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    PublishNewPostsActivity.this.setResult(0);
                    mPublishProgress.setVisibility(View.GONE);
                    back(view);
                }

                @Override
                public void onFailure(int i, String s) {
                    PublishNewPostsActivity.this.setResult(1);
                    mPublishProgress.setVisibility(View.GONE);
                    back(view);
                }
            });

            super.onPostExecute(imgUrlList);
        }
    }

}
