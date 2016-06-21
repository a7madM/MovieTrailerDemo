package movietrailer.screens.mainscreen.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.movietrailer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import movietrailer.screens.filepicker.FilePicker;
import movietrailer.screens.mainscreen.adapters.ViewHolderClickListener;
import movietrailer.screens.mainscreen.entities.Scene;
import movietrailer.utility.M;
import movietrailer.utility.UploadToServer;
import movietrailer.utility.VideoUtils;

public class CreateTrailer extends Fragment implements View.OnClickListener, ViewHolderClickListener, VideoUtils.Communicator, UploadToServer.Callback {

    // layout
    private LinearLayout linearLayout;

    private Button choose_subtitle_Btn, upload_subtitle_Btn, choose_movie_Btn, produce_Btn;
    private EditText subtitle_path_Ed, movie_path_Ed;
    private TextView progress_msg, progress_Tv;
    private ProgressBar producing_progress_bar, upload_progress;
    private VideoView trailer_video;
    ArrayList<Scene> sceneList;

    // Video Processing
    private VideoUtils videoUtils;
    private int partNumber;
    private String movie_path;
    private String PARTS_DIRECTORY = "/sdcard/Movies/Parts2/";

    private String LOG_TAG = CreateTrailer.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project, container, false);
        initUI(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onClickRecyclerView(View view, int position) {
        M.debug(getActivity(), "Clicked " + position);
    }

    private void ffmpegProcess(String startTime, int partNumber) throws Exception {
        Log.d(LOG_TAG, "Part " + partNumber + ", Duration " + sceneList.get(partNumber).getDuration());
        File directory = new File(PARTS_DIRECTORY);
        boolean exist = directory.exists();
        Log.d(LOG_TAG, "Exist " + exist);
        if (!exist) {
            boolean created = directory.mkdirs();
            Log.d(LOG_TAG, "Create " + created);
        }

//        String command = " -y -i " + movie_path + " -ss " + startTime + " -strict experimental"
        //              + " -codec copy -t " + duration + " " + PARTS_DIRECTORY + "part" + partNumber + ".avi";

        String command = " -y -i " + movie_path + " -ss " + startTime + " -strict experimental "
                + " -codec copy -t " + sceneList.get(partNumber).getDuration() + " " + PARTS_DIRECTORY + "part" + partNumber + ".mkv";

        videoUtils = new VideoUtils(getActivity(), false, CreateTrailer.this);
        videoUtils.execFFmpegBinary(command);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "Request Code " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = null;
            try {
                selectedImagePath = getPath(selectedImageUri);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception Get URI " + e);
            }
            movie_path_Ed.setText(selectedImagePath);
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra("filePath");
            subtitle_path_Ed.setText(filePath);
        }
    }

    public String getPath(Uri contentUri) throws Exception {
        String res = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onFinishedVideoProcessing(boolean status) {
        Log.d(LOG_TAG, "Status " + status);
        if (!status) {
            producing_progress_bar.setProgress(partNumber + 1);
            int percent = ((partNumber + 1) * 100) / sceneList.size();
            progress_Tv.setText(percent + " %");
            partNumber++;
            if (partNumber < sceneList.size()) {
                try {
                    ffmpegProcess(sceneList.get(partNumber).getStart(), partNumber);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Process in Part  " + partNumber + e);
                }
            } else {
                try {
                    // listFiles();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "listFile " + e);
                }

                String commandConcat = " -f concat -i /storage/sdcard1/Movies/parts.txt -c copy /sdcard/Movies/movietrailer2.mkv";
                videoUtils = new VideoUtils(getActivity(), true, CreateTrailer.this);
                try {
                    videoUtils.execFFmpegBinary(commandConcat);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "E " + e);
                }
            }
        } else {
            try {
                initVideoPlayer();
            } catch (Exception e) {
                Log.e(LOG_TAG, "iniVideoPlayer " + e);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.choose_subtitle_Btn:
                try {
                    browseFiles();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "browseFiles " + e);
                }
                break;
            case R.id.choose_movie_Btn:
                try {
                    browseMedia();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "browseMedia " + e);
                }
                break;

            case R.id.upload_subtitle_Btn:
                Log.d(LOG_TAG, "Uploading..");
                String subtitle_path = subtitle_path_Ed.getText().toString();
                if (subtitle_path == null || subtitle_path.isEmpty()) {
                    M.msg(getActivity(), "Choose Your Subtitle Please..");
                    //return;
                }

                UploadToServer uploadToServer = new UploadToServer(CreateTrailer.this);
                uploadToServer.execute(subtitle_path);
                // HttpConnector httpConnector = new HttpConnector(CreateTrailer.this);
                //httpConnector.execute("api-upload");
                break;

            case R.id.produce_Btn:
                try {
                    produceTrailer();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "produceTrailer " + e);
                }
                break;
        }
    }

    @Override
    public void preExecute() {
        Log.d(LOG_TAG, "Pre Execute");
        upload_progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(String result) {
        // Parse Result here..
        // Log.d(LOG_TAG, "Finish Uploading.." + result);

        upload_progress.setVisibility(View.GONE);
        result = "[{\"start\":\"00:56:22,520\",\"end\":\"00:56:25,364\"},{\"start\":\"00:56:25,440\",\"end\":\"00:56:28,046\"},{\"start\":\"00:56:28,120\",\"end\":\"00:56:29,565\"},{\"start\":\"01:02:46,320\",\"end\":\"01:02:49,051\"},{\"start\":\"01:02:49,240\",\"end\":\"01:02:50,526\"},{\"start\":\"01:02:50,680\",\"end\":\"01:02:55,049\"},{\"start\":\"01:01:43,360\",\"end\":\"01:01:44,960\"},{\"start\":\"01:01:45,000\",\"end\":\"01:01:46,440\"},{\"start\":\"01:01:46,480\",\"end\":\"01:01:48,130\"},{\"start\":\"01:01:49,800\",\"end\":\"01:01:52,963\"},{\"start\":\"01:01:53,280\",\"end\":\"01:01:56,124\"},{\"start\":\"00:08:47,440\",\"end\":\"00:08:50,842\"},{\"start\":\"00:08:51,000\",\"end\":\"00:08:54,129\"}]";
        try {
            JSONArray jsonArray = new JSONArray(result);
            sceneList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Scene scene = new Scene(jsonObject);
                sceneList.add(scene);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error " + e);
        }

        linearLayout = (LinearLayout) getActivity().findViewById(R.id.layout1);
        linearLayout.setVisibility(View.GONE);
        linearLayout = (LinearLayout) getActivity().findViewById(R.id.layout2);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void browseFiles() throws Exception {
        Intent intent = new Intent(getActivity(), FilePicker.class);
        startActivityForResult(intent, 2);
    }

    private void browseMedia() throws Exception {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 1);
    }

    private void listFiles() throws Exception {
        File f = new File("/sdcard/Movies/Parts/");
        File file[] = f.listFiles();
        if (file == null) {
            Log.d(LOG_TAG, "Null");
            return;
        }
        Log.d(LOG_TAG, "Size: " + file.length);

        for (int i = 0; i < file.length; i++) {
            if (file[i].getName().endsWith(".avi")) {
                Log.d(LOG_TAG, "FileName:" + file[i].getName());
            }
        }
    }

    private void produceTrailer() throws Exception {
        movie_path = movie_path_Ed.getText().toString();
        if (movie_path == null || movie_path.isEmpty()) {
            M.msg(getActivity(), "Choose Your Movie File");
            return;
        }
        Log.d(LOG_TAG, "Scene List" + sceneList.size());
        for (int i = 0; i < sceneList.size() - 1; i++) {
            Scene scene1 = sceneList.get(i);
            Scene scene2 = sceneList.get(i + 1);
            if (scene1.getHours().equals(scene2.getHours())
                    && scene1.getMin().equals(scene2.getMin())) {
                sceneList.remove(i + 1);
            }
        }
        producing_progress_bar.setMax(sceneList.size());
        Log.d(LOG_TAG, "Scene List" + sceneList.size());
        partNumber = 0;
        producing_progress_bar.setVisibility(View.VISIBLE);
        progress_Tv.setVisibility(View.VISIBLE);
        progress_msg.setVisibility(View.VISIBLE);
        producing_progress_bar.setProgress(partNumber + 1);
        int percent = ((partNumber + 1) * 100) / sceneList.size();
        Log.d(LOG_TAG, "Percent " + percent);
        progress_Tv.setText(percent + " %");
        ffmpegProcess(sceneList.get(partNumber).getStart(), partNumber);
    }

    private void initUI(View view) {
        choose_subtitle_Btn = (Button) view.findViewById(R.id.choose_subtitle_Btn);
        choose_movie_Btn = (Button) view.findViewById(R.id.choose_movie_Btn);
        upload_subtitle_Btn = (Button) view.findViewById(R.id.upload_subtitle_Btn);
        produce_Btn = (Button) view.findViewById(R.id.produce_Btn);

        movie_path_Ed = (EditText) view.findViewById(R.id.movie_path_Ed);
        subtitle_path_Ed = (EditText) view.findViewById(R.id.subtitle_path_Ed);

        progress_Tv = (TextView) view.findViewById(R.id.progress_Tv);
        progress_msg = (TextView) view.findViewById(R.id.progress_msg);
        producing_progress_bar = (ProgressBar) view.findViewById(R.id.producing_progress_bar);
        upload_progress = (ProgressBar) view.findViewById(R.id.upload_progress);

        // layout 3
        trailer_video = (VideoView) view.findViewById(R.id.trailer_video);


        // set View Action Listeners
        choose_subtitle_Btn.setOnClickListener(CreateTrailer.this);
        choose_movie_Btn.setOnClickListener(CreateTrailer.this);
        upload_subtitle_Btn.setOnClickListener(CreateTrailer.this);
        produce_Btn.setOnClickListener(CreateTrailer.this);

    }

    private void initVideoPlayer() throws Exception {
        Log.d(LOG_TAG, "Init Video");
        MediaController controller = new MediaController(getActivity());
        controller.setAnchorView(trailer_video);
        controller.setMediaPlayer(trailer_video);
        trailer_video.setVisibility(View.VISIBLE);
        trailer_video.setMediaController(controller);
        trailer_video.setVideoPath("/sdcard/Movies/movietrailer2.mkv");
        trailer_video.start();
    }
}