package movietrailer.utility;

import android.os.AsyncTask;
import android.util.Log;

import com.movietrailer.BuildConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by a7medM on 6/17/2016.
 */
public class UploadToServer extends AsyncTask<String, Void, String> {

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


    private int serverResponseCode = 0;


    @Override
    protected String doInBackground(String... params) {
        try {
            String sourceFileUri = params[0];
            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            String upLoadServerUri = BuildConfig.Link_Header_WebService + "api-upload";

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            // conn.setRequestProperty("uploaded_file", sourceFileUri);
            conn.setRequestProperty("subtitle", sourceFileUri);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"subtitle\";filename=\""
                    + sourceFileUri + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math
                        .min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0,
                        bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);

            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn
                    .getResponseMessage();
            Log.d(LOG_TAG, "Response " + serverResponseMessage + " : " + serverResponseCode);
            if (serverResponseCode == 200) {
                Log.d(LOG_TAG, "Success");
            }
            InputStream inputStream = conn.getInputStream();
            StringBuffer buffered = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffered.append(line + "\n");
            }
            if (buffered.length() == 0) {
                return null;
            }
            String jsonStr = buffered.toString();
            Log.d(LOG_TAG, jsonStr);

            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error " + e);
        }

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(LOG_TAG, "" + result);
        if (callback != null)
            callback.postExecute(result);
    }
}