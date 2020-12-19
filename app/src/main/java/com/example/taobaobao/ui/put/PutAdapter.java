package com.example.taobaobao.ui.put;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaobao.R;

import java.util.List;
import java.util.Map;


public class PutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Map<String, Object>> list;
    public Context context;
    public final int ITEM_VIEW = 1;
    public final int DATE_VIEW = 2;

    public PutAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (Integer.valueOf(list.get(position).get("type").toString()) == ITEM_VIEW) {
            return ITEM_VIEW;
        } else
            Log.d("1233","1");
        return DATE_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == DATE_VIEW) {
            Log.d("1233","2");
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nonono, parent, false);
            return new com.example.taobaobao.ui.put.PutAdapter.DateViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_buybuybuy2, parent, false);
            return new com.example.taobaobao.ui.put.PutAdapter.NewsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof com.example.taobaobao.ui.put.PutAdapter.DateViewHolder) {
            com.example.taobaobao.ui.put.PutAdapter.DateViewHolder viewHolder = (com.example.taobaobao.ui.put.PutAdapter.DateViewHolder) holder;
            Log.d("1233","3");

        } else {
            com.example.taobaobao.ui.put.PutAdapter.NewsViewHolder viewHolder = (com.example.taobaobao.ui.put.PutAdapter.NewsViewHolder) holder;
            viewHolder.tv_num.setText("库存: "+list.get(position).get("quantity").toString());
            viewHolder.tv_price.setText("￥ "+list.get(position).get("price").toString());
            viewHolder.tv_name.setText(list.get(position).get("name").toString());
            String url_pic = list.get(position).get("img").toString();
            if (url_pic.length()!=0) {
                Log.d("12333",url_pic);
                if (url_pic.charAt(0) == 'h' && url_pic.charAt(1) == 't') {
                    Glide.with(context).load(list.get(position).get("img").toString()).into(viewHolder.imageView);
                } else {
                    Glide.with(context).load("http://49.232.214.94/api/img/" + url_pic).into(viewHolder.imageView);
                }
            }
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", (Integer) list.get(position).get("good_id"));
                    NavController controller = Navigation.findNavController(v);
                    controller.navigate(R.id.action_putFragment_to_changeFragment,bundle);
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
        private LinearLayout linearLayout;
        private ImageView imageView;


        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.textView);
            tv_price = itemView.findViewById(R.id.textView2);
            tv_num = itemView.findViewById(R.id.textView6);
            imageView = itemView.findViewById(R.id.imageView);
           linearLayout = itemView.findViewById(R.id.layout233);

        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        DateViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView =itemView.findViewById(R.id.imageView5);
        }
    }
}