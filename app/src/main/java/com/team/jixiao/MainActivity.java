package com.team.jixiao;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    List<Fragment> fragments;
    MenuItem menuItem;
    int flag = 9;
    private String imagePath = "";

    private int role = -1;
    private int staff_info_id = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        role = getIntent().getIntExtra("role",-1);
        staff_info_id = getIntent().getIntExtra("staff_info_id",0);
        Log.e("Main_role:", String.valueOf(role));
        Log.e("Main_staff_info_id:", String.valueOf(staff_info_id));
//        imagePath = getIntent().getStringExtra("imagePath");
//        if (imagePath!=null){
//            Log.d("TAG", imagePath);
//        }
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragments = new ArrayList<>();
        fragments.add(new MyFragment1());
        fragments.add(new MyFragment2());
        fragments.add(new MyFragment3());
        fragments.add(new MyFragment4());
        myAdatpter adatpter = new myAdatpter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adatpter);//viewPager添加数据
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
    public String getTitles(){
        return "getTitle111";
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

}
