package com.weilun.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tb.emoji.Emoji;
import com.tb.emoji.EmojiUtil;
import com.tb.emoji.FaceFragment;
import com.weilun.adapter.ChatAdapter;
import com.weilun.bean.ChatMessageBean;
import com.weilun.bean.UserBean;
import com.weilun.loader.LoadImgFromUrlAsyncTask;
import com.weilun.threadpool.ThreadPool;
import com.weilun.ui.PullRefreshListView;
import com.weilun.usermessage.UserChatMessage;
import com.weilun.util.ImSdkUtil;
import com.weilun.weilun.R;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetServerTimeListener;
import imsdk.data.IMMyself;
import imsdk.data.recentcontacts.IMMyselfRecentContacts;

public class FriendsChatActivity extends AppCompatActivity implements FaceFragment.OnEmojiClickListener {

    private FragmentTransaction transaction;
    private FaceFragment faceFragment;
    private static final int REQUEST_IMAGE = 0;
    private EditText mFriendsChatEditText;
    private InputMethodManager mInputMethodManager;
    private Button mSendButton;
    private TextView mFriendsNameTextView;
    private PullRefreshListView mChatListView;
    ChatAdapter chatAdapter;

    private List<ChatMessageBean> chatMsgs;


    private UserBean friend;
    private UserBean currentUser;

    private int start=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_chat);
        currentUser = BmobUser.getCurrentUser(this, UserBean.class);

        new LoadImgFromUrlAsyncTask(currentUser.getImg()).
                executeOnExecutor(ThreadPool.getThreadPool(),currentUser.getImg());

        initView();

        setContent();
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // 设置监听器
        IMMyself.setOnReceiveTextListener(new IMMyself.OnReceiveTextListener() {
            // 监听来自其他用户的文本讯息
            @Override
            public void onReceiveText(String text, String fromCustomUserID, long serverActionTime) {

                Log.d("onReceiveText","收到消息:"+text);

                if (fromCustomUserID.equals(friend.getMobilePhoneNumber())) {
                    ChatMessageBean message = new ChatMessageBean();
                    message.setBelongUserMobileNo(fromCustomUserID);
                    message.setContent(text);
                    message.setTargetUserMobileNo(currentUser.getMobilePhoneNumber());
                    message.setSendTime(String.valueOf(serverActionTime*1000));

                    chatAdapter.add(message);
                    mChatListView.setSelection(chatAdapter.getCount() - 1);
                }
            }

            // 监听系统消息
            @Override
            public void onReceiveSystemText(String text, long serverActionTime) {
                Toast.makeText(FriendsChatActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        IMMyselfRecentContacts.clearUnreadChatMessage(friend.getMobilePhoneNumber());
    }

    void initView() {
        faceFragment = FaceFragment.Instance();
        mFriendsChatEditText = (EditText) findViewById(R.id.edit_user_comment);
        mFriendsChatEditText.setOnTouchListener(new chatEditTextOnTouchListener());

        mFriendsChatEditText.addTextChangedListener(new ChatEditTextChangeListener());

        mInputMethodManager = (InputMethodManager) getSystemService(FriendsChatActivity.INPUT_METHOD_SERVICE);

        mSendButton = (Button) findViewById(R.id.btn_chat_send);
        mFriendsNameTextView = (TextView) findViewById(R.id.friendsName);

        mChatListView = (PullRefreshListView) findViewById(R.id.chatMessageListView);

        mChatListView.setonRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new loadMoreChatMsgAsyncTask().executeOnExecutor(ThreadPool.getThreadPool());
                mChatListView.onRefreshComplete();
            }
        });

    }

    void setContent() {
        Intent intent = this.getIntent();
        friend = (UserBean) intent.getSerializableExtra("friends");
        mFriendsNameTextView.setText(friend.getUsername());
        new LoadMsgAsyncTask().executeOnExecutor(ThreadPool.getThreadPool());

        new LoadImgFromUrlAsyncTask(friend.getImg()).
                executeOnExecutor(ThreadPool.getThreadPool(),friend.getImg());

    }


    public void sendChatMessage(View view) {
        final String chatMessage = mFriendsChatEditText.getEditableText().toString();
        if (chatMessage == null || chatMessage.length() < 1) {
            return;
        }
        mFriendsChatEditText.setText("");


        Bmob.getServerTime(this, new GetServerTimeListener() {
            @Override
            public void onSuccess(long time) {
                // TODO Auto-generated method stub
                ChatMessageBean message = new ChatMessageBean();
                message.setBelongUserMobileNo(currentUser.getMobilePhoneNumber());
                message.setContent(chatMessage);
                message.setSendTime(String.valueOf(time*1000L));
                message.setTargetUserMobileNo(friend.getMobilePhoneNumber());

                message.save(FriendsChatActivity.this);

                ImSdkUtil.sendTextMessage(friend.getMobilePhoneNumber(), chatMessage);

                chatAdapter.add(message);
                mChatListView.setSelection(chatAdapter.getCount() - 1);
            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(FriendsChatActivity.this,"发送失败，请重新发送",Toast.LENGTH_SHORT).show();
                mFriendsChatEditText.setText(chatMessage);
                Log.i("bmob","获取服务器时间失败:" + msg);
            }
        });

    }


    private class ChatEditTextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                mSendButton.setVisibility(View.VISIBLE);
            } else {
                mSendButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    public void selectImg(View view) {

        Intent intent = new Intent(FriendsChatActivity.this, MultiImageSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

        // 最大图片选择数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);

        // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public void selectEmoji(View view) {
        transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!faceFragment.isAdded()) {
            mInputMethodManager.hideSoftInputFromWindow(mFriendsChatEditText.getWindowToken(), 0);
            transaction.add(R.id.Container, faceFragment).commit();
        } else if (faceFragment.isHidden()) {
            mInputMethodManager.hideSoftInputFromWindow(mFriendsChatEditText.getWindowToken(), 0);
            transaction.show(faceFragment).commit();
        } else {
            transaction.hide(faceFragment).commit();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (faceFragment.isAdded() && !faceFragment.isHidden()) {
                transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.hide(faceFragment).commit();
            } else {
                cancel(null);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void cancel(View view) {
        FriendsChatActivity.this.finish();
    }

    @Override
    public void onEmojiDelete() {
        EditText editText = mFriendsChatEditText;
        int index = editText.getSelectionStart();
        int length = EmojiUtil.deleteEmoji(editText, index);
        String str = editText.getText().toString();
        EmojiUtil.displayTextView(editText, str, FriendsChatActivity.this);
        editText.setSelection(index - length);
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        if (emoji != null) {
            EditText editText = mFriendsChatEditText;
            StringBuffer editString = new StringBuffer(editText.getText());
            int index = editText.getSelectionStart();
            if (index < 0) {
                editString.append(emoji.getContent());
            } else {
                editString.insert(index, emoji.getContent());
            }

            EmojiUtil.displayTextView(editText, editString.toString(), FriendsChatActivity.this);
            editText.setSelection(index + emoji.getContent().length());
        }
    }

    private class chatEditTextOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (faceFragment.isAdded() && !faceFragment.isHidden()) {
                transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.hide(faceFragment).commit();
            }
            return false;
        }
    }


    /**
     * 加载消息历史，从数据库中读出
     */
    private List<ChatMessageBean> initMsgData() {
        return UserChatMessage.getChatMsgListOfFriend(currentUser.getMobilePhoneNumber(),friend.getMobilePhoneNumber(),0);
    }


    private class loadMoreChatMsgAsyncTask extends AsyncTask<Void,Void,List<ChatMessageBean>>{

        @Override
        protected List<ChatMessageBean> doInBackground(Void... params) {
            start++;
            Log.d("loadMore","start:"+start);
            return UserChatMessage.getChatMsgListOfFriend(currentUser.getMobilePhoneNumber(),
                    friend.getMobilePhoneNumber(),start*30);
        }

        @Override
        protected void onPostExecute(List<ChatMessageBean> chatMessageBeen) {
            super.onPostExecute(chatMessageBeen);

            int length=chatMessageBeen.size();
            chatMessageBeen.addAll(chatMsgs);
            chatMsgs=chatMessageBeen;


            for(int i=0;i<chatMsgs.size();i++) {
                Log.d("loadMore", "chatMessageBeen:" + chatMsgs.get(i).getContent());
            }

            chatAdapter = new ChatAdapter(FriendsChatActivity.this, chatMessageBeen,currentUser.getImg(),friend.getImg());
            mChatListView.setAdapter(chatAdapter);

            mChatListView.setSelection(length - 1);
        }
    }

    private class LoadMsgAsyncTask extends AsyncTask<Void,Void,List<ChatMessageBean>>{

        @Override
        protected List<ChatMessageBean> doInBackground(Void... params) {
            return initMsgData();
        }

        @Override
        protected void onPostExecute(List<ChatMessageBean> chatMessageBeen) {
            super.onPostExecute(chatMessageBeen);

            chatMsgs=chatMessageBeen;

            chatAdapter = new ChatAdapter(FriendsChatActivity.this, chatMessageBeen,currentUser.getImg(),friend.getImg());
            mChatListView.setAdapter(chatAdapter);

            mChatListView.setSelection(chatAdapter.getCount() - 1);
        }
    }
}
