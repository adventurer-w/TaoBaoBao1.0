package com.example.taobaobao.ui.put;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.taobaobao.MyData;
import com.example.taobaobao.R;
import com.example.taobaobao.ui.buy.BuyAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PutFragment extends Fragment {

    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private List<Map<String, Object>> list = new ArrayList<>();
    private ImageView iv_add;
    private ImageView iv_order;
    private String responseData = "";
    private static int i = 0;
    private Context context;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_put, container, false);
        context = getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);  //避免滑动卡顿
        iv_add = view.findViewById(R.id.imageView6);
        iv_order = view.findViewById(R.id.imageView9);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        wzy();
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyData myData = new MyData(context);
                if (myData.load_check()) {
                    NavController controller = Navigation.findNavController(v);
                    controller.navigate(R.id.action_putFragment_to_addFragment);
                }
                else {
                    Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyData myData = new MyData(context);
                if (myData.load_check()) {
                    NavController controller = Navigation.findNavController(v);
                    controller.navigate(R.id.action_putFragment_to_orderFragment);
                }
                else {
                    Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        i=0;
        MyData myData = new MyData(context);
        if (!myData.load_check() || myData.load_token() == "") {
            Map map2 = new HashMap();
            map2.put("type", 2);
            list.add(map2);
            Log.d("1233", String.valueOf(map2));
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new PutAdapter(getActivity(), list));
        } else {
            new Thread(() -> {
                try {
                    Log.d("1233","熟悉熟悉熟悉");
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .callTimeout(1_000, TimeUnit.MILLISECONDS)
                            .connectTimeout(1_000, TimeUnit.MILLISECONDS)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://49.232.214.94/api/goods")
                            .method("GET", null)
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", myData.load_token())
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    getfeedback(responseData);
                } catch (IOException e) {
                    if (responseData == "") {
                        Map map2 = new HashMap();
                        map2.put("type", 2);
                        list.add(map2);
                        Log.d("1233", String.valueOf(map2));
                        getActivity().runOnUiThread(() -> {
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new PutAdapter(getActivity(), list));

                        });
                    }
                    e.printStackTrace();
                }
            }).start();
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

    public void getfeedback(String responseData) {

        if (responseData != "") {
            try {
                Log.d("1233","here1");
                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getInt("code") == 200) {
                    Log.d("1233","here2");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = jsonObject1.getJSONArray("goods");
                    if (jsonArray.length() != 0) {
                        Log.d("1233","here3");
                        for (int j = 0; i < jsonArray.length() && j < 8; i++, j++) {
                            Log.d("1233","here4"+jsonArray);
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
                            Log.d("1233", String.valueOf(map));
                        }
                        if(i==jsonArray.length()){
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "全部加载完毕", Toast.LENGTH_SHORT).show();
                                }
                            });
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
                        recyclerView.setAdapter(new PutAdapter(getActivity(), list));
                    }
                });

            } catch (JSONException e) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new PutAdapter(getActivity(), list));
                    }
                });
                e.printStackTrace();
            }
        }
    }
    public void getfeedback2(String responseData) {

        if (responseData != "") {
            try {
                Log.d("1233","here1");
                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getInt("code") == 200) {
                    Log.d("1233","here2");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = jsonObject1.getJSONArray("goods");
                    if (jsonArray.length() != 0) {
                        Log.d("1233","here3");
                        for (int j = 0; i < jsonArray.length() && j < 8; i++, j++) {
                            Log.d("1233","here4"+jsonArray);
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
                            Log.d("1233", String.valueOf(map));
                        }
                        if(i==jsonArray.length()){
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "到底了~", Toast.LENGTH_SHORT).show();
                                }
                            });
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
                        recyclerView.setAdapter(new PutAdapter(getActivity(), list));
                    }
                });

            } catch (JSONException e) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new PutAdapter(getActivity(), list));
                    }
                });
                e.printStackTrace();
            }
        }
    }
}