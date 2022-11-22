package com.team.jixiao;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.team.jixiao.Entity.Response_User;
import com.team.jixiao.utils.BitmapUtils;
import com.team.jixiao.utils.CameraUtils;
import com.team.jixiao.utils.CommonUtils;
import com.team.jixiao.utils.Constant;
import com.team.jixiao.utils.SPUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private int role = -1;
    private int staff_info_id = 0;

    private Intent intent;

    EditText username, password, rpasswrod, nickname;
    Button submit;
    Spinner spinner_sex, spinner_job;
    private String sex = "";
    private String job = "";

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

    private String imagePath;

    private Handler mainHandler;

    private int P_id = 0;

    private Gson gson = new Gson();

    String imageUrl = null;

    int num_code = 0;
    String num_msg = "";
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        role = getIntent().getIntExtra("role",-1);
        staff_info_id = getIntent().getIntExtra("staff_info_id",0);
        Log.e("RegisterActivity_role:", String.valueOf(role));
        Log.e("RegisterActivity_staff_info_id:", String.valueOf(staff_info_id));
        init();

        spinner_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sex = RegisterActivity.this.getResources().getStringArray(R.array.SexArray)[position].trim();
//                Toast.makeText(RegisterActivity.this, sex, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_job.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                job = RegisterActivity.this.getResources().getStringArray(R.array.PositionArray)[position].trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String un = username.getText().toString().trim();
                final String pw = password.getText().toString().trim();
                final String rpw = rpasswrod.getText().toString().trim();
                final String nname = nickname.getText().toString().trim();
                if (TextUtils.isEmpty(nname)) {
                    CommonUtils.showShortMsg(RegisterActivity.this, "请输入昵称");
                    username.requestFocus();
                } else if (TextUtils.isEmpty(un)) {
                    CommonUtils.showShortMsg(RegisterActivity.this, "请输入用户名");
                    username.requestFocus();
                } else if (TextUtils.isEmpty(pw)) {
                    CommonUtils.showShortMsg(RegisterActivity.this, "请输入密码");
                    password.requestFocus();
                } else if (TextUtils.isEmpty(rpw)) {
                    Toast.makeText(RegisterActivity.this, "全部输入", Toast.LENGTH_SHORT).show();
                    CommonUtils.showShortMsg(RegisterActivity.this, "请输入重复密码为空");
                    rpasswrod.requestFocus();
                } else if (!pw.equals(rpw)) {
                    CommonUtils.showShortMsg(RegisterActivity.this, "两次密码不一致");
                    rpasswrod.requestFocus();
                } else if (sex.equals("请选择") && job.equals("请选择")) {
                    CommonUtils.showShortMsg(RegisterActivity.this, "下拉框的内容未选择");
                } else {
//                    showRequest(un,pw,sex,job,imagePath);
                    switch (job) {
                        case "门店人员":
                            P_id = 1;
                            break;
                        case "递推人员":
                            P_id = 2;
                        default:
                            break;
                    }
                    postRequest(un, pw, sex, job, nname, P_id);
                }
            }
        });
    }


    private void showRequest(String un, String pw, String sex, String job, String imagePath) {
        Log.e("用户名：", "消息展示-----------------------------");
        Log.e("用户名：", un);
        Log.e("密码：", pw);
        Log.e("性别：", sex);
        Log.e("岗位：", job);
//        Log.e("图片路径：", imagePath);
    }

    private void postRequest(String username, String password, String sex, String job, String nname, int P_id) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("sex", sex)
                .add("username", username)
                .add("password", password)
                .add("nickname", nname)
                .add("role", String.valueOf(P_id))
                .build();
        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + Constant.REGISTER)
                .post(body)
                .build();
        Log.e("postRequest", String.valueOf(request));
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();//转字符串
                Log.e("TAG",  res);
                Response_User response_User = gson.fromJson(res, Response_User.class);
                num_code = response_User.getCode();
                num_msg = response_User.getMsg();
                if (num_msg.equals("用户已存在")){
                    Log.d("TAG01", "用户已存在！");
                }else {
                    startActivity(new Intent(RegisterActivity.this, StuffInfoActivity.class));
                    intent.putExtra("role",role);
                    intent.putExtra("staff_info_id",staff_info_id);
                    finish();
                }
            }
        });
    }


    void init() {
        username = findViewById(R.id.ed_mobile);
        password = findViewById(R.id.ed_password);
        rpasswrod = findViewById(R.id.ed_rpassword);
        nickname = findViewById(R.id.ed_nickname);
        submit = findViewById(R.id.submit);

        spinner_job = findViewById(R.id.spinner_job);
        spinner_sex = findViewById(R.id.spinner_sex);

//        ivHead = findViewById(R.id.iv_head);

        mainHandler = new Handler(getMainLooper());


//        if(imageUrl != null){
//            Glide.with(this).load(imageUrl).apply(requestOptions).into(ivHead);
//        }
//        btn_photo = findViewById(R.id.btn_photo);
        if (imagePath == null){
            imagePath = imageUrl;
        }
//        btn_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Log.e("转换", GetImageStr(imageUrl));
////                    PostPhoto(GetImageStr(imageUrl));
//
//                Log.e("imagePath", "----------------------------"+imagePath);
//                uploadImage(imagePath);
//            }
//        });

    }
    /**
     * 检查版本
     */


//    public void changeAvatar(View view) {
//        bottomSheetDialog = new BottomSheetDialog(this);
//        bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
//        bottomSheetDialog.setContentView(bottomView);
////        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
//        bottomSheetDialog.show();
//        TextView tvTakePictures = bottomView.findViewById(R.id.tv_take_pictures);
//        TextView tvOpenAlbum = bottomView.findViewById(R.id.tv_open_album);
//        TextView tvCancel = bottomView.findViewById(R.id.tv_cancel);
//
//        //拍照
//        tvTakePictures.setOnClickListener(v -> {
//            takePhoto();
//            CommonUtils.showShortMsg(RegisterActivity.this,"拍照");
//            bottomSheetDialog.cancel();
//        });
//        //打开相册
//        tvOpenAlbum.setOnClickListener(v -> {
//            openAlbum();
//            CommonUtils.showShortMsg(RegisterActivity.this,"打开相册");
//            bottomSheetDialog.cancel();
//        });
//        //取消
//        tvCancel.setOnClickListener(v -> {
//            bottomSheetDialog.cancel();
//        });
//        //底部弹窗显示
//        bottomSheetDialog.show();
//    }

//    private void takePhoto() {
//        if (!hasPermissions) {
//            CommonUtils.showShortMsg(RegisterActivity.this,"未获取到权限");
//            checkVersion();
//            return;
//        }
//        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
//                "yyyy_MM_dd_HH_mm_ss");
//        String filename = timeStampFormat.format(new Date());
//        Log.e("filename", filename);
//        outputImagePath = new File(getExternalCacheDir(),
//                filename + ".jpg");
//        Log.e("拍照获取", String.valueOf(outputImagePath));
//        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(this, outputImagePath);
//        // 开启一个带有返回值的Activity，请求码为TAKE_PHOTO
//        startActivityForResult(takePhotoIntent, TAKE_PHOTO);
//    }
//    private void openAlbum() {
//        if (!hasPermissions) {
//            CommonUtils.showShortMsg(RegisterActivity.this,"未获取到权限");
//            checkVersion();
//            return;
//        }
//        startActivityForResult(CameraUtils.getSelectPhotoIntent(), SELECT_PHOTO);
//    }
    /**
     * 返回到Activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //拍照后返回
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    imagePath = null;
                    //显示图片 1
                    Log.e("拍照显示图片", outputImagePath.getAbsolutePath());
                    displayImage(outputImagePath.getAbsolutePath());
                    imagePath = outputImagePath.getAbsolutePath();
                }
                break;
            //打开相册后返回
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    imagePath = null;
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        //4.4及以上系统使用这个方法处理图片
                        imagePath = CameraUtils.getImageOnKitKatPath(data, this);
                        Log.e("打开相册后返回图片1", imagePath);
                    } else {
                        imagePath = CameraUtils.getImageBeforeKitKatPath(data, this);
                        Log.e("打开相册后返回图片2", imagePath);
                    }
                    //显示图片
                    displayImage(imagePath);
                    Log.e("打开相册后返回图片~~最终", imagePath);
                }
                break;
            default:
                break;
        }
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
//            Glide.with(this).load(imagePath).apply(requestOptions).into(ivHead);

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
            CommonUtils.showShortMsg(RegisterActivity.this,"图片获取失败");
        }
    }

    private void PostPhoto(String photo){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("filenums", photo)
                .build();
        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + "/Loader/imagesUp.ashx")
                .post(body)
                .build();
        Log.e("postRequest", String.valueOf(request));
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                String res = response.toString().trim();
                Log.e("PostPhoto——image", res);
            }
        });
    }

    public void uploadImage(String imagePath){
        OkHttpClient okHttpClient = new OkHttpClient();
        file = new File(imagePath);
        RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imagePath, image)
                .build();
        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + "/Loader/imagesUp.ashx")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                String res = response.toString().trim();
                Log.e("uploadImage——image", res);
            }
        });
    }
//误删！
//    public static String GetImageStr(String filePath) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
//        String imgFile = filePath;//待处理的图片
//        InputStream in = null;
//        byte[] Data = null;
//        //读取图片字节数组
//        try {
//            in = new FileInputStream(imgFile);
//            Data = new byte[in.available()];
//            in.read(Data);
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//        return encoder.encode(Data);//返回Base64编码过的字节数组字符串
//    }

}