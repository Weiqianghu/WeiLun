package com.weilun.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.weilun.weilun.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;
import imsdk.data.IMMyself;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void logout(View view){
        BmobUser.logOut(this);   //清除缓存用户对象
        Intent intent=new Intent(SettingActivity.this,LoginAndRegisterActivity.class);
        startActivity(intent);

        IMMyself.logout();

        MainActivity.mainActivity.finish();
        SettingActivity.this.finish();
    }

    public void checkVersion(View view){
        BmobUpdateAgent.forceUpdate(SettingActivity.this);
    }
}
