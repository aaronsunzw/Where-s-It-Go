package com.aaronsng.wheresitgo.volley;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.Category;
import com.aaronsng.wheresitgo.model.User;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by aaron on 09-Apr-17.
 */

public class RetrieveAllCategoriesRequest extends Request<JSONObject> {
    private Map<String, String> params;
    RetrieveAllCategoriesRequest.Listener listener;
    static String urlTail = "public/api/category/all-categories";
    ArrayList<Category> categoryArrayList = new ArrayList<Category>();

    public RetrieveAllCategoriesRequest(int method, Map<String, String> params, RetrieveAllCategoriesRequest.Listener listener, String urlHead){
//        super(method, url, null);
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
        error.getMessage();
    }
    @Override
    protected void deliverResponse(JSONObject response) {
        try {
            System.out.println(response.toString());
            Log.d(Config.log_id,response.toString());
            //no error
            JSONObject request = response.getJSONObject("response");
            int status = new CommonRequest().getStatus(response);
            System.out.println("Status = "+status);
            if(status==1)
            {
                System.out.println("inside ");

                if(!request.getString("message").contentEquals("No Categories")){
                    //JSONObject userJson =  response.getJSONObject("user");
                    JSONArray categoryJsonArray= request.getJSONArray("category");
                    for(int i =0;i<  categoryJsonArray.length();i++)
                    {
                        System.out.println("Category " + categoryJsonArray.getJSONObject(i).getString("category_id"));
                        JSONObject categoryJSON = categoryJsonArray.getJSONObject(i);

                        Category category = new Category(
                                categoryJsonArray.getJSONObject(i).getString("category_id"),
                                categoryJsonArray.getJSONObject(i).getString("category_title"),
                                categoryJsonArray.getJSONObject(i).getString("category_description"),
                                categoryJsonArray.getJSONObject(i).getString("user_id")
                        );

                        categoryArrayList.add(category);
                        System.out.println("Category " + categoryJsonArray.getJSONObject(i).getString("category_id"));
                    }
                }


                this.listener.onSuccess(categoryArrayList);
            }
            else {
                Log.e(Config.log_id, "status !=1");
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
        void onSuccess(ArrayList<Category> categoryArrayList);
        void onFailure(Config.RequestStatusCode code);
    }


}
