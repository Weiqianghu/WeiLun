package com.weilun.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tb.emoji.Emoji;
import com.tb.emoji.EmojiUtil;
import com.tb.emoji.FaceFragment;
import com.weilun.bean.CommentBean;
import com.weilun.bean.PostsBean;
import com.weilun.bean.UserBean;
import com.weilun.weilun.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class ReviewActivity extends AppCompatActivity implements FaceFragment.OnEmojiClickListener{

    private EditText mReviewContentEditText;
    private boolean isFirst=true;
    private int resultCode=0;
    protected Intent intentBack=new Intent();
    private Button submit;

    private FaceFragment faceFragment;
    private FragmentTransaction transaction;
    private ImageView mEmoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        if(isFirst) {
            initView();
            isFirst=false;
        }
    }

    private void initView() {
        mReviewContentEditText= (EditText) findViewById(R.id.reviewContent);
        submit= (Button) findViewById(R.id.btn_Submit);
        submit.setOnClickListener(new submitClickListener());

        faceFragment = FaceFragment.Instance();
        mEmoji = (ImageView) findViewById(R.id.emoji);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (faceFragment.isAdded() && !faceFragment.isHidden()) {
                transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.hide(faceFragment).commit();
                mEmoji.setImageResource(R.drawable.emoji);
            } else {
                cancel(null);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void cancel(View view){
        ReviewActivity.this.finish();
    }

    @Override
    public void onEmojiDelete() {
        EditText editText=mReviewContentEditText;
        int index=editText.getSelectionStart();
        int length=EmojiUtil.deleteEmoji(editText,index);
        String str=editText.getText().toString();
        EmojiUtil.displayTextView(editText,str ,ReviewActivity.this);
        editText.setSelection(index-length);
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        if (emoji != null) {
            EditText editText = mReviewContentEditText;
            StringBuffer editString = new StringBuffer(editText.getText());
            int index = editText.getSelectionStart();
            if (index < 0) {
                editString.append(emoji.getContent());
            } else {
                editString.insert(index, emoji.getContent());
            }

            EmojiUtil.displayTextView(editText, editString.toString(),ReviewActivity.this);
            editText.setSelection(index + emoji.getContent().length());
        }
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

    private class submitClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String content=mReviewContentEditText.getEditableText().toString();
            if(content==null || content.length()<1)
            {
                return;
            }
            submit.setClickable(false);
            Toast.makeText(ReviewActivity.this,"发表中",Toast.LENGTH_LONG).show();
            UserBean  currentUser= BmobUser.getCurrentUser(ReviewActivity.this, UserBean.class);
            Intent intent=getIntent();
            String postsObjectId=intent.getStringExtra("postsObjectId");

            PostsBean postsBean=new PostsBean();
            postsBean.setObjectId(postsObjectId);

            final CommentBean comment = new CommentBean();
            comment.setContent(content);
            comment.setAuthor(currentUser);
            comment.setPosts(postsBean);

            comment.save(ReviewActivity.this, new SaveListener() {

                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    Log.i("life","评论发表成功");
                }

                @Override
                public void onFailure(int code, String msg) {
                    // TODO Auto-generated method stub
                    Log.i("life","评论失败");
                }
            });
            intentBack.putExtra("review","success");
            ReviewActivity.this.setResult(resultCode,intentBack);
            cancel(v);
        }
    }

    public void selectImg(View view){

    }
}
