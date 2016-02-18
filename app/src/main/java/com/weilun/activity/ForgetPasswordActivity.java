package com.weilun.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.weilun.bean.UserBean;
import com.weilun.ui.ClearEditText;
import com.weilun.util.Number;
import com.weilun.weilun.R;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

public class ForgetPasswordActivity extends AppCompatActivity {

    private String mobileNo;
    private String code;

    private ClearEditText mobileNoText;
    private ClearEditText securityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initView();
    }

    private void initView() {
        mobileNoText = (ClearEditText) findViewById(R.id.mobileNo);
        securityCode = (ClearEditText) findViewById(R.id.securityCode);
    }

    private void getDate() {
        mobileNo = mobileNoText.getText().toString();
        code = securityCode.getText().toString();
    }

    public void requestSMSCode(View view) {
        getDate();
        if (mobileNo.length() != 11) {
            Toast.makeText(ForgetPasswordActivity.this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobSMS.requestSMSCode(ForgetPasswordActivity.this, mobileNo, "注册模版", new RequestSMSCodeListener() {

            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {//验证码发送成功
                    Log.i("smile", "短信id：" + integer);//用于查询本次短信发送详情
                    Toast.makeText(ForgetPasswordActivity.this, "短信发送成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void resetPasswordBySMSCode(final View view) {
        getDate();
        if (mobileNo.length() != 11 || "".equals(code)) {
            Toast.makeText(ForgetPasswordActivity.this, "请输入正确信息", Toast.LENGTH_SHORT).show();
            return;
        }

        final String newPassword = Number.getRandomNumber() + "";
        UserBean.resetPasswordBySMSCode(this, code, newPassword, new ResetPasswordByCodeListener() {

            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {

                    Log.i("smile", "密码重置成功");

                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
                    builder.setMessage("您的新密码为：" + newPassword + "请尽快登录修改密码");
                    builder.setTitle("重要提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                            cancel(view);
                        }
                    });
                    builder.create().show();
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("smile", "重置失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                }
            }
        });
    }

    public void cancel(View view) {
        ForgetPasswordActivity.this.finish();
    }
}
