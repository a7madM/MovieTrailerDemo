package movietrailer.screens.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.movietrailer.R;

import movietrailer.auth.CurrentUser;
import movietrailer.auth.login.LoginActivity;
import movietrailer.utility.ToolbarActivity;

public class MainScreen extends ToolbarActivity {

    private String LOG_TAG = MainScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.action_faqs:
                break;

            case R.id.action_contact:
                break;

            case R.id.action_logout:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        CurrentUser currentUser = new CurrentUser(MainScreen.this);
        currentUser.logOut();
        Intent intent = new Intent(MainScreen.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}