//package com.example.asus.test3;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.telephony.SmsMessage;
//import android.text.Editable;
//import android.text.Html;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import cn.smssdk.EventHandler;
//import cn.smssdk.SMSSDK;
//
//
///**
// * Created by asus on 2017/10/13.
// */
//
//public class RegisterActivity extends Activity implements View.OnClickListener {
//    //private TextView txt_title;
//    //private ImageView img_back;
//    private Button btn_register, btn_send;
//    private EditText et_usertel, et_password, et_code;
//    /////////////protected FlippingLoadingDialog mLoadingDialog;
//    // 填写从短信SDK应用后台注册得到的APPKEY
//    private static String APPKEY = "1a1b7196fdf78";
//    // 填写从短信SDK应用后台注册得到的APPSECRET
//    private static String APPSECRET = "1702707aab4596e5db4ed561b63178bf";
//    int i = 60;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//        initControl();
//        setListener();
//
//    }
//
//    protected void initControl() {
//        //txt_title = (TextView) findViewById(R.id.txt_title);
//        //txt_title.setText("注册");
//        //img_back = (ImageView) findViewById(R.id.img_back);
//        //img_back.setVisibility(View.VISIBLE);
//        btn_send = (Button) findViewById(R.id.btn_send);
//        btn_register = (Button) findViewById(R.id.btn_register);
//        et_usertel = (EditText) findViewById(R.id.et_usertel);
//        //et_password = (EditText) findViewById(R.id.et_password);
//        et_code = (EditText) findViewById(R.id.et_code);
//        et_usertel.addTextChangedListener(new RegisterActivity.TextChange());
//        //et_password.addTextChangedListener(new RegisterActivity.TextChange());
//        btn_send.setEnabled(true);
//        btn_send.setClickable(true);
//        smsregis();
//    }
//
//
//    protected void setListener() {
//        //img_back.setOnClickListener(this);
//        btn_send.setOnClickListener(this);
//        btn_register.setOnClickListener(RegisterActivity.this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_send:
//                Toast.makeText(this, "---=====！",Toast.LENGTH_LONG).show();
//                getCode();
//                break;
//            case R.id.btn_register:
//                getRegister();
//                break;
//            default:
//                break;
//        }
//    }
//
//    //初始化SMSSDK
//    private void initSDK()
//    {
//        SMSSDK.initSDK(this, "App Key", "App Secret");
//        EventHandler eventHandler = new EventHandler() {
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
//                msg.what = SMSDDK_HANDLER;
//                handler.sendMessage(msg);
//            }
//        };
//        // 注册回调监听接口
//        SMSSDK.registerEventHandler(eventHandler);
//    }
//
//    public void smsregis(){
//        SMSSDK.initSDK(this,APPKEY,APPSECRET);
//        EventHandler eventHandler = new EventHandler(){
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
//                handler.sendMessage(msg);
//            }
//        };
//        //注册回调监听接口
//        SMSSDK.registerEventHandler(eventHandler);
//    }
//
//
//    Handler handler=new Handler(){
//        public void handleMessage(Message msg) {
//            if (msg.what == -9) {
//                btn_send.setText("重新发送(" + i + ")");
//            } else if (msg.what == -8) {
//                btn_send.setText("获取验证码");
//                btn_send.setClickable(true);
//                i = 30;
//            } else {
//                int event = msg.arg1;
//                int result = msg.arg2;
//                Object data = msg.obj;
//                Log.e("event", "event=" + event);
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // 短信注册成功后，返回MainActivity,然后提示
//                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
//                        Toast.makeText(getApplicationContext(), "提交验证码成功",
//                                Toast.LENGTH_SHORT).show();
//                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                        Toast.makeText(getApplicationContext(), "正在获取验证码",
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        ((Throwable) data).printStackTrace();
//                    }
//                }
//            }
//        }
//    };
//
//    public void getCode() {
//        // 1. 通过规则判断手机号
//        if (judgePhoneNums(et_usertel.getText().toString())) {
//            // 2. 通过sdk发送短信验证
//            SMSSDK.getVerificationCode("86", et_usertel.getText().toString());
//
//            // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
//            btn_send.setClickable(false);
//            btn_send.setText("重新发送(" + i + ")");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (; i > 0; i--) {
//                        handler.sendEmptyMessage(-9);
//                        if (i <= 0) {
//                            break;
//                        }
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    handler.sendEmptyMessage(-8);
//                }
//            }).start();
//        }
//    }
//
//    public void getRegister(){
//        //将收到的验证码和手机号提交再次核对
//        SMSSDK.submitVerificationCode("86", et_usertel.getText().toString(), et_code
//                .getText().toString());
//        createProgressBar();
//    }
//
//
//
////    public FlippingLoadingDialog getLoadingDialog(String msg) {
////        if (mLoadingDialog == null)
////            mLoadingDialog = new FlippingLoadingDialog(this, msg);
////        return mLoadingDialog;
////    }
//    /**
//     * 判断手机号码是否合理
//     *
//     * @param phoneNums
//     */
//    private boolean judgePhoneNums(String phoneNums) {
//        if (isMatchLength(phoneNums, 11)
//                && isMobileNO(phoneNums)) {
//            return true;
//        }
//        Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
//        return false;
//    }
//
//    /**
//     * 判断一个字符串的位数
//     * @param str
//     * @param length
//     * @return
//     */
//    public static boolean isMatchLength(String str, int length) {
//        if (str.isEmpty()) {
//            return false;
//        } else {
//            return str.length() == length ? true : false;
//        }
//    }
//
//    /**
//     * 验证手机格式
//     */
//    public static boolean isMobileNO(String mobileNums) {
//        /*
//         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
//         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
//         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
//         */
//        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if (TextUtils.isEmpty(mobileNums))
//            return false;
//        else
//            return mobileNums.matches(telRegex);
//    }
//
//    /**
//     * progressbar
//     */
//    private void createProgressBar() {
//        FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.CENTER;
//        ProgressBar mProBar = new ProgressBar(this);
//        mProBar.setLayoutParams(layoutParams);
//        mProBar.setVisibility(View.VISIBLE);
//        layout.addView(mProBar);
//    }
//
//    @Override
//    protected void onDestroy() {
//        SMSSDK.unregisterAllEventHandler();
//        super.onDestroy();
//    }
//
//    class TextChange implements TextWatcher {
//
//        @Override
//        public void afterTextChanged(Editable arg0) {
//
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//                                      int arg3) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence cs, int start, int before,
//                                  int count) {
//            boolean Sign2 = et_usertel.getText().length() > 0;
//            boolean Sign3 = et_password.getText().length() > 4;
//            if (Sign2 & Sign3) {
//                btn_register.setBackgroundDrawable(getResources().getDrawable(
//                        R.drawable.btn_en));
//                btn_register.setEnabled(true);
//            } else {
//                btn_register.setBackgroundDrawable(getResources().getDrawable(
//                        R.drawable.btn_en));
//                btn_register.setTextColor(0xFFD0EFC6);
//                btn_register.setEnabled(false);
//            }
//        }
//    }
//}
