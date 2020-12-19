package com.example.taobaobao.ui.buy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaobao.MyData;
import com.example.taobaobao.R;

import java.util.List;
import java.util.Map;


public class BuyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Map<String, Object>> list;
    public Context context;
    public final int ITEM_VIEW = 1;
    public final int DATE_VIEW = 2;

    public BuyAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notfound, parent, false);
            return new DateViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_buybuybuy, parent, false);
            return new NewsViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DateViewHolder) {
            DateViewHolder viewHolder = (DateViewHolder) holder;
            Log.d("1233", "3");

        } else {

            NewsViewHolder viewHolder = (NewsViewHolder) holder;
            viewHolder.tv_price.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
            viewHolder.tv_num.setText("剩余 " + list.get(position).get("quantity").toString());
            viewHolder.tv_price.setText("￥" + list.get(position).get("price"));
            viewHolder.tv_name.setText(list.get(position).get("name").toString());

            String url_pic = list.get(position).get("img").toString();
            if (url_pic.length()!=0) {
                Log.d("12333",url_pic);
                if (url_pic.charAt(0) == 'h' && url_pic.charAt(1) == 't') {

                    Glide.with(context).load(list.get(position).get("img").toString()).into(viewHolder.iv_pic);
                } else {
                    Glide.with(context).load("http://49.232.214.94/api/img/" + url_pic).into(viewHolder.iv_pic);
                }
            }
            viewHolder.go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    if(list.get(position).get("good_id").toString().length()!=0) {
                        bundle.putInt("good_id", (Integer) list.get(position).get("good_id"));
                        NavController controller = Navigation.findNavController(v);
                        controller.navigate(R.id.action_buyFragment_to_take_it_all_Fragment, bundle);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_num;
        private TextView tv_price;
        private TextView tv_name;
        private ImageView iv_pic;
        private ConstraintLayout go;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.textView3);
            tv_price = itemView.findViewById(R.id.textView4);
            tv_num = itemView.findViewById(R.id.textView5);
            iv_pic = itemView.findViewById(R.id.imageView3);
            go = itemView.findViewById(R.id.linearLayout);

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
