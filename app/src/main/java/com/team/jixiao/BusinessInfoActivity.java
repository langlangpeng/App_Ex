package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.jixiao.Adapter.DetailMerChartAdapter;
import com.team.jixiao.Adapter.DetailMerChartInfoAdapter;
import com.team.jixiao.Entity.Detail_Merchart_Data;
import com.team.jixiao.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BusinessInfoActivity extends AppCompatActivity {
    String Mobile,Merchant_Name,Address,ImgUrl,Add_time;
    TextView tv_Mobile,tv_Merchant_Name,tv_Address,tv_ImgUrl,tv_Add_time;
    ImageView iv_pic;
    RadioButton radioButton_yes,radioButton_no;
    RadioGroup radioGroup_check;
    int ID = -1;
    ListView lv_bi;
    List<Detail_Merchart_Data> list = new ArrayList<>();
    Gson gson = new Gson();
    DetailMerChartInfoAdapter detailMerChartInfoAdapter;
    private Handler mainHandler;
    JSONArray jsonArray;
    int status = -1;

    int role = -1;
    int staff_info_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_info);

        Mobile = getIntent().getStringExtra("Mobile");
        Merchant_Name = getIntent().getStringExtra("Merchant_Name");
        Address = getIntent().getStringExtra("Address");
        ImgUrl = getIntent().getStringExtra("ImgUrl");
        Add_time = getIntent().getStringExtra("Add_time");
        ID = getIntent().getIntExtra("ID",-1);
        status = getIntent().getIntExtra("status",-1);

        role = getIntent().getIntExtra("role",-1);
        staff_info_id = getIntent().getIntExtra("staff_info_id",0);
        Log.e("BusinessInfoActivity_role", String.valueOf(role));
        Log.e("BusinessInfoActivity_staff_info_id", String.valueOf(staff_info_id));

        Log.e("ID", String.valueOf(ID));
        initView();
        getRequest();
        mainHandler = new MainHandler();

        radioGroup_check.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });
    }

    private void initView() {
        lv_bi = findViewById(R.id.lv_bi);
        radioButton_yes = findViewById(R.id.radioButton_yes);
        radioButton_no = findViewById(R.id.radioButton_no);
        radioGroup_check = findViewById(R.id.radioGroup_check);

        tv_Mobile = findViewById(R.id.tv_Mobile);
        tv_Address = findViewById(R.id.tv_Address);
        tv_Merchant_Name = findViewById(R.id.tv_Merchant_Name);
//        tv_ImgUrl = findViewById(R.id.tv_ImgUrl);
        tv_Add_time = findViewById(R.id.tv_Add_time);

        iv_pic = findViewById(R.id.iv_pic);

        tv_Mobile.setText(Mobile);
        tv_Address.setText(Address);
        tv_Merchant_Name.setText(Merchant_Name);
//        tv_ImgUrl.setText(ImgUrl);
        tv_Add_time.setText(Add_time);

        mainHandler = new Handler(getMainLooper());
        detailMerChartInfoAdapter = new DetailMerChartInfoAdapter(list,this);
        lv_bi.setAdapter(detailMerChartInfoAdapter);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.msb_default_person)
                .error(R.drawable.msb_default_person)
                .fallback(R.drawable.msb_default_person)
                .override(200,200); //override指定加载图片大小
        Glide.with(this)
                .load(ImgUrl)
                .apply(requestOptions)
                .into(iv_pic);
        disableRadioGroup(radioGroup_check);
        if (status == 1){
            radioButton_yes.setChecked(true);
        }else if (status == 2){
            radioButton_no.setChecked(true);
        }
    }

    private void getRequest() {
        OkHttpClient client = new OkHttpClient();

        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(ID))
                .build();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + "/webapi/Merchant/detail_Merchant.ashx")
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
                Log.d("BusinessInfoActivity_msg", "onResponse: "+res);
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
                        Log.e("BINFO_String", vlResult);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(vlResult);
                            String x = jsonObject.optString("data");
                            Log.e("x", x);

                            JSONObject jsonObject1 = new JSONObject(x);
                            jsonArray = jsonObject1.optJSONArray("content");
                            Log.e("jsonArray", String.valueOf(jsonArray));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list = gson.fromJson(String.valueOf(jsonArray), new TypeToken<List<Detail_Merchart_Data>>(){}.getType());

                        detailMerChartInfoAdapter.setData(list);
                    }
                    break;
            }
        }
    }
    public static void disableRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }
}