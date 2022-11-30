package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.team.jixiao.Entity.MerChant;
import com.team.jixiao.utils.CommonUtils;
import com.team.jixiao.utils.L;
import com.team.jixiao.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EMapActivity extends AppCompatActivity implements AMapLocationListener, LocationSource {
    private int role = -1;
    private int staff_info_id = 0;

    private Handler mainHandler;
    Intent intent;

    Gson gson = new Gson();

    private static final int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.tv_city)
    TextView tv_city;

    private AMap aMap;//地图对象
    private Circle circle;
    private Polygon polygon;
    private Marker marker;
//    private PolygonOptions polygonOptions;
    private LatLng locLatLng = null; // 定位坐标
    private LatLng comLatLng = null; // 公司坐标
    private float radius = 200;

    List<LatLng> latLngList = new ArrayList<>();
    List<LatLng> latLngCenterList = new ArrayList<>();
    List<String> area_nameList = new ArrayList<>();
    List<MarkerOptions> markerOptionsList = new ArrayList<>();
    List<MerChant> merChantList = new ArrayList<>();
    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    LatLng latLng1 = new LatLng(30.60098, 114.281123);
    LatLng latLng2 = new LatLng(30.599022, 114.278323);
    LatLng latLng3 = new LatLng(30.597424, 114.280029);
    LatLng latLng4 = new LatLng(30.600139, 114.2827);
    LatLng latLng = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emap);
        ButterKnife.bind(this);

        role = getIntent().getIntExtra("role", -1);
        staff_info_id = getIntent().getIntExtra("staff_info_id", 0);
        Log.e("EMapActivity_role:", String.valueOf(role));
        Log.e("EMapActivity_staff_info_id:", String.valueOf(staff_info_id));
        mainHandler = new Handler(getMainLooper());
        // 启动新的线程
        new TimeThread().start();
        // 显示地图，必须要写
        mapView.onCreate(savedInstanceState);
        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getRequest();
        mainHandler = new MainHandler();

    }

    private void initView() throws Exception {

        //获取地图对象
        aMap = mapView.getMap();
        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(true);
        // 是否显示地图方向盘
        settings.setCompassEnabled(true);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);

        //定位的小图标 默认是蓝点，其实就是一张图片
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_point));
//        myLocationStyle.radiusFillColor(R.color.transparent1);
//        myLocationStyle.strokeColor(R.color.transparent1);
//        aMap.setMyLocationStyle(myLocationStyle);

        // 判断是否为Android 6.0 以上的系统版本，如果是，需要动态添加权限
        if (Build.VERSION.SDK_INT >= 23) {
            showPermissions();
        } else {
            // 开始定位
            initLoc();
        }

    }

    // 定位
    private void initLoc() throws Exception {

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(3000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    // 定位回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(amapLocation);
                    //添加图钉
                    aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getAddress());
                    tv_city.setText(amapLocation.getCity());
//                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    L.i("定位地址：" + buffer.toString());
                    // 记录当前定位的坐标
                    locLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }



    /**
     * 绘制圆圈
     *
     * @param latLng
     */
    public void drawCircle(LatLng latLng) {
        if (circle != null) {
            circle = null;
        }
        circle = aMap.addCircle(new CircleOptions()
                .center(latLng).radius(radius)
                .fillColor(Color.argb(50, 1, 1, 1))
                .strokeColor(Color.argb(50, 1, 1, 1))
                .strokeWidth(15));
    }


    //激活定位
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;

    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * Android 6.0 以上的版本的定位方法
     */
    public void showPermissions() throws Exception {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE
            }, WRITE_COARSE_LOCATION_REQUEST_CODE);
        } else {
            // 开始定位
            initLoc();
        }
    }

    // Android 6.0 以上的版本申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case WRITE_COARSE_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 开始定位
                    try {
                        initLoc();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // 没有获取到权限，做特殊处理
                    ToastUtil.showToastLong("获取位置权限失败，请手动开启");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  // 消息(一个整型值)
                    mHandler.sendMessage(msg); // 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    // 在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler(msg -> {
        switch (msg.what) {
            case 1:
                long sysTime = System.currentTimeMillis(); // 获取系统时间
                CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime); // 时间显示格式
                break;
            default:
                break;
        }
        return false;
    });

    private void getRequest() {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(staff_info_id))
                .build();

        Request request = new Request.Builder()
                .url("http://47.92.214.113:8092/webapi/electron_fenced/fenced_List.ashx")
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
                Message msg = new Message();
//                Log.d("EMAP_msg", "onResponse: "+res);
                msg.what = 1;
                msg.obj = res;
                mainHandler.sendMessage(msg);
            }
        });
    }
    class MainHandler extends Handler implements AMap.OnMarkerClickListener {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 1:
                    if (msg.obj != null) {
                        String vlResult = (String) msg.obj;
                        Log.e("INFO_String", vlResult);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(vlResult);
                            JSONObject array = jsonObject.optJSONObject("data");
                            Log.e("data", String.valueOf(array));
                            Log.e("分隔符", "*******************************************************************************");
                            JSONArray jsonArray1 = array.optJSONArray("merchant");
                            String x1 = String.valueOf(jsonArray1);
                            Log.e("merchant", x1);
                            Log.e("分隔符", "*******************************************************************************");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject2 = jsonArray1.optJSONObject(i);
                                if (jsonObject2 != null) {
                                    double longitude = jsonObject2.optDouble("longitude");
                                    double latitude = jsonObject2.optDouble("latitude");
                                    String Merchant_Name = jsonObject2.optString("Merchant_Name");
                                    LatLng latLng = new LatLng(latitude,longitude);
                                    MerChant merChant = new MerChant();
                                    merChant.setLatitude(latitude);
                                    merChant.setLongitude(longitude);
                                    merChant.setMerchant_Name(Merchant_Name);
                                    merChant.setLatLng(latLng);
                                    merChantList.add(merChant);
                                }
                            }

                            for (int i = 0; i < merChantList.size(); i++) {
                                double longitude = merChantList.get(i).getLongitude();
                                double latitude = merChantList.get(i).getLatitude();
                                String Merchant_Name = merChantList.get(i).getMerchant_Name();
                                LatLng latLng = merChantList.get(i).getLatLng();
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("目的地").snippet(Merchant_Name);
                                markerOptionsList.add(markerOptions);
                                aMap.addMarker(markerOptionsList.get(i));
                                aMap.setOnMarkerClickListener(marker -> {

                                    if (isPackageInstalled("com.autonavi.minimap")==true){
                                        Log.e("高德", String.valueOf(isPackageInstalled("com.autonavi.minimap")==true));
                                        Intent naviIntent = new Intent("android.intent.action.VIEW", Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat="+ latitude +"&dlon="+ longitude+"&dname=" +
                                                Merchant_Name +
                                                "&dev=0&t=2"));
                                        startActivity(naviIntent);
                                    }else if (isPackageInstalled("com.baidu.BaiduMap")==true){
                                        Log.e("百度", String.valueOf(isPackageInstalled("com.baidu.BaiduMap")==true));
                                        double baidulatitude = GCJ2BD(latLng).latitude;
                                        double baidulongitude = GCJ2BD(latLng).longitude;
                                        Intent naviIntent = new Intent("android.intent.action.VIEW", Uri.parse("baidumap://map/geocoder?location=" + baidulatitude + "," + baidulongitude));
                                        startActivity(naviIntent);
                                    }else if (isPackageInstalled("com.tencent.map")==true){
                                        Log.e("腾讯", String.valueOf(isPackageInstalled("com.tencent.map")==true));
                                        Intent naviIntent = new Intent("android.intent.action.VIEW", Uri.parse("qqmap://map/routeplan?type=drive&from=&fromcoord=&to=" +
                                                Merchant_Name +
                                                "&tocoord=" + latitude + "," + longitude + "&policy=0&referer=appName"));
                                        startActivity(naviIntent);
                                    }else{
                                        CommonUtils.showShortMsg(EMapActivity.this,"本手机未装任何导航软件！");
                                    }
                                    return false;
                                });
                            }













                            JSONArray jsonArray = array.optJSONArray("electron_fenced");
                            String x = String.valueOf(jsonArray);
                            Log.e("electron_fenced", x);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                latLngList = new ArrayList<>();

                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                if (jsonObject1 != null){
                                    Log.e("分隔符", "*******************************************************************************");
                                    String y = String.valueOf(jsonObject1);
                                    Log.e("jsonObject1", y);
                                    String coordinate1 = jsonObject1.optString("coordinate");
                                    String area_name = jsonObject1.optString("area_name");
                                    area_nameList.add(area_name);
                                    String substring = coordinate1.replace("[", "");
                                    substring = substring.replace("]","");
                                    System.out.println(substring);
                                    List<String> list1 = new ArrayList<>();
                                    List<String> list = Arrays.asList(substring.split(","));
                                    for (int j = 0; j < list.size(); j+=2) {
                                        double v = Double.parseDouble(list.get(j));
                                        double v1 = Double.parseDouble(list.get(j+1));
                                        list1.add(v+","+v1);
                                    }
                                    List<double[]> points = new ArrayList<>();
                                    for (String str:list1){
                                        String[] ss = str.split(",");
                                        double[] point = new double[2];
                                        point[0] = Double.valueOf(ss[0]);
                                        point[1] = Double.valueOf(ss[1]);
                                        points.add(point);
                                    }
                                    String centerPoint = getCenterPointFromListOfCoordinates(points);
                                    String[] strs = centerPoint.split(",");//根据，切分字符串
                                    for(int j = 0;j < strs.length; j+=2){
                                        double v = Double.parseDouble(strs[j]);
                                        double v1 = Double.parseDouble(strs[j+1]);
                                        LatLng latLng1 = new LatLng(v1,v);
//                                        latLngCenterList.add(latLng1);
                                    }

                                    Log.e("centerPoint", centerPoint );
                                    for (int j = 0; j < list.size(); j+=2) {
                                        System.out.println(list.get(j));
                                        double v = Double.parseDouble(list.get(j));
                                        System.out.println(v);
                                        double v1 = Double.parseDouble(list.get(j+1));
                                        System.out.println(v1);
                                        latLng = new LatLng(v1,v);
                                        System.out.println(latLng);
                                        latLngList.add(latLng);
                                        PolygonOptions polygonOptions = new PolygonOptions();
                                        polygonOptions.addAll(latLngList);
                                        polygonOptions.strokeWidth(5) // 多边形的边框
                                                .strokeColor(Color.parseColor("#169ffa")) // 边框颜色
                                                .fillColor(Color.parseColor("#f47920"));   // 多边形的填充色
                                        polygon = aMap.addPolygon(polygonOptions);


                                    }
                                    Log.e("分隔符", "*******************************************************************************");
                                }
                            }
//                            for (int i = 0; i < latLngCenterList.size(); i++) {
//                                marker = aMap.addMarker(new MarkerOptions().position(latLngCenterList.get(i)).title("目的地").snippet(area_nameList.get(i)));
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            switch (marker.getId()){

            }
            return false;
        }
    }
    // 自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation) {
        //设置图钉选项
        MarkerOptions my_option = new MarkerOptions();
        //图标
        my_option.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_point));
        // 记录公司的坐标，这里的公司坐标是随机生成
        locLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
        my_option.position(locLatLng);
        // 绘制圆圈
        drawCircle(locLatLng);
//        drawPolygonOptions();
        StringBuffer buffer = new StringBuffer();
        buffer.append(amapLocation.getAddress());
        //标题
        my_option.title("我所在位置");
        //子标题
        my_option.snippet("地址：" + amapLocation.getProvince() + amapLocation.getCity() +
                amapLocation.getDistrict() + amapLocation.getStreet());
        //设置多少帧刷新一次图片资源
        my_option.period(60);

        return my_option;
    }
    private static String getCenterPointFromListOfCoordinates(List<double[]> coordinateList) {
        int total = coordinateList.size();
        double X = 0;
        double Y = 0;
        double Z = 0;
        for (double[] coordinate : coordinateList) {
            double lat = coordinate[1] * Math.PI / 180;
            double lon = coordinate[0] * Math.PI / 180;
            X += Math.cos(lat) * Math.cos(lon);
            Y += Math.cos(lat) * Math.sin(lon);
            Z += Math.sin(lat);
        }
        X = X / total;
        Y = Y / total;
        Z = Z / total;
        double lon2 = Math.atan2(Y, X);
        double hyp = Math.sqrt(X * X + Y * Y);
        double lat2 = Math.atan2(Z, hyp);
        double longitude = lon2 * 180 / Math.PI;
        double latitude = lat2 * 180 / Math.PI;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(6);
        return nf.format(longitude) + "," + nf.format(latitude);
    }
    public static boolean isPackageInstalled(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
    /**
     * BD-09 坐标转换成 GCJ-02 坐标
     */
    public static void BD2GCJ(double Longitude,double Latitude) {
        double x = Longitude - 0.0065, y = Latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);

        String longitude = String.valueOf(z * Math.cos(theta));//lng
        String latitude = String.valueOf(z * Math.sin(theta));//lat
    }

    /**
     * GCJ-02 坐标转换成 BD-09 坐标
     */
    public static LatLng GCJ2BD(LatLng bd) {
        double x = bd.longitude, y = bd.latitude;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        double tempLon = z * Math.cos(theta) + 0.0065;
        double tempLat = z * Math.sin(theta) + 0.006;
        return new LatLng(tempLat, tempLon);
    }
}