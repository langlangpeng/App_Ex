package com.team.jixiao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillActivity extends AppCompatActivity {
    private int role = -1;
    private int staff_info_id = 0;
    @BindView(R.id.btn_billset)
    Button btn_billset;
    @BindView(R.id.btn_billbrowse)
    Button btn_billbrowse;
    @BindView(R.id.btn_valetbill)
    Button btn_valetbill;
    @BindView(R.id.btn_billmerchantset)
    Button btn_billmerchantset;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        ButterKnife.bind(this);
        role = getIntent().getIntExtra("role", -1);
        staff_info_id = getIntent().getIntExtra("staff_info_id", 0);
        Log.e("BillActivity_role:", String.valueOf(role));
        Log.e("BillActivity_info_id:", String.valueOf(staff_info_id));

    }
    @OnClick({R.id.btn_billset,R.id.btn_billbrowse,R.id.btn_valetbill,R.id.btn_billmerchantset})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.btn_billset:
                    intent = new Intent(BillActivity.this,BillSetActivity.class);
                    startActivity(intent);
                break;
            case R.id.btn_billbrowse:

                break;
            case R.id.btn_valetbill:

                break;
            case R.id.btn_billmerchantset:

                break;
            default:
                break;
        }
    }
}