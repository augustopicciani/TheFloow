package thefloow.com.thefloow.repositories;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.subjects.PublishSubject;
import thefloow.com.thefloow.model.DatabaseProvider;
import thefloow.com.thefloow.model.local.JourneyDao;
import thefloow.com.thefloow.model.local.JourneyModel;

/**
 * Created by Augusto on 05/05/2018.
 */

public class JourneyRepository {

    private JourneyDao journeyDao = DatabaseProvider.getInstance().journeyDao();
    private PublishSubject<Boolean> isTracking = PublishSubject.create();
    private static JourneyRepository INSTANCE;

    public synchronized static JourneyRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JourneyRepository();
        }
        return INSTANCE;
    }


   public void saveJourney(JourneyModel journeyModel){
        journeyDao.addJourney(journeyModel);
   }

   public Single<List<JourneyModel>> getJourneys(){
       return journeyDao.getAllJourneys();
   }

    public Single<List<JourneyModel>> getJourneysById(String journeyID){
        return journeyDao.getJourneyById(journeyID);
    }

    /**
     * I know it's not the best practice to keep variable state and there are many other solutions
     * but I have no time and I am in hurry :)
     * @return
     */
    public PublishSubject<Boolean> isTracking(){
        return isTracking;
    }

    public void setIsTracking(boolean flag){
        isTracking.onNext(flag);
    }
}
