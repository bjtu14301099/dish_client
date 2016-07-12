package com.example.deft.dish.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.List;

import static com.example.deft.dish.constant.Constant.URL_ROOT;


/**
 * Created by niezeshu on 16/7/12.
 */
public class HttpUtil {

    public static JSONObject request(String url, List<NameValuePair> params) {
        String SERVER_URL = URL_ROOT + url;
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVER_URL);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
