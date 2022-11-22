package com.team.jixiao;

import static android.app.Activity.RESULT_OK;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.team.jixiao.utils.CameraUtils;
import com.team.jixiao.utils.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFragment2 extends Fragment implements View.OnClickListener {
    TextView tv_time;
    ImageView imgbtn_history;
    ImageButton imageBtn_sign;
    Intent intent;
    String imagePath = "";


    private Gson gson = new Gson();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new TimeThread().start();//启动线程
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout2, container, false);
        tv_time = view.findViewById(R.id.tv_time);
        imgbtn_history = view.findViewById(R.id.imgbtn_history);
        imageBtn_sign = view.findViewById(R.id.imageBtn_sign);

        imgbtn_history.setOnClickListener(this);
        imageBtn_sign.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
//    public void uploadImage(String imagePath){
//        OkHttpClient okHttpClient = new OkHttpClient();
//        file = new File(imagePath);
//        RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", imagePath, image)
//                .build();
//        Request request = new Request.Builder()
//                .url("/Loader/imagesUp.ashx")
//                .post(requestBody)
//                .build();
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response){
//                String res = response.toString().trim();
//                Log.e("uploadImage——image", res);
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgbtn_history:
                intent = new Intent(getActivity(),HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.imageBtn_sign:
                intent = new Intent(getActivity(),PhotoActivity.class);
                startActivity(intent);
                break;
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

}