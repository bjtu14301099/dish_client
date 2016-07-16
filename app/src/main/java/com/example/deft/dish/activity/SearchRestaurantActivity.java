package com.example.deft.dish.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.deft.dish.R;
import com.example.deft.dish.modal.RestaurantDish;
import com.example.deft.dish.util.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class SearchRestaurantActivity extends AppCompatActivity {

    private Button buttonSelect = null;
    private static ProgressDialog selectProgress;
    private static final int MSG_SELECT_RESULT = 0;
    private static Handler mHandler;

    private void handleSelectResult(JSONObject json) {


        int returnCode = json.getInteger("return_code");
        String message = json.getString("message");
        List<RestaurantDish> dishes = JSON.parseArray(json.getJSONArray("data").toJSONString(), RestaurantDish.class);
        switch (returnCode) {
            case 0:
                onSelectSuccess(dishes);
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
                        case MSG_SELECT_RESULT:
                            selectProgress.dismiss();
                            JSONObject json = (JSONObject) msg.obj;
                            handleSelectResult(json);
                            break;
                    }
                }
            };
        }

        setContentView(R.layout.activity_select_activity_test);

        buttonSelect = (Button) findViewById(R.id.select);

        //final EditText dishEditText = (EditText) findViewById(R.id.account);

        buttonSelect.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                //String dish = dishEditText.getText().toString();
                                                String dish = "鱼";
                                                select(dish);


                                            }
                                        }
        );
    }

    private void onSelectSuccess(List<RestaurantDish> data) {
        Toast.makeText(this, data.size(), Toast.LENGTH_LONG).show();
    }


    private void select(final String dishname) {
        selectProgress = ProgressDialog.show(this, null, "加载中...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(MSG_SELECT_RESULT, connectServer(dishname));
            }
        }).start();
    }


    private static JSONObject connectServer(final String name) {
        String serverUrl = "search_rest_dish";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("name", name));
        return HttpUtil.request2(serverUrl, params);
    }

    private void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }


}
