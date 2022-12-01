package com.team.jixiao;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.team.jixiao.utils.CommonUtils;
import com.team.jixiao.utils.Constant;
import com.team.jixiao.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    ShapeableImageView iv_pic;

    @BindView(R.id.type_add)
    ImageButton type_add;

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
    Intent openCamera;
    File filePhotos;
    Uri photoUri;
    String res;
    String x="";
    File file;
    Intent intent;

    String m1 = "";
    String m2 = "";
    String m3 = "";

    String number_no1 = "";
    String number_no2 = "";
    String number_no3 = "";

    String item__no="";
    String item__name="";
    String type__name="";

    String urlSrc = "";

    String content="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_set);
        ButterKnife.bind(this);
        checkVersion();
        content = getIntent().getStringExtra("content");
        if (content==null){
            content = "";
        }
        type_name.setText(content);
    }
    @OnClick({R.id.btn_sure,R.id.btn_submit,R.id.iv_pic,R.id.type_add})
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
//                    type_number_no1.setText(type_no1.getText().toString());
                    type_number_no1.setText("11+");
                }

                if (no2<=5){
                    type_number_no2.setText("0-5");
                }else if (no2>5&&no2<=10){
                    type_number_no2.setText("6-10");
                }else {
//                    type_number_no2.setText(type_no2.getText().toString());
                    type_number_no2.setText("11+");
                }

                if (no3<=5){
                    type_number_no3.setText("0-5");
                }else if (no3>5&&no3<=10){
                    type_number_no3.setText("6-10");
                }else {
//                    type_number_no3.setText(type_no3.getText().toString());
                    type_number_no3.setText("11+");
                }
                number_no1 = String.valueOf(no1);
                number_no2 = String.valueOf(no2);
                number_no3 = String.valueOf(no3);
                break;
            case R.id.iv_pic:
//                Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show();

                bottomSheetDialog = new BottomSheetDialog(this);
                bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
                bottomSheetDialog.setContentView(bottomView);
//        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
                bottomSheetDialog.show();
                TextView tvTakePictures = bottomView.findViewById(R.id.tv_take_pictures);
                TextView tvOpenAlbum = bottomView.findViewById(R.id.tv_open_album);
                TextView tvCancel = bottomView.findViewById(R.id.tv_cancel);

                //拍照
                tvTakePictures.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        takePhoto();
                        CommonUtils.showShortMsg(BillSetActivity.this,"拍照");
                        bottomSheetDialog.cancel();
                    }
                });
                tvOpenAlbum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openAlbum();
                        CommonUtils.showShortMsg(BillSetActivity.this,"打开相册");
                        bottomSheetDialog.cancel();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });
                //底部弹窗显示
                bottomSheetDialog.show();
                break;
            case R.id.btn_submit:

                item__no = et_item_no.getText().toString().trim();
                item__name = et_item_name.getText().toString().trim();
                type__name = type_name.getText().toString().trim();

                m1 = type_amount_no1.getText().toString().trim();
                m2 = type_amount_no2.getText().toString().trim();
                m3 = type_amount_no3.getText().toString().trim();

                if (item__no.isEmpty()||item__name.isEmpty()||type__name.isEmpty()){
                    Toast.makeText(this, "请检查是否填写完整！", Toast.LENGTH_SHORT).show();
                }else{
                    if (m1.isEmpty()&&m2.isEmpty()&&m3.isEmpty()){
                        Toast.makeText(this, "金额不能为空！", Toast.LENGTH_SHORT).show();
                    }else if (m1.substring(0,1).equals("0")||m2.substring(0,1).equals("0")||m3.substring(0,1).equals("0")){
                        Toast.makeText(this, "金额不能输入为0！", Toast.LENGTH_SHORT).show();
                    }else{
                        if (imagePath!=null){
                            x=imagePath;
                        }else if (filePhotos!=null){
                            x= String.valueOf(filePhotos);
                        }else if (x.isEmpty()){
                            Toast.makeText(this, "还未选择图片", Toast.LENGTH_SHORT).show();
                        }
                        uploadImage(x);
                        Toast.makeText(this, "已提交！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.type_add:
                String x = "";
                x = type_name.getText().toString();
                intent = new Intent(BillSetActivity.this,BillSetTypeAddActivity.class);
                intent.putExtra("typename",x);
                startActivity(intent);
                break;
            default:
                break;
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
                .url("https://songhui.hcpos.com/api/Upload/newUpload.ashx")
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
                Log.e("res",res );
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String x = jsonObject.optString("data");
                    Log.i("data", x);
                    JSONObject jsonObject1 = new JSONObject(x);
                    urlSrc = jsonObject1.optString("urlSrc");
                    Log.i("urlSrc", urlSrc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                UpSign();
            }
        });
    }
    private void UpSign() {
        OkHttpClient client = new OkHttpClient();

        FormBody body = new FormBody.Builder()
                .add("item_no", item__no)
                .add("item_name", item__name)
                .add("gg",type__name)
                .add("pl_num1",number_no1)
                .add("pl_price1",m1)
                .add("pl_num2",number_no2)
                .add("pl_price2",m2)
                .add("pl_num3",number_no3)
                .add("pl_price3",m3)
                .add("picture_url",urlSrc)
                .build();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE+"/webapi/bd/base_add.ashx")
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
                Log.d("BillSetActivity_msg", "onResponse: "+res);
//                intent = new Intent(PhotoActivity.this,MainActivity.class);
//                intent.putExtra("role",role);
//                intent.putExtra("staff_info_id",staff_info_id);
//                startActivity(intent);
//                finish();
            }
        });
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
                            CommonUtils.showShortMsg(BillSetActivity.this,"已获取权限");
                            hasPermissions = true;
                        } else {//申请失败
                            CommonUtils.showShortMsg(BillSetActivity.this,"权限未开启");
                            hasPermissions = false;
                        }
                    });
        } else {
            //Android6.0以下
            CommonUtils.showShortMsg(BillSetActivity.this,"无需请求动态权限");
        }
    }

    public void changeAvatar(View view) {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
        bottomSheetDialog.setContentView(bottomView);
//        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
        bottomSheetDialog.show();
        TextView tvTakePictures = bottomView.findViewById(R.id.tv_take_pictures);
        TextView tvOpenAlbum = bottomView.findViewById(R.id.tv_open_album);
        TextView tvCancel = bottomView.findViewById(R.id.tv_cancel);

        //拍照
        tvTakePictures.setOnClickListener(v -> {
//            takePhoto();
            CommonUtils.showShortMsg(BillSetActivity.this,"拍照");
            bottomSheetDialog.cancel();
        });
        //打开相册
        tvOpenAlbum.setOnClickListener(v -> {
            openAlbum();
            CommonUtils.showShortMsg(BillSetActivity.this,"打开相册");
            bottomSheetDialog.cancel();
        });
        //取消
        tvCancel.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
        });
        //底部弹窗显示
        bottomSheetDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void takePhoto() {
        // cek permission camera
        if (canAccessCamera()) {
            // request permission
            requestPermissions(
                    new String[] { Manifest.permission.CAMERA },
                    TAKE_PHOTO
            );
        } else {
            takePictures();
        }

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    BillSetActivity.this,
                    new String[] { Manifest.permission.CAMERA },
                    TAKE_PHOTO
            );
        }
    }
    private void takePictures() {
        if (ContextCompat.checkSelfPermission(
                BillSetActivity.this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(openCamera.resolveActivity(getPackageManager()) != null) {
                filePhotos = null;
                try {
                    filePhotos = createImages();
                } catch (IOException IoEx) {
                    Log.e("CAMS", IoEx.getMessage());
                }

                if(filePhotos != null) {
                    photoUri = FileProvider.getUriForFile(
                            this,
                            getPackageName(),
                            filePhotos
                    );
                    Log.e("filePhotos", "takePictures: "+filePhotos );
                    Log.e("photoUri", String.valueOf(photoUri));
                    openCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    PictureActivityResultLauncher.launch(openCamera);

                }

            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessCamera() {
        return (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.CAMERA));
    }

    private File createImages() throws IOException {
        String timestamps = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());

        String imgFiles = "JPEG_" + timestamps;

        File storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imgFiles,
                ".jpeg",
                storage
        );
    }
    private void openAlbum() {
        if (!hasPermissions) {
            CommonUtils.showShortMsg(BillSetActivity.this,"未获取到权限");
            checkVersion();
            return;
        }
        startActivityForResult(CameraUtils.getSelectPhotoIntent(), SELECT_PHOTO);
    }
    private void deleteUnusedFile(Uri fileUri){
        File fileDelete = new File(fileUri.getPath());
        if(fileDelete.exists()) {
            if(fileDelete.delete()) {
                Log.i("TEMP", "Temporary file is deleted");
            } else {
                Log.i("TEMP", "Temporary file is not deleted");
            }
        }
    }
    ActivityResultLauncher<Intent> PictureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Uri fileUri = FileProvider.getUriForFile(
                            BillSetActivity.this,
                            getPackageName(),
                            filePhotos);

                    switch (result.getResultCode()) {
                        case Activity.RESULT_CANCELED:
                            deleteUnusedFile(fileUri);
                            break;
                        case Activity.RESULT_OK:
                            Bitmap bitmap;
                            Intent data = getIntent();
                            try {
                                if (data.hasExtra("data")) {
                                    bitmap = (Bitmap) data.getExtras().get("data");
                                } else {
                                    photoUri = fileUri;
                                    if (Build.VERSION.SDK_INT < 28) {
                                        bitmap = MediaStore.Images.Media.getBitmap(
                                                BillSetActivity.this.getContentResolver(), photoUri
                                        );
                                    } else {
                                        ImageDecoder.Source source =
                                                ImageDecoder.createSource(
                                                        BillSetActivity.this.getContentResolver(), photoUri
                                                );
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                    }
                                }
                                //图片显示
                                iv_pic.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                Log.e("ERR_PIC", "Photo error : " + e.toString());
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
    );
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
//
//            //显示图片
//            Glide.with(this).load(imagePath).apply(requestOptions).into(iv_pic);

            //1
            Log.e("压缩imagePath", imagePath);
            //压缩图片
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null){
                orc_bitmap = CameraUtils.compression(bitmap);
            }else {
                orc_bitmap = null;
            }
            iv_pic.setImageBitmap(bitmap);
            //转Base64
            base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);

        } else {
            CommonUtils.showShortMsg(BillSetActivity.this,"图片获取失败");
        }
    }

}