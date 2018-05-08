package thefloow.com.thefloow.model;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Augusto on 05/05/2018.
 */

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}