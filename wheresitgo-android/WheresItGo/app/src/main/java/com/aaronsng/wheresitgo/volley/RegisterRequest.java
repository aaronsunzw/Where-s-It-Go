/**
 * Created by aaron on 14-Apr-17.
 */
package com.aaronsng.wheresitgo.volley;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.aaronsng.wheresitgo.common.StoredObject;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import  com.aaronsng.wheresitgo.common.Config;
import  com.aaronsng.wheresitgo.model.User;

public class RegisterRequest extends Request<JSONObject> {

    private Map<String, String> params;
    Listener listener;
//    static  String url =Config.url+"public/api/user/register";
    static  String urlTail = "public/api/user/register";

    public RegisterRequest(int method, Map<String, String> params, Listener listener, String urlHead){
        super(method, urlHead+urlTail, null);
        this.listener = listener;
        this.params = params;
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
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
            listener.onFailure(Config.RequestStatusCode.GENERAL_ERROR);
    }
    @Override
    protected void deliverResponse(JSONObject response) {
        try {
            System.out.println(response.toString());
            //no error
            int status = new CommonRequest().getStatus(response);
            if(status==1)
            {
                String successText=  response.getJSONObject("response").getString("message");
                User user = new User(
                        response.getJSONObject("user").getString("user_id"),
                        response.getJSONObject("user").getString("name"),
                        response.getJSONObject("user").getString("username"),
                        response.getJSONObject("user").getString("email"),
                        params.get("password")
                        );
                this.listener.onSuccess(user);
            }
            else {
                this.listener.onFailure(Config.RequestStatusCode.FAILURE);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Log.e(Config.log_id, e.getMessage());
            this.listener.onFailure(Config.RequestStatusCode.GENERAL_ERROR);
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

    public interface Listener{
        void onSuccess(User user);
        void onFailure(Config.RequestStatusCode code);
    }


}

