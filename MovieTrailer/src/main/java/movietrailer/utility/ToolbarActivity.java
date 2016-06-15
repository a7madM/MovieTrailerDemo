package movietrailer.utility;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.movietrailer.R;


public class ToolbarActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        try {
            setupActionBar();
        } catch (Exception e) {
            Log.e("Toolbar","E" + e);
        }
    }

    private void setupActionBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

}