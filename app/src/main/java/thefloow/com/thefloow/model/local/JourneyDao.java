package thefloow.com.thefloow.model.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import io.reactivex.Single;
import thefloow.com.thefloow.model.DateConverter;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Augusto on 05/05/2018.
 */

@Dao
@TypeConverters(DateConverter.class)
public interface JourneyDao {

    //@Query("select * from JourneyModel")
    @Query("SELECT * FROM JourneyModel GROUP BY journeyID ORDER BY id DESC" )
    Single<List<JourneyModel>> getAllJourneys();

    @Query("SELECT * FROM JourneyModel WHERE journeyID = :journeyId ORDER BY id ASC")
    Single<List<JourneyModel>> getJourneyById(String journeyId);

    @Insert(onConflict = REPLACE)
    void addJourney(JourneyModel journeyModel);

    @Delete
    void deleteJourney(JourneyModel journeyModel);

}
