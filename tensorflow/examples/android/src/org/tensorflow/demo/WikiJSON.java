package org.tensorflow.demo;

/**
 * Created by vignesh on 4/2/18.
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WikiJSON {
    public interface IWikiJSON {
        void fetchStart();
        void fetchComplete(WikiItem wikiItem);
        void fetchCancel(String url);
    }
    // See https://github.com/reddit/reddit/wiki/JSON
    // XXX Write me.  Note, static method.
    public static WikiItem jsonToWikiItem(JSONObject jO) {
        WikiItem wi = new WikiItem();
        try {
                JSONObject item = jO.getJSONObject("pages");
                System.out.println(item);
                Iterator<String> keys = item.keys();
                String key = keys.next();
                JSONObject data = item.getJSONObject(key);

                if(data.has("extract")) {
                    String extract = data.getString("extract");
                    if(data.has("thumbnail")){
                        JSONObject thumbnail = data.getJSONObject("thumbnail");
                        String tUrl = thumbnail.getString("source");
                        URL thumbnailurl = new URL(tUrl);
                        wi.thumbnailURL = thumbnailurl;
                    }

                    wi.extract = extract;

                }

        }  catch (JSONException e) {
            // If an error occurs, this prints the error to the log
            e.printStackTrace();
        }
        catch (MalformedURLException me){
            me.printStackTrace();
        }
        return wi; // This is NOT what you should return
    }
}
