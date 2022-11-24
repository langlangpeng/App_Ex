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

import com.team.jixiao.Entity.Detail_Merchant;
import com.team.jixiao.MapActivity;
import com.team.jixiao.R;

import java.util.List;

public class DetailMerChartAdapter extends BaseAdapter {
    private Context context;
    private List<Detail_Merchant> list;

    public DetailMerChartAdapter(Context context, List<Detail_Merchant> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }
    public void setData( List<Detail_Merchant> list){
        this.list = list;
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
            viewHolder.tv_add_time.setText(item.getAdd_time());

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
