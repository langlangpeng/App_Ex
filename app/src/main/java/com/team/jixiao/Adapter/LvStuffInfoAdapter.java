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
import android.widget.TextView;

import com.team.jixiao.EditInfoActivity;
import com.team.jixiao.Entity.StuffInfo;
import com.team.jixiao.R;

import java.util.List;

public class LvStuffInfoAdapter extends BaseAdapter {
    private Context context;
    private List<StuffInfo> list;

    public LvStuffInfoAdapter(Context context, List<StuffInfo> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }

    public void setData(List<StuffInfo> custom_dataList) {
        this.list = custom_dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public StuffInfo getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.stuffinfo_layout,null);
            viewHolder = new ViewHolder();

            viewHolder.tv_username = convertView.findViewById(R.id.tv_username);
            viewHolder.tv_role = convertView.findViewById(R.id.tv_role);

            viewHolder.imagebtn_phone = convertView.findViewById(R.id.imagebtn_phone);
            viewHolder.imagebtn_edit = convertView.findViewById(R.id.imagebtn_edit);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Log.e("getView: ", String.valueOf(position));

        StuffInfo item = list.get(position);

        viewHolder.tv_username.setText(item.getNickname());
        viewHolder.tv_role.setText(item.getRole());

        viewHolder.imagebtn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+item.getMobile()));
                context.startActivity(intent);
            }
        });
        viewHolder.imagebtn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditInfoActivity.class);
                intent.putExtra("nickname",item.getNickname());
                intent.putExtra("id",item.getId());
                intent.putExtra("Mobile",item.getMobile());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
    private class ViewHolder{
        private TextView tv_username,tv_role;
        private ImageButton imagebtn_phone,imagebtn_edit;
    }
}
