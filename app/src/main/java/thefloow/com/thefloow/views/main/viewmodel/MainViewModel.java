package thefloow.com.thefloow.views.main.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import thefloow.com.thefloow.model.local.JourneyModel;
import thefloow.com.thefloow.repositories.JourneyRepository;
import thefloow.com.thefloow.repositories.LocationRepository;

/**
 * Created by Augusto on 05/05/2018.
 */

public class MainViewModel extends ViewModel {

    private MutableLiveData<Location> currentLocationData = new MutableLiveData<>();
    private MutableLiveData<List<LatLng>> movementPoints = new MutableLiveData<>();
    private MutableLiveData<List<JourneyModel>> selectedJourney = new MutableLiveData<>();
    private MutableLiveData<Boolean> isTracking = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MutableLiveData<Location> getCurrentLocationData() {
        return currentLocationData;
    }
    public MutableLiveData<List<LatLng>> getMovementPoints(){
        return movementPoints;
    }
    public MutableLiveData<List<JourneyModel>> getSelectedJourney(){
        return selectedJourney;
    }
    public MutableLiveData<Boolean> getIsTracking(){
        return isTracking;
    }


    /**
     * Get All coordinates points of user movements
     */
    public void getPointsForLine(){

        compositeDisposable.add(LocationRepository.getInstance().getMovementPoints()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<LatLng>>() {
                    @Override
                    public void accept(List<LatLng> latLngs) throws Exception {
                        movementPoints.setValue(latLngs);

                    }
                }));

    }

    /**
     *Get user movement locations
     */
    public void getCurrentLocation(){

        compositeDisposable.add(LocationRepository.getInstance().getCurrentLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location currentLocation) throws Exception {
                        currentLocationData.setValue(currentLocation);
                    }
                }));

    }

    /**
     * Get specific Journey by ID
     * @param journeyID
     */
    public void getSelectedJourneys(String journeyID){

        compositeDisposable.add(JourneyRepository.getInstance().getJourneysById(journeyID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<JourneyModel>>() {
                    @Override
                    public void accept(List<JourneyModel> list) throws Exception {
                        selectedJourney.setValue(list);

                    }
                }));
    }

    public void doGetIsTracking(){

       compositeDisposable.add(JourneyRepository.getInstance().isTracking()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        isTracking.setValue(aBoolean);
                    }
                }));
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();

    }


}
