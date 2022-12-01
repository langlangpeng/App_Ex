package com.team.jixiao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillSetTypeAddActivity extends AppCompatActivity {
    @BindView(R.id.l1)
    LinearLayout l1;
    @BindView(R.id.l2)
    LinearLayout l2;
    @BindView(R.id.l3)
    LinearLayout l3;
    @BindView(R.id.l4)
    LinearLayout l4;
    @BindView(R.id.l5)
    LinearLayout l5;

    @BindView(R.id.type_name1)
    EditText type_name1;
    @BindView(R.id.type_name2)
    EditText type_name2;
    @BindView(R.id.type_name3)
    EditText type_name3;
    @BindView(R.id.type_name4)
    EditText type_name4;
    @BindView(R.id.type_name5)
    EditText type_name5;

    @BindView(R.id.type_add1)
    ImageButton type_add1;
    @BindView(R.id.type_add2)
    ImageButton type_add2;
    @BindView(R.id.type_add3)
    ImageButton type_add3;
    @BindView(R.id.type_add4)
    ImageButton type_add4;


    @BindView(R.id.type_remove2)
    ImageButton type_remove2;
    @BindView(R.id.type_remove3)
    ImageButton type_remove3;
    @BindView(R.id.type_remove4)
    ImageButton type_remove4;
    @BindView(R.id.type_remove5)
    ImageButton type_remove5;

    @BindView(R.id.btn_type_submit)
    Button btn_type_submit;
    String type_content = "";
    String typename = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_set_type_add);
        ButterKnife.bind(this);
        typename = getIntent().getStringExtra("typename");
        if (typename.isEmpty()){
            typename = "";
        }
        type_name1.setText(typename);
    }
    @OnClick({R.id.type_add1,R.id.type_add2,R.id.type_add3,R.id.type_add4,R.id.type_remove2,R.id.type_remove3,R.id.type_remove4,R.id.type_remove5,R.id.btn_type_submit})
    public void OnClickView(View view){
        switch (view.getId()){
            case R.id.type_add1:
                l2.setVisibility(View.VISIBLE);
                break;
            case R.id.type_add2:
                l3.setVisibility(View.VISIBLE);
                break;
            case R.id.type_add3:
                l4.setVisibility(View.VISIBLE);
                break;
            case R.id.type_add4:
                l5.setVisibility(View.VISIBLE);
                break;
            case R.id.type_remove2:
                type_name2.setText("");
                l2.setVisibility(View.INVISIBLE);
                break;
            case R.id.type_remove3:
                type_name3.setText("");
                l3.setVisibility(View.INVISIBLE);
                break;
            case R.id.type_remove4:
                type_name4.setText("");
                l4.setVisibility(View.INVISIBLE);
                break;
            case R.id.type_remove5:
                type_name5.setText("");
                l5.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_type_submit:
                String content = type_name1.getText().toString().trim();
                if (!type_name2.getText().toString().trim().isEmpty()){
                    content = content + "|" + type_name2.getText().toString().trim();
                }
                if (!type_name3.getText().toString().trim().isEmpty()){
                    content = content + "|" + type_name3.getText().toString().trim();
                }
                if (!type_name4.getText().toString().trim().isEmpty()){
                    content = content + "|" + type_name4.getText().toString().trim();
                }
                if (!type_name5.getText().toString().trim().isEmpty()){
                    content = content + "|" + type_name5.getText().toString().trim();
                }
                Intent intent = new Intent(BillSetTypeAddActivity.this,BillSetActivity.class);
                intent.putExtra("content",content);
                startActivity(intent);
                Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}