package com.example.deft.dish.util;

import android.os.Handler;
import android.os.Message;

/**
 * Created by niezeshu on 16/7/12.
 */
public class HandlerUtil {
    public static void sendMessage(Handler mHandler, int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }
}
