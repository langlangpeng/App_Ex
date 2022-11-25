package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.team.jixiao.utils.BitmapUtils;
import com.team.jixiao.utils.CameraUtils;
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

public class WaitForCheckActivity extends AppCompatActivity implements View.OnClickListener {
    EditText ed_Mobile,ed_Merchant_Name,ed_Address,ed_content;
    TextView tv_Add_time;
    ImageView iv_pic;
    RadioGroup radioGroup_check;
    RadioButton radioButton_yes,radioButton_no;
    Button btn_submit;

    String imagePath = "";
    String res = "";
    String Mobile,Merchant_Name,Address,ImgUrl,Add_time,face_photo,content;
    int ID = -1;
    int status = 0;

    int role = -1;
    int staff_info_id = 0;

    File file;

    Intent intent;

    //权限请求
    private RxPermissions rxPermissions;
    //是否拥有权限
    private boolean hasPermissions = false;
    //底部弹窗
    private BottomSheetDialog bottomSheetDialog;
    //弹窗视图
    private View bottomView;
    //存储拍完照后的图片
    private File outputImagePath;
    //启动相机标识
    public static final int TAKE_PHOTO = 1;
    //启动相册标识
    public static final int SELECT_PHOTO = 2;
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
        setContentView(R.layout.activity_wait_for_check);

        role = getIntent().getIntExtra("role",-1);
        staff_info_id = getIntent().getIntExtra("staff_info_id",0);
        Log.e("WaitForCheckActivity_role:", String.valueOf(role));
        Log.e("WaitForCheckActivity_staff_info_id:", String.valueOf(staff_info_id));

        Mobile = getIntent().getStringExtra("Mobile");
        Merchant_Name = getIntent().getStringExtra("Merchant_Name");
        Address = getIntent().getStringExtra("Address");
        ImgUrl = getIntent().getStringExtra("ImgUrl");
        Add_time = getIntent().getStringExtra("Add_time");
        ID = getIntent().getIntExtra("ID",-1);
        face_photo = getIntent().getStringExtra("face_photo");
        Log.e("ID", String.valueOf(ID));
        initView();



        radioGroup_check.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton_yes){
                    status = 1;
                }else {
                    status = 2;
                }
            }
        });

    }

    private void initView() {
        ed_Address = findViewById(R.id.ed_Address);
        ed_Mobile = findViewById(R.id.ed_Mobile);
        ed_Merchant_Name = findViewById(R.id.ed_Merchant_Name);
        ed_content = findViewById(R.id.ed_content);

        tv_Add_time = findViewById(R.id.tv_Add_time);

        iv_pic = findViewById(R.id.iv_pic);

        radioGroup_check = findViewById(R.id.radioGroup_check);
        radioButton_yes = findViewById(R.id.radioButton_yes);
        radioButton_no = findViewById(R.id.radioButton_no);

        btn_submit = findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(this);

        ed_Address.setText(Address);
        ed_Mobile.setText(Mobile);
        ed_Merchant_Name.setText(Merchant_Name);

        tv_Add_time.setText(Add_time);


        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.msb_default_person)
                .error(R.drawable.msb_default_person)
                .fallback(R.drawable.msb_default_person)
                .override(200,200); //override指定加载图片大小
        Glide.with(this)
                .load(ImgUrl)
                .apply(requestOptions)
                .into(iv_pic);
        checkVersion();
    }



    @Override
    public void onClick(View v) {
        content = ed_content.getText().toString().trim();
        Mobile = ed_Mobile.getText().toString().trim();
        Address = ed_Address.getText().toString().trim();
        Merchant_Name = ed_Merchant_Name.getText().toString().trim();
        Log.e("content", "content获取:文本:"+content);
        switch (v.getId()){
            case R.id.btn_submit:
                if (content.isEmpty()){
                    Toast.makeText(this, "说明信息不能为空！", Toast.LENGTH_SHORT).show();
                    ed_content.requestFocus();
                }else {
                    Log.e("btn_submit", imagePath);
                    if (imagePath.isEmpty()){
                        Log.e("没有图片提交", imagePath);
                        upsign();
                    }else {
                        Log.e("拍照或图库图片提交", imagePath);
                        uploadImage(imagePath);
                    }
                }
                break;
            default:
                break;
        }
    }
    /**
     * 检查版本
     */
    private void checkVersion() {
        //Android6.0及以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果你是在Fragment中，则把this换成getActivity()
            rxPermissions = new RxPermissions(this);
            //权限请求
            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {//申请成功
                            showMsg("已获取权限");
                            hasPermissions = true;
                        } else {//申请失败
                            showMsg("权限未开启");
                            hasPermissions = false;
                        }
                    });
        } else {
            //Android6.0以下
            showMsg("无需请求动态权限");
        }
    }

    /**
     * 更换头像
     *
     * @param view
     */
    public void changeAvatar(View view) {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
        bottomSheetDialog.setContentView(bottomView);

        bottomSheetDialog.show();

        TextView tvTakePictures = bottomView.findViewById(R.id.tv_take_pictures);
        TextView tvOpenAlbum = bottomView.findViewById(R.id.tv_open_album);
        TextView tvCancel = bottomView.findViewById(R.id.tv_cancel);

        //拍照
        tvTakePictures.setOnClickListener(v -> {
            takePhoto();
            showMsg("拍照");
            bottomSheetDialog.cancel();
        });
        //打开相册
        tvOpenAlbum.setOnClickListener(v -> {
            openAlbum();
            showMsg("打开相册");
            bottomSheetDialog.cancel();
        });
        //取消
        tvCancel.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
        });
        //底部弹窗显示
        bottomSheetDialog.show();
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (!hasPermissions) {
            showMsg("未获取到权限");
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

    /**
     * 打开相册
     */
    private void openAlbum() {
        if (!hasPermissions) {
            showMsg("未获取到权限");
            checkVersion();
            return;
        }
        startActivityForResult(CameraUtils.getSelectPhotoIntent(), SELECT_PHOTO);
    }

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
                    //显示图片
                    displayImage(outputImagePath.getAbsolutePath());
                    imagePath = outputImagePath.getAbsolutePath();
                    Log.e("显示路径", imagePath);
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
                        Log.e("4.4以上版本获取", imagePath );
                    } else {
                        imagePath = CameraUtils.getImageBeforeKitKatPath(data, this);
                        Log.e("4.4以下版本获取", imagePath );
                    }
                    //显示图片
                    displayImage(imagePath);
                    Log.e("显示路径", imagePath);
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
            Glide.with(this).load(imagePath).apply(requestOptions).into(iv_pic);

            //压缩图片
            orc_bitmap = CameraUtils.compression(BitmapFactory.decodeFile(imagePath));
            //转Base64
            base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);

        } else {
            showMsg("图片获取失败");
        }
    }


    /**
     * Toast提示
     *
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
                .url(Constant.WEB_SITE + Constant.SIGN_PHOTO)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("uploadImage—No", String.valueOf(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                res = response.body().string();
                upsign();
            }
        });
    }

    private void upsign() {
        OkHttpClient client = new OkHttpClient();
        if (res.isEmpty()){
            res = getIntent().getStringExtra("face_photo");
        }
        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(ID))
                .add("status", String.valueOf(status))
                .add("address", Address)
                .add("face_photo",res)
                .add("content",content)
                .build();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE+"/webapi/Merchant/Edit_Merchant.ashx")
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
                Log.d("WFCActivity_msg", "onResponse: "+res);
                intent = new Intent(WaitForCheckActivity.this, BusinessCheckActivity.class);
                intent.putExtra("role",role);
                intent.putExtra("staff_info_id",staff_info_id);
                startActivity(intent);
            }
        });
    }
}