package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryActivity extends AppCompatActivity {
    @BindView(R.id.calendarView)
    CalendarView calendarView;

    String strTime = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        strTime = format.format(calendarView.getDate());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                strTime = year+"-"+(month+1)+"-"+dayOfMonth;
                Log.e("TAG", "onSelectedDayChange: "+strTime);
            }
        });
    }
}