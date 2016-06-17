package movietrailer.utility;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by a7medM on 6/17/2016.
 */
public class UploadToServer extends AsyncTask<String, Void, String> {

    int serverResponseCode = 0;
    private String LOG_TAG = UploadToServer.class.getSimpleName();

    Callback callback;

    public interface Callback {
        public void preExecute();

        public void postExecute(String userObject);

    }

    public UploadToServer(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null)
            callback.preExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        return "";
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(LOG_TAG, "Finished");
        if (callback != null)
            callback.postExecute(result);
    }
}