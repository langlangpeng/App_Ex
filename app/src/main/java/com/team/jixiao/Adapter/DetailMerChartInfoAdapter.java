package com.team.jixiao.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.team.jixiao.Entity.Detail_Merchart_Data;
import com.team.jixiao.MapActivity;
import com.team.jixiao.R;

import java.util.List;

public class DetailMerChartInfoAdapter extends BaseAdapter {
    List<Detail_Merchart_Data> list;
    Context context;

    public void setData(List<Detail_Merchart_Data> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public DetailMerChartInfoAdapter(List<Detail_Merchart_Data> list, Context context) {
        this.list = list;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Detail_Merchart_Data getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.detail_merchant_layout,null);
            viewHolder = new ViewHolder();

            viewHolder.tv_content = convertView.findViewById(R.id.tv_content);
            viewHolder.tv_time = convertView.findViewById(R.id.tv_time);

            convertView.setTag(viewHolder);

            Detail_Merchart_Data item = list.get(position);

            viewHolder.tv_content.setText(item.getContent());
            viewHolder.tv_time.setText(item.getAdd_time().substring(0,10));


        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private class ViewHolder{
        private TextView tv_content,tv_time;

    }
}
