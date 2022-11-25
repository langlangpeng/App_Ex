package com.team.jixiao;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.jixiao.Adapter.DetailMerChartAdapter;
import com.team.jixiao.Adapter.LvStuffInfoAdapter;
import com.team.jixiao.Entity.Detail_Merchant;
import com.team.jixiao.Entity.StuffInfo;
import com.team.jixiao.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class BusinessCheckActivity extends AppCompatActivity {
    private int role = -1;

    private int staff_info_id = 0;

    Gson gson = new Gson();

    JSONArray array;

    private Handler mainHandler;
    Intent intent;
    @BindView(R.id.tv_start_time)
    TextView tv_start_time;
    @BindView(R.id.tv_end_time)
    TextView tv_end_time;

    String start_time,end_time;

    List<Detail_Merchant> list = new ArrayList<>();

    private DetailMerChartAdapter detailMerChartAdapter;
    String time;
    ListView lv_check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_check);
        ButterKnife.bind(this);
        initView();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());

        start_time = getTime(date);
        end_time = getTime(date);

        tv_start_time.setText(start_time);
        tv_end_time.setText(end_time);

        tv_start_time.setOnClickListener(this::onViewClick);
        tv_end_time.setOnClickListener(this::onViewClick);

        role = getIntent().getIntExtra("role",-1);
        staff_info_id = getIntent().getIntExtra("staff_info_id",0);
        Log.e("BusinessCheckActivity_role:", String.valueOf(role));
        Log.e("BusinessCheckActivity_staff_info_id:", String.valueOf(staff_info_id));

        time = start_time + " 00:00 - "+ end_time +" 23:59";
        getRequest();
        mainHandler = new MainHandler();
    }

    private void getRequest() {
        OkHttpClient client = new OkHttpClient();

        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(staff_info_id))
                .add("date_time", "2020-11-23 00:00 - 2022-11-23 23:59")
                .build();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE+Constant.BUSINESS_CHECK)
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
                Log.d("BusinessCheckActivity_msg", "onResponse: "+res);
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
                        Log.e("INFO_String", vlResult);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(vlResult);
                            array = jsonObject.optJSONArray("data");
                            Log.e("array", String.valueOf(array));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list = gson.fromJson(String.valueOf(array), new TypeToken<List<Detail_Merchant>>(){}.getType());
//                        for (StuffInfo data : list){
//                            Log.e("TAG", data.getUsername() );
//                            Log.e("TAG", data.getNickname() );
//                            Log.e("TAG", data.getMobile() );
//                            Log.e("TAG", data.getSex() );
//                        }
                        detailMerChartAdapter.setData(list);
                    }
                    break;
            }
        }
    }

    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.tv_start_time:
                TimePickerView  pickerView = new TimePickerBuilder(BusinessCheckActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {

                    }
                }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                            @Override
                            public void onTimeSelectChanged(Date date) {
                                tv_start_time.setText(getTime(date));
                                time = getTime(date) + " 00:00 - " + end_time+" 23:59";
                                getRequest();
                            }
                        })
                        .setType(new boolean[]{true, true, true,false,false,false})
                        .setItemVisibleCount(6)
                        .setLineSpacingMultiplier(2.0f)
                        .isAlphaGradient(true)
                        .build();
                pickerView.show();

                break;
            case R.id.tv_end_time:
                TimePickerView pickerView2 = new TimePickerBuilder(BusinessCheckActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {

                    }
                }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                            @Override
                            public void onTimeSelectChanged(Date date) {
                                tv_end_time.setText(getTime(date));
                                time = start_time + " 00:00 - " + getTime(date)+" 23:59";
                                getRequest();
                            }
                        })
                        .setType(new boolean[]{true, true, true,false,false,false})
                        .setItemVisibleCount(6)
                        .setLineSpacingMultiplier(2.0f)
                        .isAlphaGradient(true)
                        .build();
                pickerView2.show();
                break;
        }
    }
    private void initView() {
        lv_check = findViewById(R.id.lv_check);
        mainHandler = new Handler(getMainLooper());

        detailMerChartAdapter = new DetailMerChartAdapter(this,list);
        lv_check.setAdapter(detailMerChartAdapter);
    }
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}