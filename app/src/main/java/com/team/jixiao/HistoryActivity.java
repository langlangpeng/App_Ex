package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.jixiao.Adapter.LvDataSignAdapter;
import com.team.jixiao.Adapter.LvStuffInfoAdapter;
import com.team.jixiao.Entity.DataSign;
import com.team.jixiao.Entity.StuffInfo;
import com.team.jixiao.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {
    @BindView(R.id.calendarView)
    CalendarView calendarView;

    @BindView(R.id.lv_history)
    ListView lv_history;

    String strTime = "";

    Gson gson = new Gson();

    private Handler mainHandler;

    List<DataSign> list = new ArrayList<>();

    private LvDataSignAdapter lvDataSignAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        strTime = format.format(calendarView.getDate());
        mainHandler = new Handler(getMainLooper());
        lvDataSignAdapter = new LvDataSignAdapter(this,list);
        lv_history.setAdapter(lvDataSignAdapter);

        mainHandler = new MainHandler();

        getRequest();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                strTime = year+"-"+(month+1)+"-"+dayOfMonth;
                getRequest();
                Log.e("TAG", "onSelectedDayChange: "+strTime);
            }
        });
    }

    private void getRequest() {
        OkHttpClient client = new OkHttpClient();

        FormBody body = new FormBody.Builder()
                .add("times", strTime)
                .build();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + "/webapi/attendance/sel_all.ashx")
                .post(body)
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
                Log.d("msg", "onResponse: "+res);
                msg.what = 1;
                msg.obj = res;
                mainHandler.sendMessage(msg);
            }
        });
    }
    /*
    {
    "code": 1,
    "msg": "操作成功",
    "data": [
        {
            "nickname": "阿浪",
            "clock_Recording": [
                {
                    "sign": 2,
                    "add_time": "2022-11-22 14:54:44",
                    "type": "正常"
                }
            ]
        },
        {
            "nickname": "浪浪鹏",
            "clock_Recording": [
                {
                    "sign": 1,
                    "add_time": "2022-11-22 15:06:42",
                    "type": "正常"
                },
                {
                    "sign": 2,
                    "add_time": "2022-11-22 15:06:42",
                    "type": "正常"
                }
            ]
        }
    ]
}
     */
    class MainHandler extends Handler{
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 1:
                    if (msg.obj != null) {
                        String json = (String) msg.obj;
                        Log.e("json:", json);

                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            Log.e("json_jsonObject:", String.valueOf(jsonObject));
                            Log.e("json_jsonObject:", "--------------------------------------------------");
                            JSONArray data = jsonObject.optJSONArray("data");
                            Log.e("json_data:", String.valueOf(data));

                            list = gson.fromJson(String.valueOf(data), new TypeToken<List<DataSign>>(){}.getType());

                            for (DataSign d : list){
                                Log.e("TAG", d.getNickname() );
                            }

                            for (int i = 0; i < data.length(); i++) {
                                String info = data.optString(i);
                                JSONObject jsonObject1 = new JSONObject(info);
                                Log.e("json_jsonObject1:", String.valueOf(jsonObject1));
                                String nickname = jsonObject1.optString("nickname");
                                Log.e("data1", "-------------------------------------------------------------");
                                Log.e("data1", nickname);
                                JSONArray jsonArray = jsonObject1.optJSONArray("clock_Recording");
                                Log.e("jsonArray:", String.valueOf(jsonArray));

                                for (int j = 0;j<jsonArray.length();j++){
                                    JSONObject jsonObject2 = jsonArray.optJSONObject(j);
                                    if (jsonObject2 != null){
                                        int sign = jsonObject2.optInt("sign");
                                        String add_time = jsonObject2.optString("add_time");
                                        String type = jsonObject2.optString("type");
                                        Log.e("TAG", String.valueOf(sign)+","+add_time+","+type);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        lvDataSignAdapter.setData(list);
                    }
                    break;
            }
        }
    }
}