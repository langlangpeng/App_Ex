package com.team.jixiao;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillSetActivity extends AppCompatActivity {
    @BindView(R.id.et_item_no)
    EditText et_item_no;
    @BindView(R.id.et_item_name)
    EditText et_item_name;
    @BindView(R.id.type_name)
    EditText type_name;

    @BindView(R.id.type_no1)
    EditText type_no1;
    @BindView(R.id.type_no2)
    EditText type_no2;
    @BindView(R.id.type_no3)
    EditText type_no3;

    @BindView(R.id.type_amount_no1)
    EditText type_amount_no1;
    @BindView(R.id.type_amount_no2)
    EditText type_amount_no2;
    @BindView(R.id.type_amount_no3)
    EditText type_amount_no3;

    @BindView(R.id.type_number_no1)
    TextView type_number_no1;
    @BindView(R.id.type_number_no2)
    TextView type_number_no2;
    @BindView(R.id.type_number_no3)
    TextView type_number_no3;

    @BindView(R.id.btn_sure)
    Button btn_sure;
    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.iv_pic)
    ImageView iv_pic;

    //启动相机标识
    public static final int TAKE_PHOTO = 1;
    //启动相册标识
    public static final int SELECT_PHOTO = 2;
    //是否拥有权限
    private boolean hasPermissions = false;

    private RxPermissions rxPermissions;

    //底部弹窗
    private BottomSheetDialog bottomSheetDialog;
    //弹窗视图
    private View bottomView;
    private File outputImagePath;
    //图片控件
    private ShapeableImageView ivHead;
    //Base64
    private String base64Pic;
    //拍照和相册获取图片的Bitmap
    private Bitmap orc_bitmap;

    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions.circleCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_set);
        ButterKnife.bind(this);
    }
    @OnClick({R.id.btn_sure,R.id.btn_submit,R.id.iv_pic})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.btn_sure:
                if (type_no1.getText().toString().isEmpty()){
                    type_no1.setText("0");
                }
                if (type_no2.getText().toString().isEmpty()){
                    type_no2.setText("0");
                }
                if (type_no3.getText().toString().isEmpty()){
                    type_no3.setText("0");
                }
                int no1  = Integer.parseInt(type_no1.getText().toString().trim());
                int no2  = Integer.parseInt(type_no2.getText().toString().trim());
                int no3  = Integer.parseInt(type_no3.getText().toString().trim());
                Log.e("数字", String.valueOf(no1)+","+String.valueOf(no2)+","+String.valueOf(no3)+",");
                if (no1<=5){
                    type_number_no1.setText("0-5");
                }else if (no1>5&&no1<=10){
                    type_number_no1.setText("6-10");
                }else {
                    type_number_no1.setText(type_no1.getText().toString());
                }

                if (no2<=5){
                    type_number_no2.setText("0-5");
                }else if (no2>5&&no2<=10){
                    type_number_no2.setText("6-10");
                }else {
                    type_number_no2.setText(type_no2.getText().toString());
                }

                if (no3<=5){
                    type_number_no3.setText("0-5");
                }else if (no3>5&&no3<=10){
                    type_number_no3.setText("6-10");
                }else {
                    type_number_no3.setText(type_no3.getText().toString());
                }
                break;
            case R.id.btn_submit:

                break;
            case R.id.iv_pic:

                break;
            default:
                break;
        }
    }
}