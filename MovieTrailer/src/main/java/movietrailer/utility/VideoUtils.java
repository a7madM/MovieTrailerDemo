package movietrailer.utility;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

/**
 * Created by a7medM on 4/11/2016.
 */
public class VideoUtils {

    FFmpeg ffmpeg;
    Context context;
    private String TAG = VideoUtils.class.getSimpleName();

    Communicator communicator;
    boolean status = false;
    private String Log_Tag = VideoUtils.class.getSimpleName();

    private void init(Context context, boolean status, Communicator communicator) throws Exception {
        this.context = context;
        this.communicator = communicator;
        this.status = status;
    }

    public VideoUtils(Context context, boolean status, Communicator communicator) {
        try {
            init(context, status, communicator);
        } catch (Exception e) {
            Log.e(Log_Tag, "Error " + e);
        }
        try {
            ffmpeg = FFmpeg.getInstance(context);
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {

                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            ffmpeg = null;
        }
    }

    public interface Communicator {
        public void onFinishedVideoProcessing(boolean status);
    }

    public void execFFmpegBinary(final String command) throws Exception {
        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
            @Override
            public void onFailure(String s) {
                Log.e(TAG, "FAILED " + s);
            }

            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "SUCCESS");
            }

            @Override
            public void onProgress(String s) {
                if (status)
                    Log.d(TAG, "progress : " + s);
            }

            @Override
            public void onStart() {
                Log.d(TAG, "Started command " + command);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Finished command");
                if (communicator != null)
                    communicator.onFinishedVideoProcessing(status);
            }
        });
    }
}