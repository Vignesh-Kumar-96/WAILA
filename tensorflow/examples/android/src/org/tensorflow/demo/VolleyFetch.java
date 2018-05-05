package org.tensorflow.demo;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.tensorflow.demo.WikiJSON.jsonToWikiItem;

/**
 * Created by witchel on 1/10/2018.
 */

public class VolleyFetch implements RateLimit.RateLimitCallback {

    // Add a request to Fetch, but wait for RateLimit to say it is ok.
    public void add(WikiJSON.IWikiJSON wikiJSON, URL url) {
        RateLimit.RateLimitObject rateLimitObject = new RateLimit.RateLimitObject(wikiJSON, url);
        RateLimit.getInstance().add(this, rateLimitObject);
    }

    public void rateLimitReady(final RateLimit.RateLimitObject rateLimitObject) {
        // Rate limit ready, launch download
        // XXX Write this.  Read this documentation.
        // https://developer.android.com/training/volley/request.html
        // Do a JSONObject request and pick out the parts that you need.
        // Or maybe try gson if you think that'll be easier (I haven't tried).

        final String url = (rateLimitObject.url).toString();
        String tag_json_obj = "json_obj_req";
        final WikiJSON.IWikiJSON rj = rateLimitObject.wikiJSON;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("query");
                            WikiItem wikiItem = jsonToWikiItem(data);
                            rj.fetchComplete(wikiItem);
                        } catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            rj.fetchCancel(url);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                });
        Net.getInstance().addToRequestQueue(jsObjRequest, tag_json_obj);
    }


}
