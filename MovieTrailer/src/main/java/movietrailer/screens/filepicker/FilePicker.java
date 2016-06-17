package movietrailer.screens.filepicker;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.movietrailer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilePicker extends ListActivity {

    private File currentDir;
    FileArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File("/storage/sdcard/");
        fill(currentDir);
    }

    private void fill(File file) {
        File[] dirs = file.listFiles();
        this.setTitle("Current Dir: " + file.getName());
        List<Option> dir = new ArrayList<>();
        List<Option> fls = new ArrayList<>();
        try {
            for (File ff : dirs) {
                if (ff.isDirectory())
                    dir.add(new Option(ff.getName(), "Folder", ff.getAbsolutePath()));
                else {
                    fls.add(new Option(ff.getName(), "File Size: " + ff.length(), ff.getAbsolutePath()));
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!file.getName().equalsIgnoreCase("sdcard"))
            dir.add(0, new Option("..", "Parent Directory", file.getParent()));
        adapter = new FileArrayAdapter(FilePicker.this, R.layout.file_view, dir);
        this.setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if (o.getData().equalsIgnoreCase("folder") || o.getData().equalsIgnoreCase("parent directory")) {
            currentDir = new File(o.getPath());
            fill(currentDir);
        } else {
            onFileClick(o);
        }
    }

    private void onFileClick(Option o) {
        Toast.makeText(this, "File Clicked: " + o.getName(), Toast.LENGTH_SHORT).show();
    }

}