package thefloow.com.thefloow.views.journeys.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import thefloow.com.thefloow.R;
import thefloow.com.thefloow.model.local.JourneyModel;
import thefloow.com.thefloow.util.Utils;

/**
 * Created by Augusto on 07/05/2018.
 */

public class JourneysAdapter extends RecyclerView.Adapter<JourneysAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(JourneyModel item);
    }

    private List<JourneyModel> data;
    private OnItemClickListener listener;

    public JourneysAdapter(OnItemClickListener listener){
        this.data = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_journey_layout, parent, false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class VH extends RecyclerView.ViewHolder {


        private final TextView journeyID, startDate;

        public VH(View itemView) {
            super(itemView);
            //rowId = (TextView) itemView.findViewById(R.id.tv_id);
            journeyID = (TextView) itemView.findViewById(R.id.tv_journey_id);
            startDate = (TextView) itemView.findViewById(R.id.tv_journey_date);
        }

        public void bind(final JourneyModel item, final OnItemClickListener listener){

            //rowId.setText(item.getId());
            journeyID.setText(item.getJourneyID());
            startDate.setText(Utils.getStringDate(item.getJourneyDate()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public void updateDataset(List<JourneyModel> list){
        data = list;
        notifyDataSetChanged();
    }
}
