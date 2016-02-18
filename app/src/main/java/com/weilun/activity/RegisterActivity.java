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

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

public class RegisterActivity extends AppCompatActivity {

    private String mobilePhoneNumber;
    private String username;
    private String password;
    private String security;

    private ClearEditText mobileNo;
    private ClearEditText securityCode;
    private ClearEditText passwordEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

    }

    private void initView() {
        mobileNo=(ClearEditText)findViewById(R.id.mobileNo);
        securityCode= (ClearEditText) findViewById(R.id.securityCode);
        passwordEt=(ClearEditText)findViewById(R.id.password);
    }

    public void getNewDate(){
        mobilePhoneNumber=mobileNo.getText().toString();
        username=mobilePhoneNumber;
        security=securityCode.getText().toString();
        password=passwordEt.getText().toString();
    }

    public void cancel(View view){
        RegisterActivity.this.finish();
    }

    public void register(View view){
        getNewDate();
        if(mobilePhoneNumber.equals("") || security.equals("") || password.length()<6){
            Toast.makeText(RegisterActivity.this, "密码长度最小6位", Toast.LENGTH_SHORT).show();
            return;
        }

        BmobSMS.verifySmsCode(this,mobilePhoneNumber, security, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if(ex==null){//短信验证码已验证成功
                    Log.i("smile", "验证通过");
                }else{
                    Log.i("smile", "验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                    if(ex.getErrorCode()==207){
                        Toast.makeText(RegisterActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        final UserBean user=new UserBean();
        user.setUsername(mobilePhoneNumber);
        user.setMobilePhoneNumberVerified(true);
        user.setMobilePhoneNumber(mobilePhoneNumber);
        user.setPassword(password);
        user.setImg("http://file.bmob.cn/M02/E3/AB/oYYBAFZmgSCAKD2FAAAEdkC6sfc044.jpg");


        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {

                user.login(RegisterActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {

                        ImSdkUtil.imLogin(mobilePhoneNumber);

                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);
                        RegisterActivity.this.finish();
                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        // TODO Auto-generated method stub
                        Toast.makeText(RegisterActivity.this,"登录失败:"+msg,Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Toast.makeText(RegisterActivity.this,"注册失败:" + msg,Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    public void sendMessage(View view){
        getNewDate();
        if(mobilePhoneNumber==null || mobilePhoneNumber.equals("")){
            return;
        }
        BmobSMS.requestSMSCode(RegisterActivity.this, mobilePhoneNumber, "注册模版",new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if(e==null){//验证码发送成功
                    Log.i("smile", "短信id："+integer);//用于查询本次短信发送详情
                    Toast.makeText(RegisterActivity.this, "短信发送成功", Toast.LENGTH_SHORT).show();
                }
                else
                    Log.i("smile",""+e);
            }
        });
    }
}
