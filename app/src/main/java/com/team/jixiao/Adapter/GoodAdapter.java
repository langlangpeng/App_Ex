package com.team.jixiao.Adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.team.jixiao.Entity.BillBrowse;
import com.team.jixiao.Entity.OrderData;
import com.team.jixiao.R;

import java.util.List;

/**
 * 商品适配器
 *
 * @author llw
 */
public class GoodAdapter extends BaseQuickAdapter<OrderData.OrderDataBean, BaseViewHolder> {

    public GoodAdapter(int layoutResId, @Nullable List<OrderData.OrderDataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderData.OrderDataBean item) {
//        helper.setText(R.id.tv_good_name, item.getProductName())
//                .setText(R.id.tv_good_color, item.getColor())
//                .setText(R.id.tv_good_size, item.getSize())
//                .setText(R.id.tv_goods_price, item.getPrice() + "")
//                .setText(R.id.tv_goods_num, item.getCount() + "");
        ImageView goodImg = helper.getView(R.id.Iv_urlimg);
        Glide.with(mContext).load(R.drawable.ic_baseline_wallpaper_100).into(goodImg);

        ImageView checkedGoods = helper.getView(R.id.check);
        //判断商品是否选中
        if (item.isChecked()) {
            checkedGoods.setImageDrawable(mContext.getDrawable(R.drawable.ic_checked));
        } else {
            checkedGoods.setImageDrawable(mContext.getDrawable(R.drawable.ic_check));
        }
//        //添加点击事件
//        helper.addOnClickListener(R.id.iv_checked_goods)//选中商品
//                .addOnClickListener(R.id.tv_increase_goods_num)//增加商品
//                .addOnClickListener(R.id.tv_reduce_goods_num);//减少商品
    }
}
