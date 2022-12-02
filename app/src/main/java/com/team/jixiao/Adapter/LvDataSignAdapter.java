package com.team.jixiao.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import com.team.jixiao.Entity.DataSign;
import com.team.jixiao.R;


import java.util.List;

public class LvDataSignAdapter extends BaseAdapter {
    private Context context;
    private List<DataSign> list;

    public LvDataSignAdapter(Context context, List<DataSign> list) {
        this.context = context;
        this.list = list;
        for (DataSign d : list){
            Log.e("LvDataSignAdapter", d.getNickname() );
        }
        notifyDataSetChanged();
    }
    public void setData(List<DataSign> list) {

        this.list = list;
        for (DataSign d : list){
            Log.e("setData", d.getNickname() );
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        Log.e("getCount", String.valueOf(list.size()));
        return list.size();
    }

    @Override
    public DataSign getItem(int position) {
        Log.e("getItem: ", String.valueOf(position));
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.e("getItemId: ", String.valueOf(position));
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_historyinfo,null);
            viewHolder = new ViewHolder();

            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHolder.tv_online = convertView.findViewById(R.id.tv_online);
            viewHolder.tv_offline = convertView.findViewById(R.id.tv_offline);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DataSign item = list.get(position);
        Log.e("DataSign", "getView: "+position);
        viewHolder.tv_name.setText(item.getNickname());
        return convertView;
    }
    private class ViewHolder{
        private TextView tv_name,tv_online,tv_offline;
    }
}
