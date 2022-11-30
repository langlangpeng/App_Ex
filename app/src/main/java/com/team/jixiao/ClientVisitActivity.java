package com.team.jixiao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientVisitActivity extends AppCompatActivity {
    private int role = -1;
    private int staff_info_id = 0;

    private Handler mainHandler;
    Intent intent;

    Gson gson = new Gson();

    @BindView(R.id.tv_time)
    TextView tv_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_visit);
        ButterKnife.bind(this);
        Date date = new Date(System.currentTimeMillis());
        tv_time.setText(getTime(date));

        role = getIntent().getIntExtra("role", -1);
        staff_info_id = getIntent().getIntExtra("staff_info_id", 0);
        Log.e("ClientVisitActivity_role:", String.valueOf(role));
        Log.e("ClientVisitActivity_info_id:", String.valueOf(staff_info_id));
    }
    @OnClick(R.id.tv_time)
    public void ViewClick(View view){
        switch (view.getId()){
            case R.id.tv_time:
                TimePickerView pickerView = new TimePickerBuilder(ClientVisitActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {

                    }
                }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        tv_time.setText(getTime(date));
                    }
                }).setType(new boolean[]{true, true, true,false,false,false})
                        .setItemVisibleCount(6)
                        .setLineSpacingMultiplier(2.0f)
                        .isAlphaGradient(true)
                        .build();
                pickerView.show();
                break;
        }
    }
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}