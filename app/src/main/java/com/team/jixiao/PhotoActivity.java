package com.team.jixiao;

import static com.team.jixiao.RegisterActivity.SELECT_PHOTO;

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
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.team.jixiao.utils.BitmapUtils;
import com.team.jixiao.utils.CameraUtils;
import com.team.jixiao.utils.CommonUtils;

import com.team.jixiao.utils.SPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private File outputImagePath;

    Intent intent;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        pic = findViewById(R.id.pic);
        if(imageUrl != null){
            Glide.with(this).load(imageUrl).apply(requestOptions).into(pic);
        }
        if (imagePath == null){
            imagePath = imageUrl;
        }
        checkVersion();
        takePhoto();
//        Log.e("获取路径", imagePath);
//        intent = new Intent(PhotoActivity.this,MainActivity.class);
//        intent.putExtra("flag",1);
//        startActivity(intent);
//        PhotoActivity.this.finish();
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
        outputImagePath = new File(getExternalCacheDir(), filename + ".jpg");
        Log.e("存储路径", String.valueOf(outputImagePath));
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
                        displayImage(imagePath);
                        Log.e("拍照显示图片", imagePath);
                    }
//                    intent = new Intent(PhotoActivity.this,MainActivity.class);
//                    startActivity(intent);
                    break;

            }
            uploadImage(imagePath);
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
                .url("http://192.168.1.108:8092/Loader/imagesUp.ashx")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("uploadImage——No", String.valueOf(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                String res = response.toString().trim();
                Log.e("uploadImage——image", res);
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
    public static String getTakePhotoPath(Intent data) {
        Bitmap photo = null;
        Uri uri = data.getData();
        if (uri != null) {
            photo = BitmapFactory.decodeFile(uri.getPath());
        }
        if (photo == null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                photo = (Bitmap) bundle.get("data");
            } else {

                return "";
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            // 获取 SD 卡根目录
            String saveDir = Environment.getExternalStorageDirectory() + "/fiberstore_photos";
            // 新建目录
            File dir = new File(saveDir);
            if (!dir.exists()) dir.mkdir();
            // 生成文件名
            SimpleDateFormat t = new SimpleDateFormat("xiebin");
            String filename = "MT" + (t.format(new Date())) + ".jpg";
            /**新建文件*/
            File file = new File(saveDir, filename);
            /***打开文件输出流*/
            fileOutputStream = new FileOutputStream(file);
            // 生成图片文件
            /**
             * 对应Bitmap的compress(Bitmap.CompressFormat format, int quality, OutputStream stream)方法中第一个参数。
             * CompressFormat类是个枚举，有三个取值：JPEG、PNG和WEBP。其中，
             * PNG是无损格式（忽略质量设置），会导致方法中的第二个参数压缩质量失效，
             * JPEG不解释，
             * 而WEBP格式是Google新推出的，据官方资料称“在质量相同的情况下，WEBP格式图像的体积要比JPEG格式图像小40%。
             */
            photo.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            /***相片的完整路径*/
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}