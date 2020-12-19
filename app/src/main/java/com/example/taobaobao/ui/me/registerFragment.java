package com.example.taobaobao.ui.me;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.taobaobao.MyData;
import com.example.taobaobao.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class registerFragment extends Fragment {
    private Button button;
    private EditText account;
    private EditText cipher;
    private EditText cipher2;
    private EditText name;
    private int flag = 0;
    private ImageView taobao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.register);
        account = view.findViewById(R.id.et_account);
        cipher = view.findViewById(R.id.et_cipher);
        name = view.findViewById(R.id.et_name);
        cipher2 = view.findViewById(R.id.et_cipher_2);
        taobao = view.findViewById(R.id.taobao);

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        anim.setFillAfter(true);//设置旋转后停止
        taobao.startAnimation(anim);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str_account = account.getText().toString();
                final String str_cipher = cipher.getText().toString();
                String str_name = name.getText().toString();
                String str_cipher2 = cipher2.getText().toString();


                if (str_account.length()<8)
                { Toast.makeText(getActivity(), "用户名至少8个数字或字母！", Toast.LENGTH_SHORT).show(); return;}
                else if (str_cipher.length()<8)
                { Toast.makeText(getActivity(), "密码至少8个字符！", Toast.LENGTH_SHORT).show(); return;}
                else if (str_name.length()==0)
                { Toast.makeText(getActivity(), "昵称不能为空！", Toast.LENGTH_SHORT).show(); return;}
                else if (str_name.length()>8)
                {Toast.makeText(getActivity(), "昵称最多输入8个字符！", Toast.LENGTH_SHORT).show(); return;}
                else if (!str_cipher.equals(str_cipher2))
                {Toast.makeText(getActivity(), "两次密码输入不一致！", Toast.LENGTH_SHORT).show(); return;}
                else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Gson gson = new Gson();
                            Myregister use = new Myregister();
                            use.account = str_account;
                            use.password = str_cipher;

                            OkHttpClient client = new OkHttpClient();
                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                            Log.d("1233", gson.toJson(use));
                            RequestBody body = RequestBody.create(JSON, gson.toJson(use));
                            Request request = new Request.Builder()
                                    .url("http://49.232.214.94/api/register")
                                    .addHeader("Accept", "application/json")
                                    .post(body)
                                    .build();
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            Log.d("1233response", responseData);
                            getfeedback(responseData);

                        } catch (IOException e) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "网络罢工了呢(ó﹏ò｡)", Toast.LENGTH_SHORT).show();
                                }
                            });
                            e.printStackTrace();
                        }
                    }

                    private void getfeedback(String responseData) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(responseData);
                            int code = jsonObject1.getInt("code");
                            if (code != 200) {
                                String msg = jsonObject1.getString("msg");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                flag = 0;
                            } else {
                                JSONObject jsonObject = jsonObject1.getJSONObject("data");
                                String token = jsonObject.getString("token");
                                MyData myData = new MyData(getContext());
                                Log.d("1233",token);
                                myData.save_token(token);
                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "注册成功！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Gson gson2 = new Gson();
                                Myname use2 = new Myname();
                                use2.name = str_name;
                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                MediaType mediaType = MediaType.parse("application/json");
                                RequestBody body = RequestBody.create(mediaType, gson2.toJson(use2));
                                Request request = new Request.Builder()
                                        .url("http://49.232.214.94/api/user")
                                        .method("PUT", body)
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", myData.load_token())
                                        .build();
                                Response response = client.newCall(request).execute();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        NavController controller = Navigation.findNavController(v);
                                        controller.navigate(R.id.action_registerFragment_to_meFeagment);
                                    }
                                });


                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }


            }
        });
    }

    public class Myregister {
        public String account;
        public String password;
    }
    public class Myname{
        public String name;
    }

}
