package com.example.taobaobao.ui.put;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.taobaobao.MyData;
import com.example.taobaobao.R;
import com.example.taobaobao.ui.buy.BuyAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderFragment extends Fragment {

    private String responseData;
    private List<Map<String, Object>> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        refreshLayout = (RefreshLayout)view.findViewById(R.id.refreshLayout);
        wzy();
    }

    @Override
    public void onResume() {
        super.onResume();

        MyData myData = new MyData(getContext());

        Log.d("1233i", myData.load_token());
        new Thread(() -> {
            try {
                Log.d("1233i", "here000");
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .callTimeout(1_000, TimeUnit.MILLISECONDS)
                        .connectTimeout(1_000, TimeUnit.MILLISECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url("http://49.232.214.94/api/order")
                        .method("GET", null)
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", myData.load_token())
                        .build();
                Response response = client.newCall(request).execute();
                responseData = response.body().string();
                Log.d("1233i", responseData);
                getfeedback(responseData);
            } catch (IOException e) {
                Log.d("1233i", "here123");
                Map map2 = new HashMap();
                map2.put("type", 2);
                list.add(map2);
                e.printStackTrace();
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new OrderAdapter(getContext(), list, getActivity()));
                    }
                });
            }
        }).start();
    }

    public void wzy(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                list.clear();
                onResume();
                refreshlayout.finishRefresh(1000/*,false*/);

            }
        });

    }


    public void getfeedback(String responseData) {
        if (responseData != "") {
            try {
                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getInt("code") == 200) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = jsonObject1.getJSONArray("orders");
                    if (jsonArray.length() != 0) {
                        int i = 0;
                        for (i = 0; i < jsonArray.length(); i++) {
                            Log.d("1233i", "1:" + i);
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            int good_id = jsonObject2.getInt("good_id");
                            int goods_count = jsonObject2.getInt("goods_count");
                            int user_id = jsonObject2.getInt("user_id");
                            Map map = new HashMap();
                            map.put("good_id", good_id);
                            map.put("goods_count", goods_count);
                            map.put("user_id", user_id);
                            map.put("type", 1);
                            list.add(map);
                        }
                    } else {
                        Log.d("1233", "here");
                        Map map2 = new HashMap();
                        map2.put("type", 2);
                        list.add(map2);
                        Log.d("1233", String.valueOf(map2));
                    }


                }
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new OrderAdapter(getContext(), list, getActivity()));
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
