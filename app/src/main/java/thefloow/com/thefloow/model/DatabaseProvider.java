package thefloow.com.thefloow.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import thefloow.com.thefloow.constants.Constants;
import thefloow.com.thefloow.model.local.JourneyDao;
import thefloow.com.thefloow.model.local.JourneyModel;

/**
 * Created by Augusto on 05/05/2018.
 */

@Database(entities = {JourneyModel.class}, version = Constants.DB_VERSION)
public abstract class DatabaseProvider extends RoomDatabase {

    private static DatabaseProvider INSTANCE;

    public static DatabaseProvider getInstance() {

        return INSTANCE;
    }

    public static DatabaseProvider getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), DatabaseProvider.class, Constants.DB_NAME)
                            .build();
        }
        return INSTANCE;
    }

    public abstract JourneyDao journeyDao();

}
