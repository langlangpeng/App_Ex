package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.team.jixiao.Adapter.BillBrowseAdapter;
import com.team.jixiao.Adapter.DetailMerChartAdapter;
import com.team.jixiao.Adapter.StoreAdapter;
import com.team.jixiao.Entity.BillBrowse;
import com.team.jixiao.Entity.OrderData;
import com.team.jixiao.Entity.StuffInfo;
import com.team.jixiao.Fragment.MyFragment2;
import com.team.jixiao.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BillBrowseActivity extends AppCompatActivity implements BillBrowseAdapter.DetailViewHolderListener {
    @BindView(R.id.lv_bill_browse)
    ListView lv_bill_browse;
    @BindView(R.id.all_check)
    CheckBox all_check;
    @BindView(R.id.tv_items)
    TextView tv_items;
    @BindView(R.id.tv_crash)
    TextView tv_crash;
    @BindView(R.id.btn_order)
    Button btn_order;
    @BindView(R.id.tv_billhistory)
    TextView tv_billhistory;
    @BindView(R.id.tv_return1)
    TextView tv_return1;

    private int role = -1;
    private int staff_info_id = 0;

    private boolean all_check_state = false;
    private BillBrowseAdapter billBrowseAdapter;

    private Handler mainHandler;
    List<BillBrowse> list = new ArrayList<>();
    Gson gson = new Gson();
    JSONArray array;
    private boolean flag = false;

    private String bdid = "";

    String item_no = "";
    String bd_num = "";
    String bd_price = "";
    String gg = "";
    String strDATAJSON = "";
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_browse);
        ButterKnife.bind(this);

        new BillBrowseThread().start();//启动线程
        mainHandler = new Handler(getMainLooper());
        bdid = getIntent().getStringExtra("bdid");
        if (bdid==null){
            bdid="";
        }
        if (!bdid.isEmpty()){
            tv_billhistory.setVisibility(View.VISIBLE);
        }
        Log.e("bdid", bdid);
        billBrowseAdapter = new BillBrowseAdapter(this,list,this);
        lv_bill_browse.setAdapter(billBrowseAdapter);

        role = getIntent().getIntExtra("role", -1);
        staff_info_id = getIntent().getIntExtra("staff_info_id", 0);
        Log.e("BillBrowseActivity_role:", String.valueOf(role));
        Log.e("BillBrowseActivity_info_id:", String.valueOf(staff_info_id));
        mainHandler = new MainHandler();
        getRequest();

    }

    @OnClick({R.id.all_check,R.id.btn_order,R.id.tv_billhistory,R.id.tv_return1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.all_check:
                Toast.makeText(this, "已点击", Toast.LENGTH_SHORT).show();
                if(!flag){
                    flag=true;
                    for (int i=0;i<list.size();i++){
                        list.get(i).setCheck(true);
                    }
                    billBrowseAdapter.notifyDataSetChanged();
                }else{
                    flag=false;
                    all_check.setText("全选");
                    for (int i=0;i<list.size();i++){
                        list.get(i).setCheck(false);
                    }
                    billBrowseAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_order:
                    if (bdid.isEmpty()){
                        Toast.makeText(this, "展示界面仅供参考", Toast.LENGTH_SHORT).show();
                    }else {
                        strDATAJSON = "[";
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).isCheck()){
                                item_no = list.get(i).getItem_no();
                                bd_num = String.valueOf(list.get(i).getGoods_num());
                                bd_price = list.get(i).getPrice_choice();
                                gg = list.get(i).getGg_choice();
                                    strDATAJSON += "{item_no:'"+list.get(i).getItem_no()+"',bd_num:'"+String.valueOf(list.get(i).getGoods_num())+"',bd_price:'"+list.get(i).getPrice_choice()+"',gg:'"+list.get(i).getGg_choice()+"'},";
                                }
                            }
                        strDATAJSON = strDATAJSON.substring(0,strDATAJSON.length()-1);
                        strDATAJSON = strDATAJSON + "]";
                        Log.e("TAG", strDATAJSON);
                        getOrder();
                        Toast.makeText(this, "报单已添加", Toast.LENGTH_SHORT).show();
                    }
                break;
            case R.id.tv_billhistory:
                intent = new Intent(BillBrowseActivity.this,BillHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_return1:
                intent = new Intent(BillBrowseActivity.this,BillActivity.class);
                intent.putExtra("staff_info_id",staff_info_id);
                intent.putExtra("role",role);
                startActivity(intent);
                finish();
        }
    }
    /*
     http://47.92.214.113:8092/webapi/bd/Valet_sheet_add.ashx? strDATAJSON=[{item_no:"",bd_num:"",bd_price:"",gg:""},{}]  &bdid=商户主报单id
     */
    //.add("strDATAJSON", "[{item_no:'"+item_no+"',bd_num:'"+item_no+"',bd_price:'"+bd_price+"',gg:'"+gg+"'}]")
    private void getOrder() {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("strDATAJSON", strDATAJSON)
                .add("bdid", bdid)
                .build();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE+"/webapi/bd/Valet_sheet_add.ashx")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("TAG", "onFailure: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();//转字符串
                Log.e("res", res);
            }
        });
    }
    private void getRequest() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE+"/webapi/bd/base_browse.ashx")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("TAG", "onFailure: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();//转字符串
                Message msg = new Message();
                Log.d("BillBrowseActivity_msg", "onResponse: "+res);
                msg.what = 1;
                msg.obj = res;
                mainHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void setData(final BillBrowseAdapter.ViewHolder viewHolder, int position) {

        final BillBrowse bean = list.get(position);

        viewHolder.checkbox.setChecked(bean.isCheck());

        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.checkbox.isChecked()) {
                    bean.setCheck(true);
                } else {
                    bean.setCheck(false);
                }
            }
        });
    }


    class MainHandler extends Handler {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 1:
                    if (msg.obj != null) {
                        String vlResult = (String) msg.obj;

                        Log.e("BillBrowseActivity_INFO_String", vlResult);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(vlResult);
                            array = jsonObject.optJSONArray("data");
                            Log.e("array", String.valueOf(array));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list = gson.fromJson(String.valueOf(array), new TypeToken<List<BillBrowse>>(){}.getType());
//                        for (BillBrowse data : list){
////                            Log.e("TAG", data.getItem_name());
//                            Log.e("TAG", data.getGg());
//                            String gg = data.getGg();
//                            Log.e("gg", gg);
//                            if (gg.contains("|")){
//                                ggs = gg.split("[|]");
//                                Log.i("ggs", String.valueOf(ggs));
//                                for (String s:ggs){
//                                    Log.i(TAG, s);
//                                }
//                            }
//
//                        }

                        billBrowseAdapter.setData(list);
                    }
                    break;
            }
        }
    }

    public class BillBrowseThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(100);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    double Items = 0.0;
                    double crash = 0.0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isCheck()){
                            Items += list.get(i).getGoods_num();
                            crash += list.get(i).getSum();
                        }
                    }
                    tv_crash.setText(String.valueOf(crash));
                    tv_items.setText(String.valueOf(Items));
                    break;
            }
            return false;
        }
    });
}