package com.team.jixiao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.jixiao.Adapter.DetailMerChartAdapter;
import com.team.jixiao.Adapter.ValetBillAdapter;
import com.team.jixiao.Entity.Detail_Merchant;
import com.team.jixiao.Entity.ValetBill;
import com.team.jixiao.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ValetBillActivity extends AppCompatActivity {
    int role = -1;
    int staff_info_id = 0;

    Gson gson = new Gson();

    JSONArray array;
    private List<ValetBill> list = new ArrayList<>();
    private Handler mainHandler;

    ValetBillAdapter valetBillAdapter;
    @BindView(R.id.lv_valet)
    ListView lv_valet;
    @BindView(R.id.ed_search)
    EditText ed_search;

    String ct_name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valet_bill);
        ButterKnife.bind(this);
        mainHandler = new Handler(getMainLooper());
        role = getIntent().getIntExtra("role", -1);
        staff_info_id = getIntent().getIntExtra("staff_info_id", 0);
        Log.e("ValetBillActivity_role:", String.valueOf(role));
        Log.e("ValetBillActivity_info_id:", String.valueOf(staff_info_id));

        valetBillAdapter = new ValetBillAdapter(this,list);
        lv_valet.setAdapter(valetBillAdapter);

        getRequest();
        mainHandler = new MainHandler();

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ct_name = ed_search.getText().toString().trim();
                getRequest();
            }
        });
    }

    private void getRequest() {

        OkHttpClient client = new OkHttpClient();

        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(staff_info_id))
                .add("ct_name",ct_name)
                .build();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE+"/webapi/bd/query_Merchant.ashx")
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
                Message msg = new Message();
                Log.d("ValetBillActivity_msg", "onResponse: "+res);
                msg.what = 1;
                msg.obj = res;
                mainHandler.sendMessage(msg);
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
                        Log.e("INFO_String", vlResult);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(vlResult);
                            array = jsonObject.optJSONArray("data");
                            Log.e("array", String.valueOf(array));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list = gson.fromJson(String.valueOf(array), new TypeToken<List<ValetBill>>(){}.getType());
                        valetBillAdapter.setData(list);
                    }
                    break;
            }
        }
    }
}