package movietrailer.utility;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.movietrailer.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by a7medM on 1/28/2016.
 */
public class HttpConnector extends AsyncTask<String, Void, String> {
    Callback callback;
    private final String LOG_TAG = HttpConnector.class.getSimpleName();

    public interface Callback {
        public void preExecute();

        public void postExecute(String userObject);

    }

    public HttpConnector(Callback callback) {
        this.callback = callback;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.callback != null) {
            callback.preExecute();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        if (params == null)
            return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr;
        Uri uri;
        URL url;

        try {
            if (params == null || params.length == 0)
                return null;

            StringBuilder BASE_URL = new StringBuilder();
            BASE_URL.append(BuildConfig.Link_Header_WebService + params[0]);
            uri = Uri.parse(BASE_URL.toString()).buildUpon()
                    .build();
            Log.d(LOG_TAG, uri.toString());
            url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            jsonStr = buffer.toString();
            Log.d(LOG_TAG, jsonStr);
            return jsonStr;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (callback != null)
            callback.postExecute(result);
    }
}