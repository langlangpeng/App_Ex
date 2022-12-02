package com.team.jixiao.Adapter;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.team.jixiao.Entity.BillBrowse;
import com.team.jixiao.Entity.OrderData;
import com.team.jixiao.R;


import java.util.List;

/**
 * 店铺适配器
 *
 * @author llw
 */
public class StoreAdapter extends BaseQuickAdapter<OrderData, BaseViewHolder> {

    private RecyclerView rvGood;

    public StoreAdapter(int layoutResId, @Nullable List<OrderData> data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, OrderData item) {
        rvGood = helper.getView(R.id.rv_goods);
        helper.setText(R.id.tv_store_name,item.getItem_name());

//        final GoodAdapter goodAdapter = new GoodAdapter(R.layout.item_good,item);
//        rvGood.setLayoutManager(new LinearLayoutManager(mContext));
//        rvGood.setAdapter(goodAdapter);
    }
}

