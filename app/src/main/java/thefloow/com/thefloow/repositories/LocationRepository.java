package thefloow.com.thefloow.repositories;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by Augusto on 05/05/2018.
 */

public class LocationRepository {

    private static LocationRepository INSTANCE;
    private PublishSubject<Location> currentLocation = PublishSubject.create();
    private PublishSubject<List<LatLng>> movementPoints = PublishSubject.create();

    public static synchronized LocationRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocationRepository();
        }
        return INSTANCE;
    }


    public Observable<Location> getCurrentLocation(){
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation){
        this.currentLocation.onNext(currentLocation);
    }

    public Observable<List<LatLng>> getMovementPoints(){
        return movementPoints;
    }

    public void setPoints(List<LatLng> points){
        movementPoints.onNext(points);
    }


}
