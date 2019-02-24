package com.aaronsng.wheresitgo.volley;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.aaronsng.wheresitgo.common.Config;
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
import java.util.Map;

/**
 * Created by aaron on 02-May-17.
 */

public class DeleteCategoryRequest extends Request<JSONObject> {

    private Map<String, String> params;
    DeleteCategoryRequest.Listener listener;
//    static  String url = Config.url+"public/api/category/delete-category";
    static String urlTail = "public/api/category/delete-category";

    public DeleteCategoryRequest(int method, Map<String, String> params, DeleteCategoryRequest.Listener listener, String urlHead){
        super(method, urlHead+urlTail, null);
        this.listener = listener;
        this.params = params;
    }

    protected void deliverResponse(JSONObject response) {
        try {
            Log.e(Config.log_id, response.toString());
            System.out.println(response.toString());
            //no error
            int status = new CommonRequest().getStatus(response);
            if(status==1)
            {
                String successText=  response.getJSONObject("response").getString("message");
                Log.e("RESPONSE_MESSAGE:", successText);

                this.listener.onSuccess();
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
            Log.e(Config.log_id, "JSONException"+je.getMessage());
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        listener.onFailure(Config.RequestStatusCode.GENERAL_ERROR);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError){
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }

        return volleyError;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    };

    public interface Listener{
        void onSuccess();
        void onFailure(Config.RequestStatusCode code);
    }
}
