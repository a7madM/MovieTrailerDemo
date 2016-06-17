package movietrailer.screens.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.movietrailer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import movietrailer.utility.M;
import movietrailer.utility.ToolbarActivity;

public class FilePicker extends ToolbarActivity {

    private File currentDir;
    FileAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        listView = (ListView) findViewById(R.id.listView);
        currentDir = new File("storage/sdcard1/");
        fill(currentDir);
    }

    private void fill(File file) {
        if (file == null)
            finish();
        File[] dirs = file.listFiles();
        this.setTitle(file.getName());
        List<FileOption> dir = new ArrayList<>();
        List<FileOption> fls = new ArrayList<>();
        try {
            for (File ff : dirs) {
                if (ff.isDirectory())
                    dir.add(new FileOption(ff.getName(), "Folder", ff.getAbsolutePath()));
                else {
                    fls.add(new FileOption(ff.getName(), "File Size: " + ff.length(), ff.getAbsolutePath()));
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!file.getName().equalsIgnoreCase("sdcard"))
            dir.add(0, new FileOption("..", "Parent Directory", file.getParent()));
        adapter = new FileAdapter(FilePicker.this, R.layout.file_view, dir);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileOption o = adapter.getItem(position);
                if (o.getData().equalsIgnoreCase("folder") || o.getData().equalsIgnoreCase("parent directory")) {
                    currentDir = new File(o.getPath());
                    fill(currentDir);
                } else {
                    onFileClick(o);
                }
            }
        });

    }

    private void onFileClick(FileOption o) {
        M.msg(FilePicker.this, "Chosen File: " + o.getPath());
        Intent returnIntent = new Intent();
        returnIntent.putExtra("filePath",o.getPath());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        try {
            currentDir = new File(currentDir.getParent());
            fill(currentDir);
        } catch (Exception e) {
            finish();
        }
    }
}