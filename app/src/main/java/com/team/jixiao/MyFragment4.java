package com.team.jixiao;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyFragment4 extends Fragment {
    @BindView(R.id.btn_exit)
    Button btn_exit;

    private long exitTime = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout4,container,false);
        ButterKnife.bind(getActivity());
        return view;
    }
    @OnClick({R.id.btn_exit})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.btn_exit:
                    System.exit(0);
                break;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == event.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return false;

    }
}
