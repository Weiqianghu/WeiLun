package com.weilun.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bmob.BmobPro;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.readystatesoftware.viewbadger.BadgeView;
import com.weilun.bean.UserBean;
import com.weilun.fragment.FriendFragment;
import com.weilun.fragment.HomePageFragment;
import com.weilun.fragment.MeFragment;
import com.weilun.threadpool.ThreadPool;
import com.weilun.usermessage.UserReviewRemind;
import com.weilun.util.ImgUtil;
import com.weilun.util.UserUtil;
import com.weilun.weilun.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import imsdk.data.IMMyself;
import imsdk.data.recentcontacts.IMMyselfRecentContacts;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private BadgeView meBadge;
    private BadgeView friendBadge;
    private static long chatMessageCount=0;

    public static MainActivity mainActivity;

    private static final int REQUEST_IMAGE = 0;

    private ViewPager mViewPage;
    private int count = 1;

    private LinearLayout mTabHomePage;
    private LinearLayout mTabFriend;
    private LinearLayout mTabMe;

    private ImageView mImgHomePage;
    private ImageView mImgFriend;
    private ImageView mImgMe;

    private FragmentPagerAdapter mPagerAdapter;
    private List<Fragment> mViews = new ArrayList<>();


    FriendFragment mFriendFragment ;


    private boolean isFrist = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        if (isFrist == true) {
            initView();
            isFrist = false;
        }

        initEvent();
        setSelect(0);
        new ShowUserMessageRemindAsyncTask().executeOnExecutor(ThreadPool.getThreadPool());
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub


        if(chatMessageCount>0) {
            if (friendBadge == null) {
                friendBadge = new BadgeView(MainActivity.this, mImgFriend);
                friendBadge.setText(String.valueOf(chatMessageCount));
                friendBadge.show();
            } else {
                friendBadge.setText(String.valueOf(chatMessageCount));
                friendBadge.show();
            }
        }

        super.onStart();
        // 设置监听器
        IMMyself.setOnReceiveTextListener(new IMMyself.OnReceiveTextListener() {
            // 监听来自其他用户的文本讯息
            @Override
            public void onReceiveText(String text, String fromCustomUserID, long serverActionTime) {

                mFriendFragment.refreshBdageView();

                chatMessageCount++;

                if(friendBadge==null){
                    friendBadge = new BadgeView(MainActivity.this,mImgFriend);
                    friendBadge.setText(String.valueOf(chatMessageCount));
                    friendBadge.show();
                }else {
                    friendBadge.setText(String.valueOf(chatMessageCount));
                    friendBadge.show();
                }
            }

            // 监听系统消息
            @Override
            public void onReceiveSystemText(String text, long serverActionTime) {
                Toast.makeText(mainActivity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initView() {

        chatMessageCount= IMMyselfRecentContacts.getUnreadChatMessageCount();

        mViewPage = (ViewPager) findViewById(R.id.mContener);

        mTabHomePage = (LinearLayout) findViewById(R.id.homePageView);
        mTabFriend = (LinearLayout) findViewById(R.id.friendView);
        mTabMe = (LinearLayout) findViewById(R.id.meView);

        mImgHomePage = (ImageView) findViewById(R.id.homePageImage);
        mImgFriend = (ImageView) findViewById(R.id.friendImage);
        mImgMe = (ImageView) findViewById(R.id.meImage);


        Fragment homePageFragment = new HomePageFragment();
        Fragment friendFragment = mFriendFragment=new FriendFragment();
        Fragment meFragment = new MeFragment();

        mViews.add(homePageFragment);
        mViews.add(friendFragment);
        mViews.add(meFragment);


        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mViews.get(position);
            }

            @Override
            public int getCount() {
                return mViews.size();
            }
        };

        mViewPage.setAdapter(mPagerAdapter);

        mViewPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int mCurrentItem = mViewPage.getCurrentItem();
                setSelect(mCurrentItem);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initEvent() {
        mTabHomePage.setOnClickListener(this);
        mTabFriend.setOnClickListener(this);
        mTabMe.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homePageView:
                setSelect(0);
                break;
            case R.id.friendView:
                setSelect(1);
                break;
            case R.id.meView:
                setSelect(2);
                break;
        }
    }

    private void setSelect(int i) {
        resetImg();
        switch (i) {
            case 0:
                mViewPage.setCurrentItem(0);
                mImgHomePage.setImageResource(R.drawable.homepagepressed);
                break;
            case 1:
                mViewPage.setCurrentItem(1);
                mImgFriend.setImageResource(R.drawable.friendpressed);

                if (friendBadge != null && friendBadge.isShown()) {
                    friendBadge.hide();
                    chatMessageCount=0;
                }

                break;
            case 2:
                mViewPage.setCurrentItem(2);
                mImgMe.setImageResource(R.drawable.mepressed);

                if (meBadge != null && meBadge.isShown()) {
                    meBadge.hide();
                }

                break;
        }

    }

    private void resetImg() {
        mImgHomePage.setImageResource(R.drawable.homepagenormal);
        mImgFriend.setImageResource(R.drawable.friendnormal);
        mImgMe.setImageResource(R.drawable.menormal);
    }


    public void gotoSetting(View view) {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    public void exit(View v) {
        if (count < 2) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            count++;
            new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                        count = 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            this.finish();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit(null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void selectImg(View view) {
        Intent intent = new Intent(MainActivity.this, MultiImageSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

        // 最大图片选择数量
        //intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);

        // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....
                if (paths.size() > 0) {
                    String imgPath = paths.get(0);

                    ImageView imageView = (ImageView) findViewById(R.id.userImg);
                    Bitmap smallImg = ImgUtil.getSmallBitmap(imgPath);
                    imageView.setImageBitmap(smallImg);
                    imgPath = ImgUtil.saveBitmapTo(smallImg);
                    if (imgPath == null) {
                        return;
                    }


                    BmobProFile.getInstance(MainActivity.this).upload(imgPath, new UploadListener() {
                        @Override
                        public void onSuccess(String fileName, String url, BmobFile file) {
                            Log.i("bmob", "文件上传成功：" + fileName + ",可访问的文件地址：" + file.getUrl());
                            UserBean userBean = new UserBean();
                            userBean.setImg(file.getUrl());
                            UserUtil.updateUser(MainActivity.this, userBean);
                            BmobPro.getInstance(MainActivity.this).clearCache();
                        }

                        @Override
                        public void onProgress(int progress) {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "onProgress :" + progress);
                        }

                        @Override
                        public void onError(int statuscode, String errormsg) {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "文件上传失败：" + errormsg);
                            BmobPro.getInstance(MainActivity.this).clearCache();
                        }
                    });
                }
            }
        }
    }

    public void editMyInfo(View view) {
        Intent intent = new Intent(MainActivity.this, EditMyInfoActivity.class);
        startActivity(intent);
    }

    class ShowUserMessageRemindAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (Integer.valueOf(s) > 0) {
                meBadge = new BadgeView(MainActivity.this, mImgMe);
                meBadge.setText(s);
                meBadge.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            return UserReviewRemind.getUserReviewCount(MainActivity.this);
        }
    }
}
