package tud.cnlab.wifriends;

import android.app.Application;
import android.content.Context;

/**
 * Created by Hariharan Gandhi, DSS Master Student, TU Darmstadt on "12/21/2014"
 * for the project "WiFriends"
 */
public class WiFriendsApplication extends Application {

    private static Context context;

    public static Context getAppContext() {
        return WiFriendsApplication.context;
    }

    public void onCreate() {
        super.onCreate();
        WiFriendsApplication.context = getApplicationContext();
    }
}
