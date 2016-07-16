package com.example.deft.dish.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.deft.dish.R;
import com.example.deft.dish.util.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.example.deft.dish.util.HandlerUtil.sendMessage;


public class LoginActivity extends AppCompatActivity {

    private Button buttonRegister = null;
    private Button buttonLogin = null;
    private Button buttonSelect = null;
    private static ProgressDialog loginProgress;
    private static final int MSG_LOGIN_RESULT = 0;
    private MyHandler mHandler;

    private void handleLoginResult(JSONObject json) {
        /*
         * login_result:
		 * -1：登陆失败，未知错误！
		 * 0: 登陆成功！
		 * 1：登陆失败，用户名或密码错误！
		 * 2：登陆失败，用户名不存在！
		 * */
        int returnCode = -1;
        String message = "服务器异常";
        try {
            returnCode = json.getInt("return_code");
            message = json.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (returnCode) {
            case 0:
                onLoginSuccess();
                break;
            default:
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                break;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_main222);

        buttonRegister = (Button) findViewById(R.id.button9);
        buttonLogin = (Button) findViewById(R.id.in);
        buttonSelect = (Button) findViewById(R.id.check);

        final EditText usernameEditText = (EditText) findViewById(R.id.account);
        final EditText passwordEditText = (EditText) findViewById(R.id.pwd);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //RegisterActivity
                Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View v) {
                                               login(usernameEditText.getText().toString(),
                                                       passwordEditText.getText().toString());
                                           }
                                       }
        );

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
        mHandler = new MyHandler(this);

    }

    private void onLoginSuccess() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        /*try {
            intent.putExtra("username", json.getString("username"));
            intent.putExtra("gender", json.getString("gender"));
            intent.putExtra("age", json.getInt("age"));
            intent.putExtra("phone", json.getString("phone"));
            intent.putExtra("email", json.getString("email"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        startActivity(intent);
        finish();
    }


    private void login(final String username, final String password) {
        loginProgress = ProgressDialog.show(this, null, "登陆中...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(mHandler, MSG_LOGIN_RESULT, connectServer(username, password));
            }
        }).start();
    }


    private static JSONObject connectServer(final String username, final String password) {
        String serverUrl = "login_user";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        return HttpUtil.request(serverUrl, params);
    }

    private static class MyHandler extends Handler {
        private WeakReference<LoginActivity> mOuter;

        MyHandler(LoginActivity activity) {
            mOuter = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity outer = mOuter.get();
            switch (msg.what) {
                case MSG_LOGIN_RESULT:
                    loginProgress.dismiss();
                    JSONObject json = (JSONObject) msg.obj;
                    outer.handleLoginResult(json);
                    break;
            }
        }
    }
}
