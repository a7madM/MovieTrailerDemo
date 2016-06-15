package movietrailer.utility;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.loader.LoadJNI;

public class VideoProcessor extends AsyncTask<String, Integer, Void> {

    Context context;
    private boolean commandValidationFailedFlag = false;
    String workFolder = null;
    String demoVideoFolder = null;
    String filePath = null;
    String vkLogPath = null;
    Communicator communicator;
    boolean status = false;
    private String Log_Tag = VideoProcessor.class.getSimpleName();
    String commandStr = "";

    public interface Communicator {
        public void onFinishedVideoProcessing(String status, boolean stat);
    }

    public VideoProcessor(Context context, boolean status, String filePath, Communicator communicator) {
        try {
            init(context, status, filePath, communicator);
        } catch (Exception e) {
            Log.d(Log_Tag, "" + e);
        }
    }

    private void init(Context context, boolean status, String filePath, Communicator communicator) throws Exception {
        this.context = context;
        this.communicator = communicator;
        this.status = status;
        workFolder = context.getFilesDir().getAbsolutePath() + "/";
        this.filePath = filePath;
        vkLogPath = workFolder + "vk.log";
    }


    @Override
    protected void onPreExecute() {

    }

    protected Void doInBackground(String... paths) {
        commandStr = paths[0];
        Log.d(Log_Tag, commandStr);
        if (!GeneralUtils.checkIfFileExistAndNotEmpty(filePath)) {
            Log.e(Log_Tag, filePath + " NotExist");
            return null;
        }
        // delete previous log
        GeneralUtils.deleteFileUtil(workFolder + "/vk.log");
        PowerManager powerManager = (PowerManager) context.getSystemService(Activity.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
        wakeLock.acquire();
        LoadJNI vk = new LoadJNI();
        try {
            vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder, context);
            GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);
        } catch (Throwable e) {
            Log.e(Log_Tag, "vk run exception.", e);
        } finally {
            if (wakeLock.isHeld())
                wakeLock.release();
            else {
                Log.i(Log_Tag, "Wake lock is already released, doing nothing");
            }
        }
        Log.i(Log_Tag, "doInBackground finished");
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i(Log_Tag, "onPostExecute");
        super.onPostExecute(result);
        String rc;
        if (commandValidationFailedFlag) {
            rc = "Command Validation Failed";
        } else {
            rc = GeneralUtils.getReturnCodeFromLog(vkLogPath);
        }
        final String status = rc;
        //Toast.makeText(context, status, Toast.LENGTH_LONG).show();

        if (communicator != null)
            communicator.onFinishedVideoProcessing(status, this.status);
    }
}