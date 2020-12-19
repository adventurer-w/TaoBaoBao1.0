package com.example.taobaobao.ui.me;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.taobaobao.MyData;
import com.example.taobaobao.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MeFragment extends Fragment {

    private TextView register;
    private Button button;
    private EditText et_account;
    private EditText et_cipher;
    private int flag = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        register = view.findViewById(R.id.to_register);
        button = view.findViewById(R.id.load);
        et_account = view.findViewById(R.id.et_1);
        et_cipher = view.findViewById(R.id.et_2);
        }

    @Override
    public void onResume() {
        super.onResume();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.个人中心);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account_query = et_account.getText().toString();
                String cipher_compare = et_cipher.getText().toString();
                if (account_query.length() == 0) {
                    Toast.makeText(getActivity(), "请输入用户名！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (cipher_compare.length() == 0) {
                    Toast.makeText(getActivity(), "请输入密码！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Gson gson = new Gson();
                                MeFragment.Mylogin use = new MeFragment.Mylogin();
                                use.account = account_query;
                                use.password = cipher_compare;

                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                MediaType mediaType = MediaType.parse("application/json");
                                RequestBody body = RequestBody.create(mediaType, gson.toJson(use));
                                Request request = new Request.Builder()
                                        .url("http://49.232.214.94/api/login")
                                        .method("POST", body)
                                        .addHeader("Accept", "application/json")
                                        .build();

                                Response response = client.newCall(request).execute();
                                Log.d("1233", "here");
                                String responseData = response.body().string();
                                getfeedback(responseData);

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

                        private void getfeedback(String responseData) {
                            try {

                                JSONObject jsonObject1 = new JSONObject(responseData);
                                int code = jsonObject1.getInt("code");
                                if (code != 200) {
                                    String msg = jsonObject1.getString("msg");
                                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
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
                                    myData.save_token(token);
                                    flag = 1;
                                    myData.save_check(true);
                                    Log.d("1233", myData.load_token());
                                    Log.d("1233", String.valueOf(myData.load_check()));
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            NavController controller = Navigation.findNavController(v);
                                            controller.navigate(R.id.action_meFeagment_to_centerFeagment);
                                        }
                                    });

                                }

                            } catch (JSONException e) {
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
            }
            });
        }





    public class Mylogin {
            private String account;
            private String password;
        }

    }