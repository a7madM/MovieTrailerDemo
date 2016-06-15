package movietrailer.auth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by elmoparmeg on 1/28/2016.
 */
public class CurrentUser {
    private String preference_name = "prefs";
    private String isAuthenticated = "authenticated";
    private String userEmail = "email";
    private String session_token = "session_token";

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public CurrentUser(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(preference_name,
                Context.MODE_PRIVATE);
        editor = context.getSharedPreferences(preference_name, Context.MODE_PRIVATE)
                .edit();
    }

    public void authenticated(boolean isAuthenticated) {
        editor.putBoolean(this.isAuthenticated, isAuthenticated);
        editor.apply();
    }

    public boolean isAuthenticated() {
        return sharedPreferences.getBoolean(isAuthenticated, false);
    }

    public void setSession_token(String session_token) {
        editor.putString(this.session_token, session_token);
        editor.apply();
    }


    public String getSession_token() {
        return sharedPreferences.getString(session_token, null);
    }

    public void logOut() {
        editor.clear();
        editor.commit();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(userEmail, "");
    }

    public void setUserEmail(String userEmail) {
        editor.putString(this.userEmail, userEmail);
        editor.apply();
    }
}