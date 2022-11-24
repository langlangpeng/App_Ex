package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.gson.Gson;

import com.team.jixiao.Entity.Response_User;
import com.team.jixiao.utils.CommonUtils;
import com.team.jixiao.utils.Constant;
import java.io.IOException;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.ed_mobile)
    EditText ed_username;
    @BindView(R.id.ed_password)
    EditText ed_password;

    private static int code = 0;
    private static int role = -1;
    private static int staff_info_id = 0;
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    @OnClick(R.id.btn_login)
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.btn_login:
                doLogin();
                break;
        }
    }
    private void doLogin() {
        final String uname = ed_username.getText().toString().trim();
        final String pass = ed_password.getText().toString().trim();
        if (TextUtils.isEmpty(uname)){
            CommonUtils.showShortMsg(LoginActivity.this,"请输入用户名");
            ed_username.requestFocus();
        }else if (TextUtils.isEmpty(pass)){
            CommonUtils.showShortMsg(LoginActivity.this,"请输入密码");
            ed_password.requestFocus();
        }else {
//            editor = preferences.edit();
//            if(remember.isChecked()){
//                editor.putBoolean("remember_password",true);
//                editor.putString("Name", uname);
//                editor.putString("Password", pass);
//            }else{editor.clear();}
//            editor.apply();
//
//            SharedPreferences share = getSharedPreferences("Login", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = share.edit();
//            editor.putString("Name",uname);
//            editor.putString("Password",pass);
//            editor.putBoolean("LoginBool", true);
//            editor.apply();
            postRequest(uname,pass);
        }
    }

    private void postRequest(String username,String password) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + Constant.LOGIN_ON)
                .post(body)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                Log.i("onResponse", "-------------"+res+"-------------------");
                Response_User response_User = gson.fromJson(res, Response_User.class);
                code = response_User.getCode();
                role = response_User.getRole();
                staff_info_id = response_User.getStaff_info_id();
                if (code == 1){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("role",role);
                    intent.putExtra("staff_info_id",staff_info_id);
                    startActivity(intent);
                    finish();
                }else if (code == 0){
                    Log.d("TAG01", "密码有误！");
                }
            }
        });
    }
}