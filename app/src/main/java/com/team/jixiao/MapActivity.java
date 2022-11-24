package com.team.jixiao;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.team.jixiao.utils.CommonUtils;


import java.io.File;

public class MapActivity extends AppCompatActivity {
    private MapView mMapView;
    private AMapLocationClient mLocationClient = null; //位置client
    private AMap mAMap;
    //https://lbs.amap.com/console/show/picker
    //GCJ-02
    private double myLongitude=114.28399;//经度
    private double myLatitude=30.638663;//维度
    //百度
    //114.27753146990442,30.63243765914121
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myLongitude = getIntent().getDoubleExtra("Longitude",0);
        myLatitude = getIntent().getDoubleExtra("Latitude",0);
        mMapView = findViewById(R.id.mapview);
        //显示定位地图
        mMapView.onCreate(savedInstanceState);
        try {
            setMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.e("TAG", BD2GCJ(myLongitude,myLatitude));//百度

        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPackageInstalled("com.autonavi.minimap")==true){
                    Log.e("高德", String.valueOf(isPackageInstalled("com.autonavi.minimap")==true));
                    Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat="+ myLatitude +"&dlon="+ myLongitude+"&dname=" +
                            "目的地" +
                            "&dev=0&t=2"));
                    startActivity(naviIntent);
                }else if (isPackageInstalled("com.baidu.BaiduMap")==true){
                    Log.e("百度", String.valueOf(isPackageInstalled("com.baidu.BaiduMap")==true));
                    Intent naviIntent = new Intent("android.intent.action.VIEW", Uri.parse("baidumap://map/geocoder?location=" + 30.63243765914121 + "," + 114.27753146990442));
                    startActivity(naviIntent);
                }else if (isPackageInstalled("com.tencent.map")==true){
                    Log.e("腾讯", String.valueOf(isPackageInstalled("com.tencent.map")==true));
                    Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=&fromcoord=&to=" +
                            "目的地" +
                            "&tocoord=" + myLatitude + "," + myLongitude + "&policy=0&referer=appName"));
                    startActivity(naviIntent);
                }else{
                    CommonUtils.showShortMsg(MapActivity.this,"本手机未装任何导航软件！");
                }

                // 高德地图-GCJ02

                // 百度地图-BD-09
//                Intent naviIntent = new Intent("android.intent.action.VIEW", Uri.parse("baidumap://map/geocoder?location=" + 30.63243765914121 + "," + 114.27753146990442));
//                startActivity(naviIntent);

//                // 腾讯地图-GCJ02
//                Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=&fromcoord=&to=" +
//                        "目的地" +
//                        "&tocoord=" + myLatitude + "," + myLongitude + "&policy=0&referer=appName"));
//                startActivity(naviIntent);

            }
        });


    }

    private void setMap() throws Exception {
        mAMap = mMapView.getMap();
        initLocation();//初始化位置信息
        setPositionStyle(); //设置显示定位的样式显示
        //启动定位
        mLocationClient.startLocation();
    }

    private void initLocation() throws Exception {
        //初始化位置
        LatLng latLng = new LatLng(myLatitude,myLongitude);
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));//“16”即缩放的比例
        mLocationClient=new AMapLocationClient(getApplicationContext());//初始化mLocationClient
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                StringBuffer sb=new StringBuffer();
                if (aMapLocation!=null){
                    if (aMapLocation.getErrorCode()==0) {
                        //解析定位结果
                        sb.append("定位成功：");
                        sb.append("定位类型："+aMapLocation.getLocationType()+"\n");
                        sb.append(aMapLocation.getLongitude()+"\n");
                        sb.append(aMapLocation.getLatitude()+"\n");
                        sb.append("精确度："+aMapLocation.getAccuracy()+"米"+"\n");
                        sb.append("提供者："+aMapLocation.getProvider()+"\n");
                    }
                }
                Log.d("TAG", "onLocationChanged: "+sb.toString());
            }
        });
    }

    private void setPositionStyle() {
        MyLocationStyle myLocationStyle=new MyLocationStyle();
        myLocationStyle.interval(8000);//8秒执行一次位置更新
        myLocationStyle.showMyLocation(true);
        myLocationStyle.strokeWidth(5);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//位置样式
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.getUiSettings().setMyLocationButtonEnabled(true); //显示地图位置按钮
        mAMap.setMyLocationEnabled(true); //设置地图是否显示位置
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
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
    public static boolean isPackageInstalled(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

}
