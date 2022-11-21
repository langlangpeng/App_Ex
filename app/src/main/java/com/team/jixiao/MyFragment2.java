package com.team.jixiao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFragment2 extends Fragment implements View.OnClickListener {
    TextView tv_time;
    ImageView imgbtn_history;
    ImageButton imageBtn_sign;
    Intent intent;

    //底部弹窗
    private BottomSheetDialog bottomSheetDialog;

    private View bottomView;
    private File outputImagePath;

    private File file;

    private Gson gson = new Gson();
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
        imgbtn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),HistoryActivity.class);
                startActivity(intent);
            }
        });
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
                intent = new Intent(getActivity(),HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.imageBtn_sign:

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