package com.aaronsng.wheresitgo.volley;

import org.json.JSONObject;


public class CommonRequest {



    public int getStatus(JSONObject response) {
        try {
            if (response.getJSONObject("response").getString("status").equalsIgnoreCase("1")) {
                return 1;
            } else
                return Integer.parseInt(response.getJSONObject("response").getString("status"));
        } catch (Exception e) {

            return 0;
        }
    }


    //  1=  authenticated
    //  0=  wrong password or loginid
    // -1=  generic error
    public int getAuthentication(JSONObject response) {
        try {
            if (response.getString("auth").equalsIgnoreCase("1")) {
                return 1;
            } else
                return 0;
        } catch (Exception e) {

            return -1;
        }
    }
}
