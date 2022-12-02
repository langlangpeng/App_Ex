package com.team.jixiao.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team.jixiao.Entity.BillBrowse;
import com.team.jixiao.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BillBrowseAdapter extends BaseAdapter{
    private Context context;
    private List<BillBrowse> list;
    private LayoutInflater mInflater;
    private DetailViewHolderListener mListener;
    double defaultnum = 1.0;
    public BillBrowseAdapter(Context context, List<BillBrowse> list,DetailViewHolderListener mListener) {
        this.context = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
        this.mListener =  mListener;
        notifyDataSetChanged();
    }

    public void setData(List<BillBrowse> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public BillBrowse getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.lv_bill_browse,null);
            viewHolder = new ViewHolder();

            viewHolder.tv_money = convertView.findViewById(R.id.tv_money);
//            viewHolder.tv_unit = convertView.findViewById(R.id.tv_unit);
            viewHolder.tv_sum = convertView.findViewById(R.id.tv_sum);

            viewHolder.tv_1_5 = convertView.findViewById(R.id.tv_1_5);
            viewHolder.tv_6_10 = convertView.findViewById(R.id.tv_6_10);
            viewHolder.tv_11m = convertView.findViewById(R.id.tv_11m);

//            viewHolder.tv_reduce_goods_num = convertView.findViewById(R.id.tv_reduce_goods_num);
            viewHolder.ed_goods_num = convertView.findViewById(R.id.ed_goods_num);
//            viewHolder.tv_increase_goods_num = convertView.findViewById(R.id.tv_increase_goods_num);

            viewHolder.tv_goodname = convertView.findViewById(R.id.tv_goodname);

            viewHolder.Iv_urlimg = convertView.findViewById(R.id.Iv_urlimg);
            viewHolder.checkbox = convertView.findViewById(R.id.check);
            viewHolder.spinner = convertView.findViewById(R.id.spinner);

            List<String> spinnerlist = new ArrayList<>();
            ArrayAdapter<String> arrayAdapter;

            convertView.setTag(viewHolder);

            BillBrowse item = list.get(position);

            ViewHolder finalViewHolder = viewHolder;
            if (item.getGg().contains("|")){
                spinnerlist = Arrays.asList(item.getGg().split("[|]"));
            }else{
                spinnerlist.add(item.getGg());
            }
            arrayAdapter = new ArrayAdapter<>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinnerlist);
            viewHolder.spinner.setAdapter(arrayAdapter);
            List<String> finalSpinnerlist = spinnerlist;
            viewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    item.setGg_choice(finalSpinnerlist.get(position));
//                    Toast.makeText(context, finalSpinnerlist.get(position), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    item.setGg_choice(finalSpinnerlist.get(position));
                }
            });
            viewHolder.checkbox.setChecked(item.isCheck());

            viewHolder.tv_money.setText(item.getPl_price1());
//            viewHolder.tv_unit.setText(item.getUnit());

            viewHolder.tv_sum.setText(item.getPl_price1());
            viewHolder.tv_goodname.setText(item.getItem_name());
            viewHolder.tv_1_5.setText(item.getPl_price1());
            viewHolder.tv_6_10.setText(item.getPl_price2());
            viewHolder.tv_11m.setText(item.getPl_price3());

            item.setPrice_choice(item.getPl_price1());
            viewHolder.tv_1_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setPrice_choice(item.getPl_price1());
                    finalViewHolder.tv_money.setText(item.getPl_price1());
                    double m = Double.valueOf(item.getPl_price1());
                    double num = Double.parseDouble(finalViewHolder.ed_goods_num.getText().toString());
                    double m2 = num*m;
                    item.setSum(m2);
                    item.setGoods_num(num);
                    finalViewHolder.tv_sum.setText(String.valueOf(m2));
                }
            });
            viewHolder.tv_6_10.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setPrice_choice(item.getPl_price2());
                    finalViewHolder.tv_money.setText(item.getPl_price2());
                    double m = Double.valueOf(item.getPl_price2());
                    double num = Double.parseDouble(finalViewHolder.ed_goods_num.getText().toString());
                    double m2 = num*m;
                    item.setSum(m2);
                    item.setGoods_num(num);
                    finalViewHolder.tv_sum.setText(String.valueOf(m2));
                }
            });
            viewHolder.tv_11m.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setPrice_choice(item.getPl_price3());
                    finalViewHolder.tv_money.setText(item.getPl_price3());
                    double m = Double.valueOf(item.getPl_price3());
                    double num = Double.parseDouble(finalViewHolder.ed_goods_num.getText().toString());
                    double m2 = num*m;
                    item.setSum(m2);
                    item.setGoods_num(num);
                    finalViewHolder.tv_sum.setText(String.valueOf(m2));
                }
            });

            viewHolder.ed_goods_num.setText(String.valueOf(defaultnum));
            ViewHolder finalViewHolder1 = viewHolder;
            viewHolder.ed_goods_num.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String sum = finalViewHolder1.ed_goods_num.getText().toString();
                    if (sum.isEmpty()||sum.substring(0,1)=="."){
                        sum = "0.0";
                    }
                    double num = Double.parseDouble(sum);
                    item.setGoods_num(num);
                }
            });
//            ViewHolder finalViewHolder3 = viewHolder;
//            viewHolder.tv_increase_goods_num.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int num = Integer.parseInt(finalViewHolder3.tv_goods_num.getText().toString());
//                    num++;
//                    finalViewHolder3.tv_goods_num.setText(String.valueOf(num));
//                    double m = Double.parseDouble(finalViewHolder3.tv_money.getText().toString());
//                    double m2 = m*num;
//                    item.setSum(m2);
//                    item.setGoods_num(num);
//                    finalViewHolder3.tv_sum.setText(String.valueOf(m2));
//                }
//            });
//            viewHolder.tv_reduce_goods_num.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int num = Integer.parseInt(finalViewHolder3.tv_goods_num.getText().toString());
//                    num--;
//                    if (num<=0){
//                        num=0;
//                    }
//                    finalViewHolder3.tv_goods_num.setText(String.valueOf(num));
//                    double m = Double.parseDouble(finalViewHolder3.tv_money.getText().toString());
//                    double m2 = m*num;
//                    item.setSum(m2);
//                    item.setGoods_num(num);
//                    finalViewHolder3.tv_sum.setText(String.valueOf(m2));
//                }
//            });
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_baseline_wallpaper_100)
                    .error(R.drawable.no_drawable)
                    .fallback(R.drawable.ic_baseline_wallpaper_100)
                    .override(100,100); //override指定加载图片大小
            Glide.with(context)
                    .load(item.getPicture_url())
                    .apply(requestOptions)
                    .into(viewHolder.Iv_urlimg);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        mListener.setData(viewHolder, position);
        return convertView;
    }


    public class ViewHolder{
        public TextView tv_money,tv_unit,tv_sum,tv_goodname;
        public TextView tv_1_5,tv_6_10,tv_11m;
        public EditText ed_goods_num;
        public ImageView Iv_urlimg;
        public CheckBox checkbox;
        public Spinner spinner;
    }
    /**
     * 展示不同数据的接口
     */
    public interface DetailViewHolderListener {
        void setData(ViewHolder viewHolder, int position);
    }
}
