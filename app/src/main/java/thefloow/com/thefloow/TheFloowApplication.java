package thefloow.com.thefloow;

import android.app.Application;

import thefloow.com.thefloow.model.DatabaseProvider;

/**
 * Created by Augusto on 05/05/2018.
 */

public class TheFloowApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseProvider.getDatabase(this);
    }
}
