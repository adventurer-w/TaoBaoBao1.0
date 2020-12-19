package com.example.taobaobao.ui.buy;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.taobaobao.MainActivity;
import com.example.taobaobao.MyData;
import com.example.taobaobao.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BuyFragment extends Fragment {

    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private static int i = 0;
    private List<Map<String, Object>> list = new ArrayList<>();
    private int flag = 0;
    private String responseData = "";
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);
        context = getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);  //避免滑动卡顿
        list.clear();
        wzy();

    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        i = 0;
        new Thread(() -> {
            try {
                Log.d("12333","here");
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .callTimeout(1_000, TimeUnit.MILLISECONDS)
                        .connectTimeout(1_000, TimeUnit.MILLISECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url("http://49.232.214.94/api/goods")
                        .method("GET", null)
                        .addHeader("Accept", "application/json")
                        .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                        .build();
                Response response = client.newCall(request).execute();
                responseData = response.body().string();
                getfeedback(responseData);
            } catch (IOException e) {
                    list.clear();
                    responseData="";
                    flag = 1;
                    Map map2 = new HashMap();
                    map2.put("type", 2);
                    list.add(map2);
                    Log.d("1233", String.valueOf(map2));
                    getActivity().runOnUiThread(() -> {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new BuyAdapter(getActivity(), list));
                    });

                e.printStackTrace();
            }
        }).start();

    }


    public void getfeedback(String responseData) {
        if (responseData != "") {
            try {
                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getInt("code") == 200) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = jsonObject1.getJSONArray("goods");
                    for (int j = 0; i < jsonArray.length() && j < 8; i++, j++) {
                        Log.d("1233i", "1:" + i);
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        int good_id = jsonObject2.getInt("good_id");
                        int user_id = jsonObject2.getInt("user_id");
                        int quantity = jsonObject2.getInt("quantity");
                        long price = jsonObject2.getLong("price");
                        String name = jsonObject2.getString("name");
                        String info = jsonObject2.getString("info");
                        String img = jsonObject2.getString("img");

                        Map map = new HashMap();

                        map.put("good_id", good_id);
                        map.put("user_id", user_id);
                        map.put("quantity", quantity);
                        map.put("price", price);
                        map.put("info", info);
                        map.put("name", name);
                        map.put("img", img);
                        map.put("type", 1);
                        list.add(map);

                    }
                    if (i == jsonArray.length()) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "到底了~", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                        recyclerView.setAdapter(new BuyAdapter(getActivity(), list));
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void wzy() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                onResume();
                refreshlayout.finishRefresh(1000/*,false*/);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {

                getfeedback(responseData);
                refreshlayout.finishLoadMore(200/*,false*/);//传入false表示加载失败
            }
        });

    }
    //getApplicationContext().getPackageName()
//小心修改application定义中通过android:process的特殊情况,一般直接写字符串就够用了

}
