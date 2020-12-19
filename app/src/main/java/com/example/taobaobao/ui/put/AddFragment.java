package com.example.taobaobao.ui.put;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.taobaobao.MyData;
import com.example.taobaobao.R;
import com.example.taobaobao.ui.me.registerFragment;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {
    Button button;
    EditText et_name;
    EditText et_num;
    EditText et_price;
    EditText et_if;
    ImageView im_pic;
    private int flag = 0;
    private Dialog bottomDialog;
    private Context context;
    private Activity activity;
    private Uri photoUri;   //相机拍照返回图片路径
    private File outputImage;
    public static final String STR_IMAGE = "image/*";
    private Uri cropImgUri;
    private static final int GET_BACKGROUND_FROM_CAPTURE_RESOULT = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final int TAKE_PHOTO = 3;
    private String hash;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_add, container, false);
        context = getContext();
        activity = getActivity();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.button);
        et_name = view.findViewById(R.id.editTextTextPersonName2);
        et_num = view.findViewById(R.id.editTextNumber);
        et_price = view.findViewById(R.id.editTextTextPersonName);
        et_if = view.findViewById(R.id.add_content);
        im_pic = view.findViewById(R.id.imageView7);

        im_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog = new Dialog(activity, R.style.Theme_Design_BottomSheetDialog);
                View contentView = LayoutInflater.from(activity).inflate(R.layout.layout_pic, null);
                TextView tv_camera = contentView.findViewById(R.id.tv_camera);
                TextView tv_chose = contentView.findViewById(R.id.tv_chose);
                TextView tv_cancle = contentView.findViewById(R.id.tv_cancle);
                bottomDialog.setContentView(contentView);
                ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
                layoutParams.width = getResources().getDisplayMetrics().widthPixels;
                contentView.setLayoutParams(layoutParams);
                bottomDialog.getWindow().setGravity(Gravity.BOTTOM);//弹窗位置
                bottomDialog.getWindow().setWindowAnimations(R.style.Animation_Design_BottomSheetDialog);//弹窗样式
                bottomDialog.show();
                tv_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectCamera();
                        bottomDialog.dismiss();

                    }
                });
                tv_chose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectPhoto();
                        bottomDialog.dismiss();
                    }
                });
                tv_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomDialog.dismiss();
                    }
                });

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = et_name.getText().toString();
                String info = et_if.getText().toString();
                if(name.length()!=0&&info.length()!=0) {
                    int price = Integer.parseInt(et_price.getText().toString());
                    int number = Integer.parseInt(et_num.getText().toString());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            AddFragment.Mygoods use = new AddFragment.Mygoods();
                            use.info = info;
                            use.name = name;
                            use.price = price;
                            use.quantity = number;
                            use.img = hash;
                            Log.d("1233i", gson.toJson(use));
                            MyData myData = new MyData(context);
                            OkHttpClient client = new OkHttpClient().newBuilder()
                                    .build();
                            MediaType mediaType = MediaType.parse("application/json");
                            RequestBody body = RequestBody.create(mediaType, gson.toJson(use));
                            Request request = new Request.Builder()
                                    .url("http://49.232.214.94/api/good")
                                    .method("POST", body)
                                    .addHeader("Accept", "application/json")
                                    .addHeader("Authorization", myData.load_token())
                                    .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                                    .addHeader("Content-Type", "application/json")
                                    .build();
                            try {
                                Response response = client.newCall(request).execute();
                                String responseData = response.body().string();
                                Log.d("1233", responseData);
                                getfeedback(responseData);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                else {
                    Toast.makeText(getActivity(), "不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public class Mygoods {
        private String name;
        private String info;
        private int price;
        private int quantity;
        private String img;
    }

    private void selectCamera() {
        outputImage = new File(context.getExternalCacheDir(), "camera_photos.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoUri = Uri.fromFile(outputImage);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STR_IMAGE);
        startActivityForResult(intent, GET_BACKGROUND_FROM_CAPTURE_RESOULT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {

            case GET_BACKGROUND_FROM_CAPTURE_RESOULT:   //相册返回
                photoUri = data.getData();
                cropRawPhoto(photoUri);
                break;


            case TAKE_PHOTO://   拍照返回
                cropRawPhoto(photoUri);

                break;

            case RESULT_REQUEST_CODE:   //裁剪完照片
                if (cropImgUri != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Bitmap headImage = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(cropImgUri));
                                Objects.requireNonNull(activity).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        im_pic.setImageBitmap(headImage);
                                    }
                                });
                                final String Photo = getRealPath(getContext(), cropImgUri);
                                Log.d("1233p", Photo);
                                MyData myData = new MyData(getContext());
                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("img", Photo,
                                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                                        new File(Photo)))
                                        .build();
                                Request request = new Request.Builder()
                                        .url("http://49.232.214.94/api/upload/good")
                                        .method("POST", body)
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", myData.load_token())
                                        .build();
                                Response response = client.newCall(request).execute();
                                Log.d("1233p","here");
                                String responseData = response.body().string();
                                Log.d("1233",responseData);
                                getfeedback2(responseData);
                            } catch (Exception e) {
                                Objects.requireNonNull(activity).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "上传失败，可能是图片过大", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(activity, "cropImgUri为空！", Toast.LENGTH_SHORT).show();
                }
                break;

        }


        super.onActivityResult(requestCode, resultCode, data);


    }

    public void cropRawPhoto(Uri uri) {
        File cropImage = new File(Environment.getExternalStorageDirectory(), "crop_image.jpg");
        String path = cropImage.getAbsolutePath();
        try {
            if (cropImage.exists()) {
                cropImage.delete();
            }
            cropImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cropImgUri = Uri.fromFile(cropImage);
        Log.d("1233p", "here2");
        Intent intent = new Intent("com.android.camera.action.CROP");
//设置源地址uri
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
//设置目的地址uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImgUri);
//设置图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    public static String getRealPath(Context context, Uri uri) {
        if (context == null || uri == null) {
            Log.d("1233p", "smdmy");
            return null;

        }
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Byfile(context, uri);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Api11To18(context, uri);
        }
        return getRealPathFromUri_AboveApi19(context, uri);
    }

    private static String getRealPathFromUri_Byfile(Context context, Uri uri) {
        Log.d("1233p", "old");
        String uri2Str = uri.toString();
        String filePath = uri2Str.substring(uri2Str.indexOf(":") + 3);
        return filePath;
    }


    @SuppressLint("NewApi")
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = null;
        Log.d("1233p", "nnn");
        wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Log.d("1233p", "n");
        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }


    public void getfeedback(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            int code = jsonObject.getInt("code");
            if (code != 200) {
                flag = 0;
                String msg = jsonObject.getString("msg");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "上架成功", Toast.LENGTH_SHORT).show();
                        NavController controller = Navigation.findNavController(getView());
                        controller.navigate(R.id.action_addFragment_to_putFragment);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getfeedback2(String responseData) {
        try {

            JSONObject jsonObject1 = new JSONObject(responseData);
            int code = jsonObject1.getInt("code");
            Log.d("1233", String.valueOf(code));
            if (code == 200) {
                JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                hash = jsonObject2.getString("hash");
                Log.d("1233", "44444444" + hash);
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
}
