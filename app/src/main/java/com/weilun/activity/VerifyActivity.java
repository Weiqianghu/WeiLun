package com.weilun.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.weilun.bean.UserBean;
import com.weilun.util.ImSdkUtil;
import com.weilun.util.ImgUtil;
import com.weilun.util.NetUtil;
import com.weilun.weilun.R;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import imsdk.data.IMSDK;

public class VerifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        Bmob.initialize(this,"e71aa392fb7c6e9d1053e3c235395af9");


        // 初始化IMSDK
        // 在Application类onCreate()方法中，设置applicationContext和appKey
        IMSDK.init(getApplicationContext(), "825e8d776acfbfffac1b6dbf");


        new Thread() {
            public void run() {
                ImgUtil.deleteAllTempFiles(new File(ImgUtil.getSDPath() + "/tempImg/"));
            }
        }.start();
        Handler x = new Handler();
        x.postDelayed(new splashHandler(), 2000);
    }

    class splashHandler implements Runnable {

        public void run() {
            UserBean user = BmobUser.getCurrentUser(VerifyActivity.this, UserBean.class);
            if (!NetUtil.isNetworkAvailable(VerifyActivity.this)) {
                Intent intent = new Intent(VerifyActivity.this, NetContConnectActivity.class);
                startActivity(intent);
                VerifyActivity.this.finish();
            } else if (user != null) {

                ImSdkUtil.imLogin(user.getMobilePhoneNumber());


                Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                startActivity(intent);
                VerifyActivity.this.finish();
            } else {
                //缓存用户对象为空时， 可打开用户注册界面…
                Intent intent = new Intent(VerifyActivity.this, LoginAndRegisterActivity.class);
                startActivity(intent);
                VerifyActivity.this.finish();
            }
        }
    }
}
