package movietrailer.screens.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import movietrailer.screens.adapters.SceneAdapter;
import movietrailer.screens.adapters.ViewHolderClickListener;
import movietrailer.screens.entities.Scene;
import movietrailer.utility.HttpConnector;
import movietrailer.utility.M;
import movietrailer.utility.VideoProcessor;

public class CreateTrailer extends Fragment implements View.OnClickListener, ViewHolderClickListener, VideoProcessor.Communicator, HttpConnector.Callback {

    private Button choose_subtitle_Btn, upload_subtitle_Btn, choose_movie_Btn, produce_Btn;
    private EditText subtitle_path_Ed, movie_path_Ed;
    private TextView progress_msg, progress_Tv;
    private ProgressBar producing_progress_bar;
    private VideoView trailer_video;

    // Recycler
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // layout
    private LinearLayout linearLayout;

    // Video Processing
    private VideoProcessor videoProcessor;
    private String start[] = {"00:01:00", "00:02:00", "00:03:00", "00:04:00"
            , "00:05:00", "00:06:00", "00:07:00", "00:08:00", "00:09:00", "00:10:00"};
    private int partNumber;
    private String movie_path;
    private String PARTS_DIRECTORY = "/sdcard/Movies/Parts/";

    private String LOG_TAG = CreateTrailer.class.getSimpleName();


    HttpConnector httpConnector;

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

        // layout 3
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        trailer_video = (VideoView) view.findViewById(R.id.trailer_video);


        // set View Action Listeners
        choose_subtitle_Btn.setOnClickListener(CreateTrailer.this);
        choose_movie_Btn.setOnClickListener(CreateTrailer.this);
        upload_subtitle_Btn.setOnClickListener(CreateTrailer.this);
        produce_Btn.setOnClickListener(CreateTrailer.this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project, container, false);
        initUI(view);
        setHasOptionsMenu(true);
        return view;
    }

    private void initCardView(List<Scene> sceneList) {
        producing_progress_bar.setVisibility(View.GONE);
        progress_Tv.setVisibility(View.GONE);
        progress_msg.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerAdapter = new SceneAdapter(getActivity(), sceneList, this);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onClickRecyclerView(View view, int position) {
        M.debug(getActivity(), "Clicked " + position);
    }

    private void doProcess(String startTime, int partNumber) {
        int duration = new Random().nextInt(3) + 3;
        Log.d(LOG_TAG, "Part " + partNumber + ", Duration " + duration);
        File directory = new File(PARTS_DIRECTORY);
        boolean exist = directory.exists();
        Log.d(LOG_TAG, "Exist " + exist);
        if (!exist) {
            boolean created = directory.mkdirs();
            Log.d(LOG_TAG, "Create " + created);
        }
        String command = "ffmpeg -y -i " + movie_path + " -strict experimental -ss " + startTime
                + " -codec copy -t " + duration + " " + PARTS_DIRECTORY + "part" + partNumber + ".mp4";
        //String commandConcat = "ffmpeg -f concat -i /sdcard/Movies/list.txt -c copy /sdcard/Movies/allParts.mp4";
        videoProcessor = new VideoProcessor(getActivity(), false, movie_path, CreateTrailer.this);
        videoProcessor.execute(command);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != 0) {
            Log.d("resultCode", "resultCode " + resultCode + " requestCode " + requestCode);
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            movie_path_Ed.setText(selectedImagePath);
        }
    }

    public String getPath(Uri contentUri) {
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
    public void onFinishedVideoProcessing(String status, boolean stat) {
        Log.d(LOG_TAG, "Finished " + partNumber + " With Status " + status);
        producing_progress_bar.setProgress(partNumber + 1);
        int percent = ((partNumber + 1) * 100) / start.length;
        Log.d(LOG_TAG, "Percent " + percent);
        progress_Tv.setText(percent + " %");
        partNumber++;
        if (partNumber < start.length / 2) {
            doProcess(start[partNumber], partNumber);
        } else if (!stat) {
            listFiles();
            String commandConcat = "ffmpeg -f concat -i storage/sdcard1/Movies/parts.txt -c copy /sdcard/Movies/allParts.mp4";
            videoProcessor = new VideoProcessor(getActivity(), true, "storage/sdcard1/Movies/parts.txt", CreateTrailer.this);
            videoProcessor.execute(commandConcat);
        } else {
            File file = new File("/storage/sdcard1/Movies/parts.txt");
            boolean movieExist = file.exists();
            Log.d(LOG_TAG, "Movie Exist " + movieExist);
            trailer_video.setVideoPath("/sdcard/Movies/allParts.mp4");
            MediaController controller = new MediaController(getActivity());
            controller.setAnchorView(trailer_video);
            controller.setMediaPlayer(trailer_video);
            trailer_video.setMediaController(controller);
            trailer_video.start();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.choose_subtitle_Btn:
                browseFiles();
                break;

            case R.id.choose_movie_Btn:
                browseMedia();
                break;

            case R.id.upload_subtitle_Btn:
                Log.d(LOG_TAG, "Uploading..");
                String subtitle_path = subtitle_path_Ed.getText().toString();
                if (subtitle_path == null || subtitle_path.isEmpty()) {
                    M.msg(getActivity(), "Choose Your Subtitle Please..");
                    // return;
                }

                httpConnector = new HttpConnector(CreateTrailer.this);
                httpConnector.execute();
                break;

            case R.id.produce_Btn:
                produceTrailer();
                break;
        }
    }

    private void browseFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 2);
    }

    private void browseMedia() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 1);
    }

    private void listFiles() {
        File f = new File("/sdcard/Movies/Parts/");
        File file[] = f.listFiles();
        if (file == null) {
            Log.d(LOG_TAG, "Null");
            return;
        }
        Log.d(LOG_TAG, "Size: " + file.length);
        ArrayList<Scene> sceneList = new ArrayList<>();

        for (int i = 0; i < file.length; i++) {
            if (file[i].getName().endsWith(".mp4")) {
                Log.d(LOG_TAG, "FileName:" + file[i].getName());
                Scene scene = new Scene();
                scene.setName(file[i].getName());
                sceneList.add(scene);
            }
        }
        initCardView(sceneList);
    }

    private void produceTrailer() {
        movie_path = movie_path_Ed.getText().toString();
        if (movie_path == null || movie_path.isEmpty()) {
            M.msg(getActivity(), "Choose Your Movie File");
            return;
        }
        partNumber = 0;
        producing_progress_bar.setVisibility(View.VISIBLE);
        progress_Tv.setVisibility(View.VISIBLE);
        progress_msg.setVisibility(View.VISIBLE);
        producing_progress_bar.setProgress(partNumber + 1);
        int percent = ((partNumber + 1) * 100) / start.length;
        Log.d(LOG_TAG, "Percent " + percent);
        progress_Tv.setText(percent + " %");
        doProcess(start[partNumber], partNumber);
    }

    @Override
    public void preExecute() {

    }

    @Override
    public void postExecute(String result) {
        // Parse Result here..
        Log.d(LOG_TAG, "Finish Uploading..");
        linearLayout = (LinearLayout) getActivity().findViewById(R.id.layout1);
        linearLayout.setVisibility(View.GONE);
        linearLayout = (LinearLayout) getActivity().findViewById(R.id.layout2);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout = (LinearLayout) getActivity().findViewById(R.id.layout3);
        producing_progress_bar.setMax(start.length);
    }
}