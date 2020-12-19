package com.example.taobaobao.ui.buy;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.taobaobao.MyData;
import com.example.taobaobao.R;
import com.example.taobaobao.ui.me.MeFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Take_it_all_Fragment extends Fragment {
    private Button button;
    private Dialog bottomDialog;
    private String responseData;
    private TextView tv_name;
    private TextView tv_price;
    private TextView tv_num;
    private ImageView iv_pic;
    private TextView tv_info;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_it_all, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_name = view.findViewById(R.id.textView13);
        tv_price = view.findViewById(R.id.textView12);
        tv_num = view.findViewById(R.id.textView10);
        iv_pic = view.findViewById(R.id.imageView7);
        tv_info = view.findViewById(R.id.ifon);
        button = view.findViewById(R.id.button2);


    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            Log.d("1233", String.valueOf(getArguments().getInt("good_id")));
            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("http://49.232.214.94/api/goods/" + getArguments().getInt("good_id"))
                        .method("GET", null)
                        .addHeader("Accept", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                responseData = response.body().string();
                Log.d("1233", responseData);
                getfeedback(responseData);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyData myData = new MyData(getContext());
                if (myData.load_goods() != -1) {
                    if (myData.load_goods() != 0) {
                        bottomDialog = new Dialog(getActivity(), R.style.Theme_Design_BottomSheetDialog);
                        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_number, null);
                        bottomDialog.setContentView(contentView);
                        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
                        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
                        contentView.setLayoutParams(layoutParams);
                        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);//弹窗位置
                        bottomDialog.getWindow().setWindowAnimations(R.style.Animation_Design_BottomSheetDialog);//弹窗样式
                        bottomDialog.show();
                        ImageButton ib_add = contentView.findViewById(R.id.imageButton2);
                        ImageButton ib_mil = contentView.findViewById(R.id.imageButton);
                        TextView tv_num = contentView.findViewById(R.id.textView9);
                        Button bt_buy = contentView.findViewById(R.id.button3);

                        tv_num.setText("1");
                        ib_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int i = Integer.parseInt(tv_num.getText().toString());
                                if (i < myData.load_goods()) {
                                    i++;
                                    tv_num.setText(String.valueOf(i));
                                } else {
                                    Toast.makeText(getActivity(), "不能再多啦~", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        ib_mil.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Integer.parseInt(tv_num.getText().toString()) > 1) {
                                    int i = Integer.parseInt(tv_num.getText().toString());
                                    i--;
                                    tv_num.setText(String.valueOf(i));
                                } else {
                                    Toast.makeText(getActivity(), "不能再少啦~", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        bt_buy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Gson gson = new Gson();
                                            Take_it_all_Fragment.buy use = new Take_it_all_Fragment.buy();
                                            use.good_id = myData.load_id();
                                            use.goods_count = Integer.valueOf((String) tv_num.getText()).intValue();

                                            OkHttpClient client = new OkHttpClient().newBuilder()
                                                    .build();
                                            MediaType mediaType = MediaType.parse("application/json");
                                            RequestBody body = RequestBody.create(mediaType,gson.toJson(use));
                                            Request request = new Request.Builder()
                                                    .url("http://49.232.214.94/api/order")
                                                    .method("POST", body)
                                                    .addHeader("Accept", "application/json")
                                                    .addHeader("Authorization", myData.load_token())
                                                    .build();
                                            Response response = client.newCall(request).execute();
                                            Log.d("1233",responseData);
                                            myData.save_goods(-1);
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "购买成功！", Toast.LENGTH_SHORT).show();
                                                    bottomDialog.dismiss();
                                                    NavController controller = Navigation.findNavController(getView());
                                                    controller.navigate(R.id.action_take_it_all_Fragment_to_buyFragment);
                                                }
                                            });
                                        } catch (IOException e) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "网络罢工了呢(ó﹏ò｡)", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });


                    } else {
                        Toast.makeText(getActivity(), "该商品暂时无货", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void getfeedback(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            int code = jsonObject.getInt("code");
            if (code != 200) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                Toast.makeText(getContext(),"该商品不存在！",Toast.LENGTH_SHORT).show();
                return;
                    }
                });
            } else {

                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("good");
                int good_id = jsonObject2.getInt("good_id");
                int user_id = jsonObject2.getInt("user_id");
                int quantity = jsonObject2.getInt("quantity");
                long price = jsonObject2.getLong("price");
                String name = jsonObject2.getString("name");
                String info = jsonObject2.getString("info");
                String img = jsonObject2.getString("img");
                MyData myData = new MyData(getContext());
                myData.save_goods(quantity);
                myData.save_id(good_id);

                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_name.setText("商品名：" + name);
                        tv_info.setText(info);
                        tv_num.setText("剩余：" + quantity);
                        tv_price.setText("价格：" + price);
                        Log.d("1233", img);
                        if (img.length()!=0) {
                            Log.d("12333",img);
                            if (img.charAt(0) == 'h' && img.charAt(1) == 't') {

                                Glide.with(getContext()).load(img).into(iv_pic);
                            } else {
                                Glide.with(getContext()).load("http://49.232.214.94/api/img/" + img).into(iv_pic);
                            }
                        }
                    }
                });


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class buy {
        private int good_id;
        private int goods_count;
    }
}
