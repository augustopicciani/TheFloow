package thefloow.com.thefloow.views.journeys.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import thefloow.com.thefloow.model.local.JourneyModel;
import thefloow.com.thefloow.repositories.JourneyRepository;

/**
 * Created by Augusto on 07/05/2018.
 */

public class JourneyViewModel extends ViewModel {

    private MutableLiveData<List<JourneyModel>> journeyList = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MutableLiveData<List<JourneyModel>> getJourneyList(){
        return journeyList;
    }



    public void getAllJourneys(){

        compositeDisposable.add(JourneyRepository.getInstance().getJourneys()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<JourneyModel>>() {
                    @Override
                    public void accept(List<JourneyModel> list) throws Exception {
                        journeyList.setValue(list);

                    }
                }));



    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();

    }
}
