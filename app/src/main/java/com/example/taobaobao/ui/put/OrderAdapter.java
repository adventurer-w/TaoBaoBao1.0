package com.example.taobaobao.ui.put;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaobao.MainActivity;
import com.example.taobaobao.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.PendingIntent.getActivity;


public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Map<String, Object>> list;
    public Context context;
    public Activity activity;
    public final int ITEM_VIEW = 1;
    public final int DATE_VIEW = 2;

    public OrderAdapter(Context context, List<Map<String, Object>> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;

    }


    @Override
    public int getItemViewType(int position) {
        if (Integer.valueOf(list.get(position).get("type").toString()) == ITEM_VIEW) {
            return ITEM_VIEW;
        } else

            Log.d("1233", "1");
        return DATE_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == DATE_VIEW) {
            Log.d("1233", "2");
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false);
            return new OrderAdapter.DateViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
            return new OrderAdapter.NewsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OrderAdapter.DateViewHolder) {
            OrderAdapter.DateViewHolder viewHolder = (OrderAdapter.DateViewHolder) holder;
            Log.d("1233", "3");

        } else {
            OrderAdapter.NewsViewHolder viewHolder = (OrderAdapter.NewsViewHolder) holder;
            int good_id = (int) list.get(position).get("good_id");


            new Thread(() -> {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url("http://49.232.214.94/api/goods/" + good_id)
                            .method("GET", null)
                            .addHeader("Accept", "application/json")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("1233", responseData);

                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("good");
                    int quantity = jsonObject2.getInt("quantity");
                    long price = jsonObject2.getLong("price");
                    String my_name = jsonObject2.getString("name");
                    String img = jsonObject2.getString("img");

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.tv_num1.setText("购买数量：" + list.get(position).get("goods_count").toString());
                            viewHolder.tv_num2.setText("剩余数量：" + quantity);
                            viewHolder.tv_name.setText(my_name);
                            viewHolder.tv_id.setText(price+"元");
                            if (img.length() != 0) {
                                Log.d("12333", img);
                                if (img.charAt(0) == 'h' && img.charAt(1) == 't') {
                                    Glide.with(context).load(img).into(viewHolder.imageView);
                                } else {
                                    Glide.with(context).load("http://49.232.214.94/api/img/" + img).into(viewHolder.imageView);
                                }
                            }
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }).start();





        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_num1;
        private TextView tv_num2;
        private TextView tv_id;
        private TextView tv_name;
        private ImageView imageView;


        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.textView);
            tv_id = itemView.findViewById(R.id.textView2);
            tv_num1 = itemView.findViewById(R.id.textView8);
            imageView = itemView.findViewById(R.id.imageView);
            tv_num2 = itemView.findViewById(R.id.textView6);

        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        DateViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView5);
        }
    }

}
