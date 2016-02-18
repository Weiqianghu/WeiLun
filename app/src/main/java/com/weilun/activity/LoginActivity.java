package com.weilun.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.weilun.bean.UserBean;
import com.weilun.ui.ClearEditText;
import com.weilun.util.ImSdkUtil;
import com.weilun.weilun.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import imsdk.data.IMMyself;

public class LoginActivity extends AppCompatActivity {

    private String password;
    private String mobileNo;

    private ClearEditText mobileNoText;
    private ClearEditText passwordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        passwordText=(ClearEditText)findViewById(R.id.password);
        mobileNoText=(ClearEditText)findViewById(R.id.mobileNo);
    }

    public void login(View view){
        mobileNo=mobileNoText.getText().toString();
        password=passwordText.getText().toString();
        if(mobileNo.length()<1||password.length()<1)
            return;

        BmobUser.loginByAccount(this, mobileNo, password, new LogInListener<UserBean>() {

            @Override
            public void done(UserBean userBean, BmobException e) {
                if (userBean != null) {


                    ImSdkUtil.imLogin(mobileNo);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void forgetPassword(View view){
        Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
        startActivity(intent);
    }

    public void cancel(View view){
        LoginActivity.this.finish();
    }
}
