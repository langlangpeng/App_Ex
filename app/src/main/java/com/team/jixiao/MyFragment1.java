package com.team.jixiao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyFragment1 extends Fragment {

    Intent intent;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG", "onAttach is invoke");
        String titles = ((MainActivity) context).getTitles();//通过强转成宿主activity，就可以获取到传递过来的数据
        Log.d("TAG", "onAttach is invoke context "+titles);
        String titles1 = ((MainActivity)getActivity()).getTitles();
        Log.d("TAG", "onAttach is invoke getActivity "+titles1);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout1,container,false);
        return view;
}

}
