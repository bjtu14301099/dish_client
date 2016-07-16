package com.example.deft.dish.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.example.deft.dish.R;
import com.example.deft.dish.modal.Dish;
import com.example.deft.dish.modal.DishIngredient;
import com.example.deft.dish.modal.DishProcess;
import com.example.deft.dish.util.BitmapUtil;
import com.example.deft.dish.util.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/7/10.
 */
public class ProcessActivity extends AppCompatActivity {

    private Button bt_addprocess;
    private LinearLayout ll;
    private ImageButton ib[];
    private Bitmap bm[];
    private TextView tv[];
    private int processNum;
    private String[] areas1 = new String[]{"删除", "取消"};
    private Button bt_back;
    private String name;
    private String[] ingredient;
    private String[] amount;
    private Intent passIntent;
    private int num;
    private TextView title;
    private String level;
    private String time;
    private Button bt_finish;
    private Handler mHandler;
    private static ProgressDialog uploadProgress;


    private void handleUploadResult(JSONObject json) {


        int returnCode = -1;
        String message = "服务器异常";
        JSONArray datam = null;
        try {
            returnCode = json.getInt("return_code");
            message = json.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (returnCode) {
            case 0:

                onUploadSuccess();
                break;
            default:
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                break;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mHandler == null) {
            mHandler = new Handler(getApplicationContext().getMainLooper()) {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 0:
                            uploadProgress.dismiss();
                            JSONObject json = (JSONObject) msg.obj;
                            handleUploadResult(json);
                            break;
                    }
                }
            };
        }

        setContentView(R.layout.activity_process);
        bt_addprocess = (Button) findViewById(R.id.button6);
        bt_addprocess.setOnClickListener(new btapListener());
        bt_back = (Button) findViewById(R.id.button);
        bt_back.setOnClickListener(new btbListener());
        ll = (LinearLayout) findViewById(R.id.linear);
        title = (TextView) findViewById(R.id.textView);
        ib = new ImageButton[30];
        bm = new Bitmap[30];
        tv = new TextView[30];
        ingredient = new String[20];
        amount = new String[20];
        bt_finish = (Button) findViewById(R.id.button2);
        passIntent = this.getIntent();
        bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                name string 菜谱名
                level string 难度
                time string 时间
                ingredient string[] 配料数组
                amount string[]  配料多少的数组
                num int 配料加配料多少的总和
                bm bitmap[] 照片数组
                tv TextView[] textview数组，读取步骤描述


                 */


                Bundle bundle = passIntent.getExtras();
                name = bundle.getString("name");
                ingredient = bundle.getStringArray("ingredient");
                amount = bundle.getStringArray("amount");
                num = bundle.getInt("num");

                level = bundle.getString("level");
                time = bundle.getString("time");
                Dish dish = new Dish();
                dish.setName(name);
                dish.setLevel(level);
                dish.setTime(time);
                for (int i = 0; i < bm.length; i++) {
                    if (bm[i] == null)
                        break;
                    DishProcess dishProcess = new DishProcess();
                    dishProcess.setImage(BitmapUtil.toString(bm[i]));
                    dishProcess.setDetail(tv[i].getText().toString());
                    dish.getDishProcesses().add(dishProcess);
                }
                for (int i = 0; i < ingredient.length; i++) {
                    if (ingredient[i] == null)
                        break;
                    DishIngredient dishIngredient = new DishIngredient();
                    dishIngredient.setName(ingredient[i]);
                    dishIngredient.setAmount(amount[i]);
                    dish.getDishIngredients().add(dishIngredient);
                }


                upload(dish);
                title.setText(name);


            }
        });
    }

    class btbListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ProcessActivity.this.finish();
        }
    }

    class btapListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();

            intent.setClass(ProcessActivity.this, AddProcess.class);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);

            startActivityForResult(intent, 0);


        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap photo = bundle.getParcelable("bitmap");
            bm[processNum] = photo;
            String des = bundle.getString("des");
            ib[processNum] = new ImageButton(ProcessActivity.this);
            tv[processNum] = new TextView(ProcessActivity.this);
            Drawable drawable = new BitmapDrawable(photo);
            ib[processNum].setBackgroundDrawable(drawable);
            tv[processNum].setText(des);
            LinearLayout ll1 = new LinearLayout(ProcessActivity.this);
            ll1.setOrientation(LinearLayout.HORIZONTAL);
            ib[processNum].setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            tv[processNum].setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));
            ib[processNum].setTag(processNum);
            ib[processNum].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    new android.app.AlertDialog.Builder(ProcessActivity.this).setTitle("").setItems(areas1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                int i = (Integer) view.getTag();
                                ib[i].setVisibility(View.GONE);
                                tv[i].setVisibility(View.GONE);
                                for (int j = i; j < processNum - 1; j++) {
                                    bm[j] = bm[j + 1];
                                    ib[j] = ib[j + 1];
                                    tv[j] = tv[j + 1];
                                }
                                ib[processNum - 1].setVisibility(View.GONE);
                                tv[processNum - 1].setVisibility(View.GONE);
                                processNum--;
                            }
                            dialog.dismiss();
                        }
                    }).show();
                    return true;
                }
            });
            ll1.addView(ib[processNum]);
            ll1.addView(tv[processNum]);
            ll.addView(ll1);
            processNum++;

        }

    }

    private void onUploadSuccess() {
        Toast.makeText(this, "success", Toast.LENGTH_LONG).show();
    }


    private void upload(final Dish dish) {
        uploadProgress = ProgressDialog.show(this, null, "正在保存...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(0, connectServer(dish));
            }
        }).start();
    }


    private static JSONObject connectServer(Dish dish) {
        String serverUrl = "upload_dish";
        String s = com.alibaba.fastjson.JSON.toJSONString(dish);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("json", s));
        System.out.println(s);
        return HttpUtil.request(serverUrl, params);
    }

    private void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }
}

