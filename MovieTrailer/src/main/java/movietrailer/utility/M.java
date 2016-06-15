package movietrailer.utility;

import android.content.Context;
import android.widget.Toast;

public class M {
    public static void msg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void debug(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}