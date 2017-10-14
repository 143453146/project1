package com.example.asus.test3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.test3.smss.MsgType;
import com.mob.MobSDK;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EventHandler  eventHandler;
    private Button btn_register, btn_send;
    private EditText et_usertel, et_password, et_code;
    int i = 30;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        init();

    }
    void initView(){
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_register = (Button) findViewById(R.id.btn_register);
        et_usertel = (EditText) findViewById(R.id.et_usertel);
        //et_password = (EditText) findViewById(R.id.et_password);
        et_code = (EditText) findViewById(R.id.et_code);
        et_usertel.addTextChangedListener(new LoginActivity.TextChange());
        //et_password.addTextChangedListener(new RegisterActivity.TextChange());
        btn_send.setEnabled(true);
        btn_send.setClickable(true);
        initListener();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
//                Toast.makeText(this, "---=====！",Toast.LENGTH_LONG).show();
                getCode();
                break;
            case R.id.btn_register:
                getRegister();
                break;
            default:
                break;
        }
    }
    protected void initListener() {
        //img_back.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_register.setOnClickListener(this);

    }
    void init(){
        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        //SMSSDK.setAskPermisionOnReadContact(boolShowInDialog);
        MobSDK.init(this, "219bf254255a9", "416ef27eafcdea78279e510559bb43cb");
        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result==SMSSDK.RESULT_ERROR) {
                    Message msg = new Message();
                    msg.arg1 = event;
                    msg.arg2 = result;
                    msg.obj = data;
                    msg.what=MsgType.CODE_ERROR;
                    handler.sendMessage(msg);
                }
                else {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        handler.sendEmptyMessage(SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE);

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        handler.sendEmptyMessage(SMSSDK.EVENT_GET_VERIFICATION_CODE);
                    } else {
//                            handler.sendEmptyMessage(SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE);
//                            ((Throwable) data).printStackTrace();
                    }
                }
                }
        };

        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    public void getRegister(){
        //将收到的验证码和手机号提交再次核对
        SMSSDK.submitVerificationCode("86", et_usertel.getText().toString(), et_code
                .getText().toString());

    }
    public void getCode() {
        // 1. 通过规则判断手机号
        if (MsgType.judgePhoneNums(this,et_usertel.getText().toString())) {
            // 2. 通过sdk发送短信验证
            if (!isExistUser()) {
                SMSSDK.getVerificationCode("86", et_usertel.getText().toString());

                // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                btn_send.setClickable(false);
                btn_send.setText("重新发送(" + i + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            handler.sendEmptyMessage(MsgType.SMSS_WAITING);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(MsgType.RESEND_CODE);
                    }
                }).start();
            }
        }
    }

    public boolean isExistUser(){

        return false;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }
    Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == MsgType.SMSS_WAITING) {
                btn_send.setText("重新发送(" + i + ")");
            } else if (msg.what == MsgType.RESEND_CODE) {
                btn_send.setText("获取验证码");
                btn_send.setClickable(true);
                i = 30;
            }else if(msg.what == MsgType.EVENT_SUBMIT_VERIFICATION_CODE){
                Toast.makeText(getApplicationContext(), "提交验证码成功",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }else if(msg.what == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                Toast.makeText(getApplicationContext(), "正在获取验证码",
                        Toast.LENGTH_SHORT).show();
            }else if(msg.what==MsgType.CODE_ERROR){
                Toast.makeText(getApplicationContext(), "发送验证码失败",
                        Toast.LENGTH_SHORT).show();
                if(msg.obj!=null){
                    ((Throwable)  msg.obj).printStackTrace();
                }
            }
        }
    };
    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {
            boolean Sign2 = et_usertel.getText().length() > 0;
//            boolean Sign3 = et_password.getText().length() > 4;
            boolean Sign3=true;
            if (Sign2 & Sign3) {
                btn_register.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.draw));
                btn_register.setEnabled(true);
            } else {
                btn_register.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.draw));
                btn_register.setTextColor(0xFFD0EFC6);
                btn_register.setEnabled(false);
            }
        }
    }
}
