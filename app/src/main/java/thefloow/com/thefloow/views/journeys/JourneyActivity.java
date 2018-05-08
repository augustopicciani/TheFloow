package thefloow.com.thefloow.views.journeys;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import thefloow.com.thefloow.R;
import thefloow.com.thefloow.constants.Constants;
import thefloow.com.thefloow.model.local.JourneyModel;
import thefloow.com.thefloow.views.journeys.adapter.JourneysAdapter;
import thefloow.com.thefloow.views.journeys.viewmodel.JourneyViewModel;

/**
 * Created by Augusto on 07/05/2018.
 */

public class JourneyActivity extends AppCompatActivity implements JourneysAdapter.OnItemClickListener{

    private RecyclerView journeysList;
    private JourneysAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journeys);
        init();
    }

    private void init(){

        JourneyViewModel viewModel = ViewModelProviders.of(this).get(JourneyViewModel.class);
        journeysList = (RecyclerView) findViewById(R.id.rv_journeys_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        journeysList.setLayoutManager(mLayoutManager);
        journeysList.setItemAnimator(new DefaultItemAnimator());
        adapter = new JourneysAdapter(this);
        journeysList.setAdapter(adapter);
        viewModel.getJourneyList().observe(this, new Observer<List<JourneyModel>>() {
            @Override
            public void onChanged(@Nullable List<JourneyModel> list) {
                Log.d("test", "journeylist size: " + list.size());
                adapter.updateDataset(list);
            }
        });
        viewModel.getAllJourneys();
    }

    @Override
    public void onItemClick(JourneyModel item) {
        Log.d("test", "clicked: " + item.getJourneyID());
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.JOURNEY_ID_EXTRA, item.getJourneyID());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
