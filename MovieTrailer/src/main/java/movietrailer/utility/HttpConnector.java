package movietrailer.utility;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by a7medM on 1/28/2016.
 */
public class HttpConnector extends AsyncTask<HashMap<String, String>, Void, String> {
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
    protected String doInBackground(HashMap... params) {
        if (params == null)
            return null;
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (callback != null)
            callback.postExecute(result);
    }
}