package com.aaronsng.wheresitgo.volley;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.Record;
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
 * Created by aaron on 15-Apr-17.
 */

public class RetrieveAllRecordsRequest extends Request<JSONObject> {
    private Map<String, String> params;
    RetrieveAllRecordsRequest.Listener listener;
//    static  String url = Config.url+"public/api/record/all-records";
    static  String urlTail = "public/api/record/all-records";
    ArrayList<Record> recordArrayList = new ArrayList<Record>();
    private Context appContext;

    public RetrieveAllRecordsRequest(int method, Map<String, String> params, RetrieveAllRecordsRequest.Listener listener, String urlHead){
        super(method, urlHead+urlTail, null);
        this.listener = listener;
        this.params = params;
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
            return Response.error(new ParseError(je));
        }
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
                if(!request.getString("message").contentEquals("No Records")){
                    //JSONObject userJson =  response.getJSONObject("user");
                    JSONArray recordJsonArray= request.getJSONArray("record");
                    for(int i =0;i<  recordJsonArray.length();i++)
                    {
                        System.out.println("Record " + recordJsonArray.getJSONObject(i).getString("record_id"));
                        JSONObject recordJSON = recordJsonArray.getJSONObject(i);

                        Record record = new Record(
                                recordJsonArray.getJSONObject(i).getString("record_id"),
                                recordJsonArray.getJSONObject(i).getString("record_title"),
                                recordJsonArray.getJSONObject(i).getString("record_description"),
                                recordJsonArray.getJSONObject(i).getString("record_image"),
                                recordJsonArray.getJSONObject(i).getString("record_audio"),
                                recordJsonArray.getJSONObject(i).getString("category_id")

                        );

                        recordArrayList.add(record);
                        System.out.println("Record " + recordJsonArray.getJSONObject(i).getString("record_id"));
                    }

                    this.listener.onSuccess(recordArrayList);
                }else{
                    this.listener.onSuccess(recordArrayList);
                }


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

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    };

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

    public interface Listener{
        void onSuccess(ArrayList<Record> recordArrayList);
        void onFailure(Config.RequestStatusCode code);
    }
}
