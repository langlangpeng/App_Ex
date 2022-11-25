package com.team.jixiao.Fragment;

import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.team.jixiao.Entity.Detail_Merchant;
import com.team.jixiao.Entity.Line;
import com.team.jixiao.HistoryActivity;
import com.team.jixiao.MainActivity;
import com.team.jixiao.PhotoActivity;
import com.team.jixiao.R;
import com.team.jixiao.utils.CameraUtils;
import com.team.jixiao.utils.CommonUtils;
import com.team.jixiao.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFragment2 extends Fragment implements View.OnClickListener, AMapLocationListener {
    TextView tv_time,tv_data;
    ImageView imgbtn_history;
    ImageButton imageBtn_sign;
    Intent intent;
    String imagePath = "";
    int role = -1;
    int staff_info_id = 0;
    double Latitude = 0;//获取纬度
    double Longitude = 0;//获取经度
    String Address = "无";//地址

    private Gson gson = new Gson();
    AMapLocationClient locationClient = null;
    String nickname = "";
    private Handler mainHandler;

    TextView tv_name;

    TextView tv_position;

    TextView tv_status,tv_status2;

    TextView tv_distancen;

    TextView tv_on,tv_off;

    Button btn_status;
    private int sign = 0;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        role = ((MainActivity) context).getRole();
        staff_info_id = ((MainActivity) context).getStaff_info_id();
        Latitude = ((MainActivity) context).getLatitude();
        Longitude = ((MainActivity) context).getLongitude();
        Address = ((MainActivity) context).getAddress();
        Log.e("MyFragment2_role", String.valueOf(role));
        Log.e("MyFragment2_staff_info_id", String.valueOf(staff_info_id));

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new TimeThread().start();//启动线程
        mainHandler = new Handler(getMainLooper());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout2, container, false);
        ButterKnife.bind(getActivity());
        tv_time = view.findViewById(R.id.tv_time);
        imgbtn_history = view.findViewById(R.id.imgbtn_history);
        imageBtn_sign = view.findViewById(R.id.imageBtn_sign);
        tv_name = view.findViewById(R.id.tv_name);
        tv_position = view.findViewById(R.id.tv_position);
        tv_status = view.findViewById(R.id.tv_status);
        tv_status2 = view.findViewById(R.id.tv_status2);
        tv_distancen = view.findViewById(R.id.tv_distancen);
        tv_data = view.findViewById(R.id.tv_data);
        tv_on = view.findViewById(R.id.tv_on);
        tv_off = view.findViewById(R.id.tv_off);
        Log.e("getRequest!", "----------------------------------------------" );
        mainHandler = new MainHandler();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date(System.currentTimeMillis());
        tv_data.setText("今天是　"+format.format(date));
        imgbtn_history.setOnClickListener(this);
        imageBtn_sign.setOnClickListener(this);

        try {
            locationClient = new AMapLocationClient(getActivity());
            AMapLocationClientOption option = new AMapLocationClientOption();
            /**
             * 设置签到场景，相当于设置为：
             * option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
             * option.setOnceLocation(true);
             * option.setOnceLocationLatest(true);
             * option.setMockEnable(false);
             * option.setWifiScan(true);
             * option.setGpsFirst(false);
             * 其他属性均为模式属性。
             * 如果要改变其中的属性，请在在设置定位场景之后进行
             */
            option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
            locationClient.setLocationOption(option);
            //设置定位监听
            locationClient.setLocationListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationClient.startLocation();
        getRequest();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgbtn_history:
                intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.imageBtn_sign:
                intent = new Intent(getActivity(), PhotoActivity.class);
                intent.putExtra("Latitude",Latitude);
                intent.putExtra("Longitude",Longitude);
                intent.putExtra("role",role);
                intent.putExtra("staff_info_id",staff_info_id);
                intent.putExtra("Address",Address);
                intent.putExtra("sign",sign);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            Latitude = aMapLocation.getLatitude();
            Longitude = aMapLocation.getLongitude();
            Address = aMapLocation.getAddress();

            Log.e("Latitude", String.valueOf(Latitude));
            Log.e("Longitude", String.valueOf(Longitude));
            Log.e("Address", Address);

        } else {
            Log.e("AMap", "签到定位失败，错误码：" + aMapLocation.getErrorCode() + ", " + aMapLocation.getLocationDetail());

        }
    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    tv_time.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    break;
            }
            return false;
        }
    });
    private void getRequest() {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(staff_info_id))
                .add("Latitude", String.valueOf(Latitude))
                .add("Longitude", String.valueOf(Longitude))
                .build();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + Constant.MY_LOCATION)
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
                Log.d("MyF2_msg", "onResponse: "+res);
                msg.what = 1;
                msg.obj = res;
                mainHandler.sendMessage(msg);
            }
        });
    }
    class MainHandler extends Handler{
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 1:
                    if (msg.obj != null) {
                        String vlResult = (String) msg.obj;
                        Log.e("INFO_String", vlResult);
                        try {
                            JSONObject jsonObject = new JSONObject(vlResult);

                            Log.e("json_jsonObject:", String.valueOf(jsonObject));
                            String data = jsonObject.optString("data");
                            Log.e("json_data:", data);
                            //info
                            JSONObject jsonObject3 = new JSONObject(data);
                            String data1 = jsonObject3.optString("info");
                            Log.e("json_data1", data1);
                            JSONObject jsonObject4 = new JSONObject(data1);
                            String data2 = jsonObject4.optString("data");
                            Log.e("json_data2", data2);
                            JSONArray array1 = new JSONArray(data2);
                            List<Line> list = new ArrayList<>();
                            list = gson.fromJson(String.valueOf(array1), new TypeToken<List<Line>>(){}.getType());
                            for (Line line:list)
                            {
                                Log.e("Sign", String.valueOf(line.getSign()));
                                sign = line.getSign();
                                if (sign==1){
                                    tv_on.setText(line.getAdd_time().substring(10,19));
                                }
                                tv_off.setText(line.getAdd_time().substring(10,19));
                            }
                            Log.e("sign状态", String.valueOf(sign));

                            JSONObject jsonObject1 = new JSONObject(data);
                            String distancen = jsonObject1.getString("distancen");
                            tv_distancen.setText(distancen);
                            Boolean is_coordinate = jsonObject1.getBoolean("is_coordinate");
                            if (is_coordinate==true){
                                tv_status.setText("不在上班区域！");
                                tv_status2.setText("外勤");
                                imageBtn_sign.setBackgroundResource(R.drawable.bg_special_disease_circle2);
                            }else{
                                tv_status.setText("请认真上班哦！");
                                tv_status2.setText("内勤");
                                imageBtn_sign.setBackgroundResource(R.drawable.bg_special_disease_circle);
                            }
                            String info = jsonObject1.getString("info");
                            Log.e("info:", info);
                            JSONObject jsonObject2 = new JSONObject(info);
                            nickname = jsonObject2.getString("nickname");
                            String role = jsonObject2.getString("role");
                            tv_position.setText(role);
                            tv_name.setText(nickname);
                            Log.e("nickname:", nickname);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
            }
        }
    }
}