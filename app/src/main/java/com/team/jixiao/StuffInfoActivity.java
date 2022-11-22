package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.jixiao.Adapter.LvStuffInfoAdapter;
import com.team.jixiao.Entity.StuffInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StuffInfoActivity extends AppCompatActivity {
    private int role = -1;
    private int staff_info_id = 0;

    Gson gson = new Gson();

    JSONArray array;
    List<StuffInfo> list = new ArrayList<>();
    private Handler mainHandler;
    Intent intent;

    private LvStuffInfoAdapter lvStuffInfoAdapter;
    @BindView(R.id.lv_stuff)
    ListView lv_stuff;
    @BindView(R.id.btn_stuff_add)
    ImageButton btn_stuff_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuff_info);
        ButterKnife.bind(this);
        init();
        role = getIntent().getIntExtra("role",-1);
        staff_info_id = getIntent().getIntExtra("staff_info_id",0);
        Log.e("StuffInfo_role:", String.valueOf(role));
        Log.e("StuffInfo_staff_info_id:", String.valueOf(staff_info_id));

        mainHandler = new MainHandler();
        getRequest();
    }
    public void init(){
        mainHandler = new Handler(getMainLooper());
        lvStuffInfoAdapter = new LvStuffInfoAdapter(this,list);
        lv_stuff.setAdapter(lvStuffInfoAdapter);

        btn_stuff_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(StuffInfoActivity.this,RegisterActivity.class);
                intent.putExtra("role",role);
                intent.putExtra("staff_info_id",staff_info_id);
                startActivity(intent);
            }
        });
    }

    private void getRequest() {
        OkHttpClient client = new OkHttpClient();

//        FormBody body = new FormBody.Builder()
//                .add("id", custom_id)
//                .build();

        Request request = new Request.Builder()
                .url("http://47.92.214.113:8092/webapi/staff/sel_staff.ashx")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("TAG", "onFailure: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();//转字符串
                Message msg = new Message();
                Log.d("MyF1_msg", "onResponse: "+res);
                msg.what = 1;
                msg.obj = res;
                mainHandler.sendMessage(msg);
            }
        });
    }
    class MainHandler extends Handler {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 1:
                    if (msg.obj != null) {
                        String vlResult = (String) msg.obj;
//                        JSONObject jsonObject = new JSONObject(json);
//                        JSONObject data = jsonObject.optJSONObject("data");
//                        int rs_code = jsonObject.optInt("rs_code");
//                        String rs_msg = jsonObject.optString("rs_msg");

                        Log.e("INFO_String", vlResult);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(vlResult);
                            array = jsonObject.optJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list = gson.fromJson(String.valueOf(array), new TypeToken<List<StuffInfo>>(){}.getType());
                        for (StuffInfo data : list){
                            Log.e("TAG", data.getUsername() );
                            Log.e("TAG", data.getNickname() );
                            Log.e("TAG", data.getMobile() );
                            Log.e("TAG", data.getSex() );
                        }
                        lvStuffInfoAdapter.setData(list);
                    }
                break;
            }
        }
    }
}