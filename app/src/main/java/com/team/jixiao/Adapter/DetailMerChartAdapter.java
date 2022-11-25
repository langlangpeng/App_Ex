package com.team.jixiao.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team.jixiao.BusinessInfoActivity;
import com.team.jixiao.Entity.Detail_Merchant;
import com.team.jixiao.MapActivity;
import com.team.jixiao.R;
import com.team.jixiao.WaitForCheckActivity;

import java.util.List;

public class DetailMerChartAdapter extends BaseAdapter {
    private Context context;
    private List<Detail_Merchant> list;
    private Intent intent;
    int role = -1;
    int staff_info_id =0;
    public DetailMerChartAdapter(Context context, List<Detail_Merchant> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }
    public void setData(List<Detail_Merchant> list, int role, int staff_info_id){
        this.list = list;
        this.role = role;
        this.staff_info_id = staff_info_id;
//        Log.e("DetailMerChartAdapter", "setData: "+list );
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Detail_Merchant getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.check_layout,null);
            viewHolder = new ViewHolder();

            viewHolder.tv_Merchant_Name = convertView.findViewById(R.id.tv_Merchant_Name);
            viewHolder.tv_address = convertView.findViewById(R.id.tv_address);
            viewHolder.tv_add_time = convertView.findViewById(R.id.tv_add_time);

            viewHolder.imageButton_map = convertView.findViewById(R.id.imageButton_map);
            viewHolder.imageButton_call = convertView.findViewById(R.id.imageButton_call);

            viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
            convertView.setTag(viewHolder);

            Detail_Merchant item = list.get(position);

            viewHolder.tv_Merchant_Name.setText(item.getMerchant_Name());
            viewHolder.tv_address.setText(item.getAddress());
            viewHolder.tv_add_time.setText(item.getAdd_time().substring(0,10));


            viewHolder.imageButton_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+item.getMobile()));
                    context.startActivity(intent);
                }
            });
            viewHolder.imageButton_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,MapActivity.class);
                    intent.putExtra("Latitude",item.getLatitude());
                    intent.putExtra("Longitude",item.getLongitude());
                    context.startActivity(intent);
                }
            });
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.msb_default_person)
                    .error(R.drawable.msb_default_person)
                    .fallback(R.drawable.msb_default_person)
                    .override(100,100); //override指定加载图片大小
            Glide.with(context)
                    .load(item.getUrl()+item.getFace_photo())
                    .apply(requestOptions)
                    .into(viewHolder.iv_icon);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getStatus()==0){
                        intent = new Intent(context, WaitForCheckActivity.class);
                        intent.putExtra("Mobile",item.getMobile());
                        intent.putExtra("Merchant_Name",item.getMerchant_Name());
                        intent.putExtra("Address",item.getAddress());
                        intent.putExtra("ImgUrl",item.getUrl()+item.getFace_photo());
                        intent.putExtra("Add_time",item.getAdd_time().substring(0,10));
                        intent.putExtra("ID",item.getId());
                        intent.putExtra("status",item.getStatus());
                        intent.putExtra("url",item.getUrl());
                        intent.putExtra("face_photo",item.getFace_photo());
                        intent.putExtra("role",role);
                        intent.putExtra("staff_info_id",staff_info_id);
                        context.startActivity(intent);
                    }else if (item.getStatus()==1||item.getStatus()==2){
                        intent = new Intent(context, BusinessInfoActivity.class);
                        intent.putExtra("Mobile",item.getMobile());
                        intent.putExtra("Merchant_Name",item.getMerchant_Name());
                        intent.putExtra("Address",item.getAddress());
                        intent.putExtra("ImgUrl",item.getUrl()+item.getFace_photo());
                        intent.putExtra("Add_time",item.getAdd_time().substring(0,10));
                        intent.putExtra("ID",item.getId());
                        intent.putExtra("status",item.getStatus());
                        intent.putExtra("url",item.getUrl());
                        intent.putExtra("face_photo",item.getFace_photo());
                        intent.putExtra("role",role);
                        intent.putExtra("staff_info_id",staff_info_id);
                        context.startActivity(intent);
                    }
                }
            });
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
    private class ViewHolder{
        private TextView tv_Merchant_Name,tv_address,tv_add_time;
        private ImageButton imageButton_map,imageButton_call;
        private ImageView iv_icon;
    }
}
