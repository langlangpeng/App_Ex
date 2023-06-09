package com.team.jixiao.Fragment;

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

import com.team.jixiao.BillActivity;
import com.team.jixiao.BusinessCheckActivity;
import com.team.jixiao.ClientVisitActivity;
import com.team.jixiao.EMapActivity;
import com.team.jixiao.MainActivity;
import com.team.jixiao.R;
import com.team.jixiao.StuffInfoActivity;



public class MyFragment1 extends Fragment implements View.OnClickListener {
    LinearLayout InStuff,EMap,business_check,ClientVisit,Bill;
    Intent intent;
    int role = -1;
    int staff_info_id = 0;


    public void onAttach(Context context) {
        super.onAttach(context);
        role = ((MainActivity) context).getRole();
        staff_info_id = ((MainActivity) context).getStaff_info_id();
        Log.e("MyFragment1_role", String.valueOf(role));
        Log.e("MyFragment1_staff_info_id", String.valueOf(staff_info_id));
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout1,container,false);

        InStuff = view.findViewById(R.id.InStuff);
        EMap = view.findViewById(R.id.EMap);
        business_check = view.findViewById(R.id.business_check);
        ClientVisit = view.findViewById(R.id.ClientVisit);
        Bill = view.findViewById(R.id.Bill);

        if (role!=0){
            InStuff.setVisibility(View.INVISIBLE);
            EMap.setVisibility(View.INVISIBLE);
        }
        InStuff.setOnClickListener(this);
        business_check.setOnClickListener(this);
        EMap.setOnClickListener(this);
        ClientVisit.setOnClickListener(this);
        Bill.setOnClickListener(this);
        return view;
}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.InStuff:
                intent = new Intent(getActivity(), StuffInfoActivity.class);
                intent.putExtra("role",role);
                intent.putExtra("staff_info_id",staff_info_id);
                startActivity(intent);
                break;
            case R.id.business_check:
                intent = new Intent(getActivity(), BusinessCheckActivity.class);
                intent.putExtra("role",role);
                intent.putExtra("staff_info_id",staff_info_id);
                startActivity(intent);
                break;
            case R.id.EMap:
                intent = new Intent(getActivity(), EMapActivity.class);
                intent.putExtra("role",role);
                intent.putExtra("staff_info_id",staff_info_id);
                startActivity(intent);
                break;
            case R.id.ClientVisit:
                intent = new Intent(getActivity(), ClientVisitActivity.class);
                intent.putExtra("role",role);
                intent.putExtra("staff_info_id",staff_info_id);
                startActivity(intent);
                break;
            case R.id.Bill:
                intent = new Intent(getActivity(), BillActivity.class);
                intent.putExtra("role",role);
                intent.putExtra("staff_info_id",staff_info_id);
                startActivity(intent);
                break;
        }

    }

}
