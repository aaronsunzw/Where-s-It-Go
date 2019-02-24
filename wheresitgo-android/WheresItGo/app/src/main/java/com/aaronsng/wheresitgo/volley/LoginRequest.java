package com.aaronsng.wheresitgo.volley;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Member;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.aaronsng.wheresitgo.common.StoredObject;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.model.User;


public class LoginRequest extends Request<JSONObject> {

    private Map<String, String> params;
    LoginListener loginListener;
    private Context appContext;
//    static String url =Config.url+"public/api/auth";
    static String urlTail = "public/api/auth";

    public LoginRequest(int method, Map<String, String> params, LoginListener loginListener, String urlHead){
        super(method, urlHead+urlTail, null);
        this.loginListener=loginListener;
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {


        return params;
    };

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {

            Log.e(Config.log_id, "UnsupportedEncodingException");
            return Response.error(new ParseError(e));
        } catch (JSONException je) {

            Log.e(Config.log_id, "JSONException");
            return Response.error(new ParseError(je));
        }
    }
    @Override
    public void deliverError(VolleyError error) {
        loginListener.onFailure(Config.RequestStatusCode.GENERAL_ERROR);
    }
    @Override
    protected void deliverResponse(JSONObject response) {
        try {
            System.out.println(response.toString());
            //no error
            if(response.getJSONObject("response").getString("status").equalsIgnoreCase("1"))
            {
                User user = new User(
                        response.getJSONObject("user").getString("user_id"),
                        response.getJSONObject("user").getString("name"),
                        response.getJSONObject("user").getString("username"),
                        response.getJSONObject("user").getString("email"),
                        params.get("password")
                );

                this.loginListener.onSuccess(user);
            } else {
                this.loginListener.onFailure(Config.RequestStatusCode.AUTH_FAILED);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

            Log.e(Config.log_id, e.getMessage());
            Log.e(Config.log_id, "here59");
            this.loginListener.onFailure(Config.RequestStatusCode.GENERAL_ERROR);
        }

    }
   /* @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }//In your extended request class*/

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError){


        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }

        return volleyError;
    }

    public interface LoginListener{
        void onSuccess(User user);
        void onFailure(Config.RequestStatusCode code );
    }


}