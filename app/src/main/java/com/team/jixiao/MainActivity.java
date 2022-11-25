package com.team.jixiao;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.team.jixiao.Fragment.MyFragment1;
import com.team.jixiao.Fragment.MyFragment2;
import com.team.jixiao.Fragment.MyFragment3;
import com.team.jixiao.Fragment.MyFragment4;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AMapLocationListener {
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    List<Fragment> fragments;
    MenuItem menuItem;
    int flag = 9;
    private String imagePath = "";

    private int role = -1;
    private int staff_info_id = 0;

    double Latitude = 0;//获取纬度
    double Longitude = 0;//获取经度
    String Address = "无";//地址

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    AMapLocationClient locationClient = null;

    final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
//        }


        role = getIntent().getIntExtra("role",-1);
        staff_info_id = getIntent().getIntExtra("staff_info_id",0);
        Log.e("Main_role:", String.valueOf(role));
        Log.e("Main_staff_info_id:", String.valueOf(staff_info_id));

        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragments = new ArrayList<>();
        fragments.add(new MyFragment1());
        fragments.add(new MyFragment2());
        fragments.add(new MyFragment3());
        fragments.add(new MyFragment4());
        myAdatpter adatpter = new myAdatpter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adatpter);//viewPager添加数据
        try {
            locationClient = new AMapLocationClient(this);
            AMapLocationClientOption option = new AMapLocationClientOption();
            /**
             * 设置签到场景，相当于设置为：
             * option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
             * option.setOnceLocation(true);
             * option.setOnceLocationLatest(true);
             * option.setMockEnable(false);
             * option.setWifiScan(true);
             * option.setGpsFirst(false);
             * 其他属性均为模式属性。
             * 如果要改变其中的属性，请在在设置定位场景之后进行
             */
            option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
            locationClient.setLocationOption(option);
            //设置定位监听
            locationClient.setLocationListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationClient.startLocation();
        //底部导航添加列表监听事件，通过底部导航监听事件改变viewpager页
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item1:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.item2:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.item3:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.item4:
                        viewPager.setCurrentItem(3);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        //viewPager添加页改变监听事件，通过viewpager的改变设置底部导航
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (menuItem == null) {
                    menuItem = bottomNavigationView.getMenu().getItem(0);
                }
                //将上次的选择设置为false，等待下次的选择
                menuItem.setChecked(false);
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            Latitude = aMapLocation.getLatitude();
            Longitude = aMapLocation.getLongitude();
            Address = aMapLocation.getAddress();

        } else {
            //可以记录错误信息，或者根据错误错提示用户进行操作，Demo中只是打印日志
            Log.e("AMap", "签到定位失败，错误码：" + aMapLocation.getErrorCode() + ", " + aMapLocation.getLocationDetail());
            //提示错误信息
//            tvResult.setText("签到失败");
        }
    }

    private class myAdatpter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public myAdatpter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getStaff_info_id() {
        return staff_info_id;
    }

    public void setStaff_info_id(int staff_info_id) {
        this.staff_info_id = staff_info_id;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时，需要销毁定位client
        if (null != locationClient) {
            locationClient.onDestroy();
        }
    }
}
