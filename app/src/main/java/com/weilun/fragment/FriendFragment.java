package com.weilun.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.viewbadger.BadgeView;
import com.weilun.adapter.FriendsAdapter;

import com.weilun.bean.FriendsBean;

import com.weilun.bean.UserBean;
import com.weilun.threadpool.ThreadPool;
import com.weilun.ui.PullRefreshListView;
import com.weilun.weilun.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import imsdk.data.IMMyself;


public class FriendFragment extends Fragment {

    private List<UserBean> userList = new ArrayList<>();
    private List<String> friendObjectIdList = new ArrayList<>();
    private ListView mFriendsListView;
    private boolean isGetData = false;
    private FriendsAdapter friendsAdapter;
    private boolean isFresh = true;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (isFresh == true) {
            view = inflater.inflate(R.layout.fragment_friend, container, false);
            initView();

            refreshFriends();
            isFresh = false;
        } else {
            cacheView();
        }

        return view;
    }

    public void refreshFriends() {
        new FriendAsyncTask().executeOnExecutor(ThreadPool.getThreadPool());
    }

    public void refreshBdageView(){
        friendsAdapter = new FriendsAdapter(getActivity(), userList, mFriendsListView);
        mFriendsListView.setAdapter(friendsAdapter);
    }

    private void cacheView() {
        mFriendsListView.setAdapter(friendsAdapter);
    }

    private void initView() {
        mFriendsListView = (ListView) view.findViewById(R.id.friendsListView);

        /*mFriendsListView.setonRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFriends();
                mFriendsListView.onRefreshComplete();
            }
        });*/
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshBdageView();
    }


    class FriendAsyncTask extends AsyncTask<Void, Void, List<UserBean>> {
        @Override
        protected List<UserBean> doInBackground(Void... params) {
            return getFriendsData();
        }

        @Override
        protected void onPostExecute(List<UserBean> userBeen) {
            friendsAdapter = new FriendsAdapter(getActivity(), userBeen, mFriendsListView);
            mFriendsListView.setAdapter(friendsAdapter);
            super.onPostExecute(userBeen);
        }
    }

    private List<UserBean> getFriendsData() {
        UserBean currentUser = BmobUser.getCurrentUser(getActivity(), UserBean.class);
        BmobRelation userRelation = new BmobRelation();
        userRelation.add(currentUser);


        BmobQuery<FriendsBean> objectQuery = new BmobQuery<>();
        objectQuery.addWhereEqualTo("user", currentUser);
        objectQuery.findObjects(getActivity(), new FindListener<FriendsBean>() {
            @Override
            public void onSuccess(List<FriendsBean> list) {
                for (int i = 0, length = list.size(); i < length; i++) {
                    friendObjectIdList.add(list.get(i).getObjectId());
                    isGetData = true;
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d("life", "objectid查询失败");
            }
        });


        long currentTime = System.currentTimeMillis();
        while (!isGetData) {
            if (System.currentTimeMillis() - currentTime > 5000) {
                break;
            }
        }
        isGetData = false;

        for (int i = 0, size = friendObjectIdList.size(); i < size; i++) {
            BmobQuery<UserBean> query = new BmobQuery<>();
            FriendsBean friends = new FriendsBean();
            friends.setObjectId(friendObjectIdList.get(i));

            query.addWhereRelatedTo("friend", new BmobPointer(friends));
            query.findObjects(getActivity(), new FindListener<UserBean>() {
                @Override
                public void onSuccess(List<UserBean> list) {
                    Log.i("life", "查询成功：" + list.size());
                    userList.clear();
                    for (int i = 0, length = list.size(); i < length; i++) {
                        userList.add(list.get(i));
                        Log.d("life", "userList赋值");
                    }
                    isGetData = true;
                }

                @Override
                public void onError(int code, String msg) {
                    // TODO Auto-generated method stub
                    Log.i("life", "查询失败：" + code + "-" + msg);
                }
            });
        }
        currentTime = System.currentTimeMillis();
        while (!isGetData) {
            if (System.currentTimeMillis() - currentTime > 5000) {
                break;
            }
        }
        isGetData = false;
        return userList;
    }

}
