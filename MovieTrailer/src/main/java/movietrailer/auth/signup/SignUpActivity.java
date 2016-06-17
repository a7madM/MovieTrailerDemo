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

import movietrailer.utility.HttpConnector;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, HttpConnector.Callback {

    Button loginBtn;
    EditText first_name_Ed, last_name_Ed, username_Ed, email_Ed, passwd_Ed, confirm_passwd_Ed, phone_Ed;
    TextView msgTV;
    ProgressBar progressBar;


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

        first_name_Ed = (EditText) findViewById(R.id.first_name_Ed);
        last_name_Ed = (EditText) findViewById(R.id.last_name_Ed);
        username_Ed = (EditText) findViewById(R.id.username_Ed);
        email_Ed = (EditText) findViewById(R.id.email_Ed);
        passwd_Ed = (EditText) findViewById(R.id.passwd_Ed);
        confirm_passwd_Ed = (EditText) findViewById(R.id.confirm_passwd_Ed);
        phone_Ed = (EditText) findViewById(R.id.phone_Ed);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        msgTV = (TextView) findViewById(R.id.msgTV);

        loginBtn = (Button) findViewById(R.id.signUpBtn);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String firstName = first_name_Ed.getText().toString();
        String lastName = last_name_Ed.getText().toString();
        String username = username_Ed.getText().toString();
        String email = email_Ed.getText().toString();
        String password = passwd_Ed.getText().toString();
        String confirm_password = confirm_passwd_Ed.getText().toString();
        String phone = phone_Ed.getText().toString();

        if (!validParameters(firstName, lastName, username, email, password, confirm_password, phone)) {
            msgTV.setVisibility(View.VISIBLE);
            msgTV.setText("Fill All data.");
            return;
        }
        if (!password.equals(confirm_password)) {
            msgTV.setVisibility(View.VISIBLE);
            msgTV.setText("Password not matchs.");
            return;
        }
        StringBuilder input = new StringBuilder();

        input.append("api-register");
        input.append("?username=" + username);
        input.append("?email=" + email);
        input.append("&firstName=" + firstName);
        input.append("&lastName=" + lastName);
        input.append("&password=" + password);
        input.append("&phone=" + phone);

        HttpConnector httpConnector = new HttpConnector(this);
        httpConnector.execute(input.toString());
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


    private boolean validParameters(String firstName, String lastName, String username,
                                    String email, String password, String confirmPassword, String phone) {
        if (firstName == null || firstName.isEmpty() || lastName == null
                || firstName.isEmpty() || username == null || username.isEmpty() || email == null
                || email.isEmpty() || password == null || password.isEmpty()
                || confirmPassword == null || confirmPassword.isEmpty() || phone == null || phone.isEmpty())
            return false;
        return true;
    }

    private void startMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, Splash.class);
        startActivity(intent);
        finish();
    }
}