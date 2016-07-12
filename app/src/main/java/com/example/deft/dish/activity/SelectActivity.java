package com.example.deft.dish.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.deft.dish.R;
import com.example.deft.dish.util.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends AppCompatActivity {

    private Button buttonSelect = null;
    private static ProgressDialog selectProgress;
    private static final int MSG_SELECT_RESULT = 0;
    private static Handler mHandler;

    private void handleSelectResult(JSONObject json) {


        int returnCode = -1;
        String message = "服务器异常";
        JSONArray datam = null;
        try {
            returnCode = json.getInt("return_code");
            message = json.getString("message");
            datam = json.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (returnCode) {
            case 0:

                onSelectSuccess(datam);
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
                                                String dish = "1";
                                                select(dish);


                                            }
                                        }
        );
    }

    private void onSelectSuccess(JSONArray data) {
        try {
            JSONObject jo = (JSONObject) data.get(0);
            Toast.makeText(this, jo.getString("dish_name"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void select(final String dishname) {
        selectProgress = ProgressDialog.show(this, null, "查找中...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(MSG_SELECT_RESULT, connectServer(dishname));
            }
        }).start();
    }


    private static JSONObject connectServer(final String dish) {
        String serverUrl = "admin_table_search";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("dishname", dish));
        return HttpUtil.request(serverUrl, params);
    }

    private void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }


}
