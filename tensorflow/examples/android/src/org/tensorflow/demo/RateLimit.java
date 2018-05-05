package org.tensorflow.demo;

/**
 * Created by vignesh on 4/2/18.
 */

import android.os.Handler;

import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RateLimit {
    public interface RateLimitCallback {
        void rateLimitReady(RateLimitObject rateLimitObject);
    }
    public static class RateLimitObject {
        public WikiJSON.IWikiJSON wikiJSON;
        public URL url;
        public RateLimitObject(WikiJSON.IWikiJSON wikiJSON, URL url) {
            this.wikiJSON = wikiJSON;
            this.url = url;
        }
    }
    private class CallbackAndObject {
        public RateLimitCallback rateLimitCallback;
        public RateLimitObject rateLimitObject;
        public CallbackAndObject(RateLimitCallback rateLimitCallback, RateLimitObject rateLimitObject) {
            this.rateLimitCallback = rateLimitCallback;
            this.rateLimitObject = rateLimitObject;
        }
    }

    protected Handler handler;
    protected Runnable rateLimitRequest;
    protected final int rateLimitMillis = 2000; // 2 sec
    protected AtomicBoolean okToRun;
    protected CopyOnWriteArrayList<CallbackAndObject> rateLimitCallbacks;

    private RateLimit() {
        handler = new Handler();
        okToRun = new AtomicBoolean(false);
        rateLimitCallbacks = new CopyOnWriteArrayList<>();
        rateLimitRequest = new Runnable() {
            @Override
            public void run() {
                okToRun.set(true);
                // Don't hold lock for runIfOk, because don't hold lock during callback
                runIfOk();
                handler.postDelayed(this, rateLimitMillis);
            }
        };
        handler.postDelayed(rateLimitRequest, rateLimitMillis);
    }

    // Don't hold lock for runIfOk, because don't hold lock during callback
    protected void runIfOk() {
        if (!rateLimitCallbacks.isEmpty() ) {
            if (okToRun.compareAndSet(true, false)) {
                CallbackAndObject head = rateLimitCallbacks.remove(0);
                // Do callback without holding the lock because we don't know how long
                // it will take, and we are done protecting okToRun and rateLimitCallbacks
                head.rateLimitCallback.rateLimitReady(head.rateLimitObject);
            }
        }
    }

    // See https://en.wikipedia.org/wiki/Double-checked_locking
    // To understand this idiom
    // Also https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
    private static class RateLimitHolder {
        public static final RateLimit rateLimit = new RateLimit();
    }

    public static RateLimit getInstance() {
        return RateLimitHolder.rateLimit;
    }

    // Possibly called from an AsyncTask
    public void add(RateLimitCallback rateLimitCallback, RateLimitObject rateLimitObject) {
        CallbackAndObject callbackAndObject = new CallbackAndObject(rateLimitCallback, rateLimitObject);
        rateLimitCallbacks.addIfAbsent(callbackAndObject);
        // Call runIfOk because we might have waited rateLimitMillis
        runIfOk();
    }
}
