package org.tensorflow.demo;

/**
 * Created by vignesh on 4/2/18.
 */
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Net {
    private RequestQueue mRequestQueue;
    private Context context;

    private static class NetHolder {
        public static Net helper = new Net();
    }

    public static Net getInstance() {
        return NetHolder.helper;
    }

    public static synchronized void init(Context _context) {
        // XXX You will die here if you do not call Net.init(...) in your MainActivity
        NetHolder.helper.context = _context;
        NetHolder.helper.mRequestQueue = Volley.newRequestQueue(_context.getApplicationContext());
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}

