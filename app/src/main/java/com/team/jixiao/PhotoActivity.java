package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.team.jixiao.Entity.Detail_Merchant;
import com.team.jixiao.utils.BitmapUtils;
import com.team.jixiao.utils.CameraUtils;
import com.team.jixiao.utils.CommonUtils;

import com.team.jixiao.utils.Constant;
import com.team.jixiao.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PhotoActivity extends AppCompatActivity {
    private RxPermissions rxPermissions;

    //是否拥有权限
    private boolean hasPermissions = false;

    //启动相机标识
    public static final int TAKE_PHOTO = 1;

    private String imagePath;

    private File outputImagePath;

    Intent intent;
    private Handler mainHandler;
    File file;
    String imageUrl = null;
    //Base64
    private String base64Pic;
    //拍照和相册获取图片的Bitmap
    private Bitmap orc_bitmap;

    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions.circleCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存

    private ImageView pic;

    private double Latitude = 0;
    private double Longitude = 0;
    private int staff_info_id = 0;
    private int role = -1;
    private int sign = 0;
    String Address = "无";//地址
    String res = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Latitude = getIntent().getDoubleExtra("Latitude",0);
        Longitude = getIntent().getDoubleExtra("Longitude",0);

        staff_info_id = getIntent().getIntExtra("staff_info_id",0);
        role = getIntent().getIntExtra("role",-1);
        Address = getIntent().getStringExtra("Address");
        sign = getIntent().getIntExtra("sign",0);
        Log.e("PhotoActivity_Latitude", String.valueOf(Latitude));
        Log.e("PhotoActivity_Longitude", String.valueOf(Longitude));
        Log.e("staff_info_id", String.valueOf(staff_info_id));
        Log.e("role", String.valueOf(role));
        Log.e("Address", Address );
        Log.e("sign", String.valueOf(sign));
        if (sign==0){
            sign=1;
        }else if (sign==1){
            sign=2;
        }
        mainHandler = new Handler(getMainLooper());
        pic = findViewById(R.id.pic);
        //检查版本
        checkVersion();
        //取出缓存
        imageUrl = SPUtils.getString("imageUrl",null,this);
        takePhoto();


//        mainHandler = new MainHandler();
    }
    private void checkVersion() {
        //Android6.0及以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果你是在Fragment中，则把this换成getActivity()
            rxPermissions = new RxPermissions(this);
            //权限请求
            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {//申请成功
                            CommonUtils.showShortMsg(PhotoActivity.this,"已获取权限");
                            hasPermissions = true;
                        } else {//申请失败
                            CommonUtils.showShortMsg(PhotoActivity.this,"权限未开启");
                            hasPermissions = false;
                        }
                    });
        } else {
            //Android6.0以下
            CommonUtils.showShortMsg(PhotoActivity.this,"无需请求动态权限");
        }
    }


    public void uploadImage(String imagePath){
        Log.d("uploadImage", imagePath);
        OkHttpClient okHttpClient = new OkHttpClient();
        file = new File(imagePath);
        RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imagePath, image)
                .build();
        Request request = new Request.Builder()
                .url(Constant.WEB_SITE+Constant.SIGN_PHOTO)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("uploadImage——No", String.valueOf(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                res = response.body().string();
                Log.e("res",res );
                upsign();
            }
        });
    }
    /**
     * 通过图片路径显示图片
     */
    private void displayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            Log.e("图片缓存", imagePath);
            //放入缓存
            SPUtils.putString("imageUrl",imagePath,this);

            //显示图片
            Glide.with(this).load(imagePath).apply(requestOptions).into(pic);

            //1
            Log.e("压缩imagePath", imagePath);
            //压缩图片
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null){
                orc_bitmap = CameraUtils.compression(bitmap);
            }else {
                orc_bitmap = null;
            }
            //转Base64
            base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);

        } else {
            CommonUtils.showShortMsg(PhotoActivity.this,"图片获取失败");
        }

    }
    private void takePhoto() {
        if (!hasPermissions) {
            CommonUtils.showShortMsg(PhotoActivity.this,"未获取到权限");
            checkVersion();
            return;
        }
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        Log.e("filename", filename);
        outputImagePath = new File(getExternalCacheDir(),
                filename + ".jpg");
        Log.e("拍照获取", String.valueOf(outputImagePath));
        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(this, outputImagePath);
        // 开启一个带有返回值的Activity，请求码为TAKE_PHOTO
        startActivityForResult(takePhotoIntent, TAKE_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //拍照后返回
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    imagePath = null;
                    //显示图片 1
                    imagePath = outputImagePath.getAbsolutePath();
                    displayImage(outputImagePath.getAbsolutePath());
                    Log.e("显示路径", imagePath);
                }
                break;

        }
//        if (imagePath.isEmpty()){
//            Toast.makeText(this, "图片上传失败，请检查手机权限是否开启", Toast.LENGTH_SHORT).show();
//        }
        uploadImage(imagePath);
    }

    private void upsign() {
        OkHttpClient client = new OkHttpClient();

        String s_Latitude = String.valueOf(Latitude);
        String s_Longitude = String.valueOf(Longitude);
        Log.e("s_Latitude", s_Latitude);

        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(staff_info_id))
                .add("Latitude", s_Latitude)
                .add("Longitude", s_Longitude)
                .add("face_photo",res)
                .add("Address",Address)
                .add("sign", String.valueOf(sign))
                .build();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE+Constant.SIGN_INFO)
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
                Log.d("PhotoActivity_msg", "onResponse: "+res);
                intent = new Intent(PhotoActivity.this,MainActivity.class);
                intent.putExtra("role",role);
                intent.putExtra("staff_info_id",staff_info_id);
                startActivity(intent);
                finish();
            }
        });
    }
}