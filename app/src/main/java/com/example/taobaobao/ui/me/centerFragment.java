package com.example.taobaobao.ui.me;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.taobaobao.MyData;
import com.example.taobaobao.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class centerFragment extends Fragment {
    private ImageView imageView;
    private ImageView imageView2;
    private TextView name_view;
    private ImageView sex_view;
    private ImageView iv_head;
    private RelativeLayout change_pic;
    private RelativeLayout change_name;
    private RelativeLayout change_sex;
    private RelativeLayout change_password;
    private String name;
    private String head;
    private EditText edit;
    private boolean sex;
    private RadioGroup rgSex;
    private boolean sex2;
    private Dialog bottomDialog;
    private int SELECT_PICTURE = 0x00;
    private int SELECT_CAMER = 0x01;
    private Intent intent;
    private Bitmap bitmap;
    private static final int GET_BACKGROUND_FROM_CAPTURE_RESOULT = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final int TAKE_PHOTO = 3;
    private Uri photoUri;   //相机拍照返回图片路径
    private File outputImage;
    public static final String STR_IMAGE = "image/*";
    private Uri cropImgUri;
    private Context context;
    private Activity activity;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_center, container, false);
        context = getContext();
        activity = getActivity();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.pic_2);
        name_view = view.findViewById(R.id.name);
        sex_view = (ImageView) view.findViewById(R.id.imageView2);
        change_name = (RelativeLayout) view.findViewById(R.id.name233);
        change_sex = (RelativeLayout) view.findViewById(R.id.sex233);
        change_pic = (RelativeLayout) view.findViewById(R.id.change);
        change_password = (RelativeLayout) view.findViewById(R.id.password);
        imageView2 = view.findViewById(R.id.pic_3);
        iv_head = view.findViewById(R.id.picture);


        MyData myData = new MyData(context);
        sex2 = true;
        Log.d("12330", myData.load_token());
        Log.d("12331", myData.load_check().toString());

        if (!myData.load_check()) {
            NavController controller = Navigation.findNavController(Objects.requireNonNull(getView()));
            controller.navigate(R.id.action_centerFeagment_to_meFeagment);
        }
        if (isAdded() && activity != null && context != null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController controller = Navigation.findNavController(v);
                    controller.navigate(R.id.action_centerFeagment_to_meFeagment);
                }
            });
            change_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater factory = LayoutInflater.from(activity);
                    final View view = factory.inflate(R.layout.layout_name, null);
                    edit = view.findViewById(R.id.name);

                    new AlertDialog.Builder(activity)
                            .setTitle("请输入新的昵称")
                            .setView(view)
                            .setPositiveButton("确定",
                                    new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            //事件
                                            if (edit.getText().toString().length() == 0) {
                                                Toast.makeText(activity, "昵称不能为空！", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else if (edit.getText().toString().length() > 8) {
                                                Toast.makeText(activity, "昵称最多输入8个字符！", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else {
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        MyData myData1 = new MyData(context);
                                                        Gson gson = new Gson();
                                                        Myname use = new Myname();
                                                        use.name = (edit.getText().toString());
                                                        use.sex = myData1.load_sex();
                                                        OkHttpClient client = new OkHttpClient().newBuilder()
                                                                .build();
                                                        MediaType mediaType = MediaType.parse("application/json");
                                                        Log.d("1233sex", gson.toJson(use));
                                                        RequestBody body = RequestBody.create(mediaType, gson.toJson(use));
                                                        Request request = new Request.Builder()
                                                                .url("http://49.232.214.94/api/user")
                                                                .method("PUT", body)
                                                                .addHeader("Accept", "application/json")
                                                                .addHeader("Authorization", myData1.load_token())
                                                                .build();
                                                        try {
                                                            Response response = client.newCall(request).execute();
                                                        } catch (IOException e) {
                                                            activity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(context, "网络罢工了呢(ó﹏ò｡)", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            e.printStackTrace();
                                                        }

                                                        onResume();
                                                    }
                                                }).start();
                                            }
                                        }
                                    }).setNegativeButton("取消", null).create().show();

                }
            });
            change_sex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater factory = LayoutInflater.from(activity);
                    final View view = factory.inflate(R.layout.layout_sex, null);
                    rgSex = (RadioGroup) view.findViewById(R.id.rgSex);

                    new AlertDialog.Builder(activity)
                            .setTitle("请选择新的性别吧~")//提示框标题
                            .setView(view)
                            .setPositiveButton("确定",//提示框的两个按钮
                                    new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (rgSex.getCheckedRadioButtonId() == R.id.radio0) {
                                                sex2 = true;
                                            } else if (rgSex.getCheckedRadioButtonId() == R.id.radio1) {
                                                sex2 = false;
                                            } else {
                                                return;
                                            }
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MyData myData2 = new MyData(context);
                                                    Gson gson = new Gson();
                                                    Myname use = new Myname();
                                                    use.name = myData2.load_name();
                                                    use.sex = sex2;
                                                    OkHttpClient client = new OkHttpClient().newBuilder()
                                                            .build();
                                                    MediaType mediaType = MediaType.parse("application/json");
                                                    RequestBody body = RequestBody.create(mediaType, gson.toJson(use));
                                                    Request request = new Request.Builder()
                                                            .url("http://49.232.214.94/api/user")
                                                            .method("PUT", body)
                                                            .addHeader("Accept", "application/json")
                                                            .addHeader("Authorization", myData2.load_token())
                                                            .build();
                                                    try {
                                                        Response response = client.newCall(request).execute();
                                                    } catch (IOException e) {
                                                        Objects.requireNonNull(activity).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(context, "网络罢工了呢(ó﹏ò｡)", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        e.printStackTrace();
                                                    }

                                                    onResume();
                                                }
                                            }).start();


                                        }
                                    }).setNegativeButton("取消", null).create().show();

                }
            });
            change_pic.setOnClickListener(new View.OnClickListener() {
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
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myData.load_theme()) {
                        myData.save_theme(false);
                    } else {
                        myData.save_theme(true);
                    }
                    Toast.makeText(context, "主题修改成功，重启后生效！", Toast.LENGTH_SHORT).show();
                }
            });
            change_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, "后端没做这个接口呢", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded() && activity != null && context != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyData myData = new MyData(context);
                    Log.d("1233center", myData.load_token());
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url("http://49.232.214.94/api/user")
                            .method("GET", null)
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", myData.load_token())
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Log.d("1233resp", responseData);
                        if (isAdded() && activity != null && context != null) {
                            getfeedback(responseData);
                        }
                        onResume();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void getfeedback(String responseData) {
        try {

            JSONObject jsonObject1 = new JSONObject(responseData);
            int code = jsonObject1.getInt("code");
            if (code == 200) {
                MyData myData = new MyData(context);
                JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                JSONObject jsonObject3 = jsonObject2.getJSONObject("user");
                name = jsonObject3.getString("name");
                sex = jsonObject3.getBoolean("sex");
                head = jsonObject3.getString("head");
                myData.save_name(name);
                myData.save_sex(sex);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        name_view.setText(name);
                        if (sex) {
                            sex_view.setImageDrawable(getResources().getDrawable(R.drawable.sex_boy));
                        } else {
                            sex_view.setImageDrawable(getResources().getDrawable(R.drawable.sex_girl));
                        }
                        if (head.length() != 0) {
                            Log.d("12333", head);
                            if (head.charAt(0) == 'h' && head.charAt(1) == 't' && head.charAt(2) == 't') {
                                Glide.with(getContext()).load(head)
                                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                        .into(iv_head);
                            } else {
                                Glide.with(getContext()).load("http://49.232.214.94/api/img/" + head)
                                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                        .into(iv_head);
                            }
                        }

                    }
                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class Myname {
        private String name;
        private boolean sex;
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
                                        iv_head.setImageBitmap(headImage);
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
                                        .url("http://49.232.214.94/api/upload/head")
                                        .method("POST", body)
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", myData.load_token())
                                        .build();
                                Response response = client.newCall(request).execute();
                                Log.d("1233p", String.valueOf(response));

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

    public File getFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}
