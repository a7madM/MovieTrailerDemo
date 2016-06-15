package movietrailer.auth.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.movietrailer.R;
import com.movietrailer.Splash;

import movietrailer.auth.CurrentUser;
import movietrailer.utility.HttpConnector;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, HttpConnector.Callback {

    Button loginBtn;
    EditText userEmail_Ed, passwd_Ed;
    TextView msgTV;
    ProgressBar progressBar;
    String email;
    CurrentUser currentUser;
    private final String LOG_TAG = SignUpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        init();
    }

    private void init() {
        msgTV = (TextView) findViewById(R.id.msgTV);
        userEmail_Ed = (EditText) findViewById(R.id.userEmail_Ed);
        passwd_Ed = (EditText) findViewById(R.id.passwd_Ed);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        email = userEmail_Ed.getText().toString();
        String password = passwd_Ed.getText().toString();
        if (!validParameters(email, password)) {
            msgTV.setVisibility(View.VISIBLE);
            msgTV.setText("Type Email and Password");
            //  return;
        }
        HttpConnector loginTask = new HttpConnector(this);
        loginTask.execute();
    }

    @Override
    public void preExecute() {
        msgTV.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.GONE);
    }

    @Override
    public void postExecute(String userObject) {
        try {
            startMainActivity();
        } catch (Exception e) {

        }
    }


    private boolean validParameters(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty())
            return false;
        return true;
    }

    private void startMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, Splash.class);
        startActivity(intent);
        finish();
    }
}
