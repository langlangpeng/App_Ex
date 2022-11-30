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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
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
import java.util.Locale;

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

    private static final int CAMERA_REQ = 1001;
    private static final int VIDEO_REQ = 1002;

    Intent openCamera;

    MediaController mediaController;

    File filePhotos;
    Uri photoUri;
    @RequiresApi(api = Build.VERSION_CODES.M)
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

        // cek permission camera
        if (canAccessCamera()) {
            // request permission
            requestPermissions(
                    new String[] { Manifest.permission.CAMERA },
                    CAMERA_REQ
            );
        } else {
            takePictures();
        }

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    PhotoActivity.this,
                    new String[] { Manifest.permission.CAMERA },
                    CAMERA_REQ
            );
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

    private void takePictures() {
        if (ContextCompat.checkSelfPermission(
                PhotoActivity.this, Manifest.permission.CAMERA) ==
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

    ActivityResultLauncher<Intent> PictureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Uri fileUri = FileProvider.getUriForFile(
                            PhotoActivity.this,
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
                                                PhotoActivity.this.getContentResolver(), photoUri
                                        );
                                    } else {
                                        ImageDecoder.Source source =
                                                ImageDecoder.createSource(
                                                        PhotoActivity.this.getContentResolver(), photoUri
                                                );
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                    }
                                }
                                //图片显示
                                pic.setImageBitmap(bitmap);
                                uploadImage(String.valueOf(filePhotos));
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
                Log.e("uploadImage—No", String.valueOf(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                res = response.body().string();
                Log.e("res",res );
                UpSign();
            }
        });
    }

    private void UpSign() {
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQ) {
            if(canAccessCamera()) {
                Toast.makeText(PhotoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            } else {
                takePictures();
            }
        }
    }
}