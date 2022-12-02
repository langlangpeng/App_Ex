package com.team.jixiao.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team.jixiao.BillBrowseActivity;
import com.team.jixiao.Entity.BillBrowse;
import com.team.jixiao.Entity.ValetBill;
import com.team.jixiao.R;

import java.util.List;

public class ValetBillAdapter extends BaseAdapter {
    private Context context;
    private List<ValetBill> list;

    public ValetBillAdapter(Context context, List<ValetBill> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }
    public void setData(List<ValetBill> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ValetBill getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_valet,null);
            viewHolder = new ViewHolder();

            viewHolder.ct_name = convertView.findViewById(R.id.ct_name);
            viewHolder.ct_phone = convertView.findViewById(R.id.ct_phone);
            viewHolder.ct_address = convertView.findViewById(R.id.ct_address);
            viewHolder.createtime = convertView.findViewById(R.id.createtime);
            viewHolder.ct_face_photo = convertView.findViewById(R.id.ct_face_photo);

            convertView.setTag(viewHolder);

            ValetBill item = list.get(position);
            viewHolder.ct_name.setText(item.getCt_name());
            viewHolder.ct_phone.setText(item.getCt_phone());
            viewHolder.ct_address.setText(item.getCt_address());
            viewHolder.createtime.setText(item.getCreatetime().substring(0,10));
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_baseline_wallpaper_100)
                    .error(R.drawable.no_drawable)
                    .fallback(R.drawable.ic_baseline_wallpaper_100)
                    .override(100,100); //override指定加载图片大小
            Glide.with(context)
                    .load(item.getCt_face_photo())
                    .apply(requestOptions)
                    .into(viewHolder.ct_face_photo);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("bdid", String.valueOf(item.getId()));
                    Intent intent = new Intent(context, BillBrowseActivity.class);
                    intent.putExtra("bdid",String.valueOf(item.getId()));
                    context.startActivity(intent);
                }
            });
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
    public class ViewHolder{
        public TextView ct_name,ct_phone,ct_address,createtime;
        public ImageView ct_face_photo;
    }
}
