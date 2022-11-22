package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.team.jixiao.Entity.Response_User;
import com.team.jixiao.utils.CommonUtils;
import com.team.jixiao.utils.Constant;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditInfoActivity extends AppCompatActivity {
    @BindView(R.id.ed_nickname)
    EditText ed_nickname;
    @BindView(R.id.ed_mobile)
    EditText ed_mobile;
    @BindView(R.id.ed_rpassword)
    EditText ed_rpassword;
    @BindView(R.id.submit)
    Button submit;
    int id = -1;
    String nickname = "";
    String Mobile = "";
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        ButterKnife.bind(this);
        id = getIntent().getIntExtra("id",-1);
        nickname = getIntent().getStringExtra("nickname");
        Mobile = getIntent().getStringExtra("Mobile");

        ed_nickname.setText(nickname);
        ed_nickname.setKeyListener(null);
        ed_mobile.setText(Mobile);
        ed_mobile.setKeyListener(null);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String rpassword = ed_rpassword.getText().toString().trim();
                if (TextUtils.isEmpty(rpassword)) {
                    CommonUtils.showShortMsg(EditInfoActivity.this, "请输入密码！");
                    ed_rpassword.requestFocus();
                }else{
                    postRequest(id,rpassword);
                }
            }
        });
    }

    private void postRequest(int id, String rpassword) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("password", rpassword)
                .build();
        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + "/webapi/staff/up_pwdbyid.ashx")
                .post(body)
                .build();
        Log.e("postRequest", String.valueOf(request));
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();//转字符串
                Log.e("TAG",  res);
                startActivity(new Intent(EditInfoActivity.this, LoginActivity.class));
                intent.putExtra("role",0);
                intent.putExtra("staff_info_id",1);
                finish();
            }
        });
    }
}