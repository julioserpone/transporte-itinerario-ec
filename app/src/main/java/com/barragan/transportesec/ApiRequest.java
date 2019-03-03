package com.barragan.transportesec;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.util.Log;


public class ApiRequest {

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url;
    private String respuesta;

    ApiRequest(String Url) {
        this.url = Url;
    }

    public void sendAndRequestResponse(Context context, Request tipo_request) {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(context);
        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, this.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                respuesta = response.toString();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        });

        //mRequestQueue.add(mStringRequest);

    }
}
