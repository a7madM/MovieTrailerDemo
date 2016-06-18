package movietrailer.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.movietrailer.R;

import org.json.JSONObject;

import movietrailer.auth.CurrentUser;
import movietrailer.auth.signup.SignUpActivity;
import movietrailer.screens.mainscreen.MainScreen;
import movietrailer.utility.HttpConnector;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, HttpConnector.Callback {

    Button loginBtn, registerBtn;
    EditText email_Ed, passwd_Ed;
    TextView msgTV;
    ProgressBar progressBar;
    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        init();
    }

    private void init() {
        msgTV = (TextView) findViewById(R.id.msgTV);
        email_Ed = (EditText) findViewById(R.id.email_Ed);
        passwd_Ed = (EditText) findViewById(R.id.passwd_Ed);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.loginBtn:
                String email = email_Ed.getText().toString();
                String password = passwd_Ed.getText().toString();
                if (!validParameters(email, password)) {
                    msgTV.setVisibility(View.VISIBLE);
                    msgTV.setText("Type Email and Password");
                    return;
                }
                HttpConnector httpConnector = new HttpConnector(this);
                StringBuilder input = new StringBuilder();
                input.append("api-login");
                input.append("?email=" + email);
                input.append("&password=" + password);
                httpConnector.execute(input.toString());
                break;

            case R.id.registerBtn:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void preExecute() {
        msgTV.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.GONE);
    }

    @Override
    public void postExecute(String userObject) {
        progressBar.setVisibility(View.GONE);
        loginBtn.setVisibility(View.VISIBLE);
        try {
            Log.d(LOG_TAG, "Result " + userObject);
            JSONObject jsonObject = new JSONObject(userObject);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                String token = jsonObject.getString("token");
                CurrentUser currentUser = new CurrentUser(LoginActivity.this);
                currentUser.authenticated(true);
                currentUser.setSession_token(token);
                startMainActivity();
            } else {
                msgTV.setVisibility(View.VISIBLE);
                msgTV.setText("Login Failed, Try again..");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Post Execute " + e);
        }
    }


    private boolean validParameters(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty())
            return false;
        return true;
    }


    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainScreen.class);
        startActivity(intent);
        finish();
    }

}