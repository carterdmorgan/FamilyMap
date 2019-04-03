package carterdmorgan.com.familymap.data;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import carterdmorgan.com.familymap.R;

import static carterdmorgan.com.familymap.MapsFragment.TAG;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder>{

    private ArrayList<String> eventTypes;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvSubtitle;
        private Switch switchFilter;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.filter_view_holder_title_text_view);
            tvSubtitle = itemView.findViewById(R.id.filter_view_holder_subtitle_text_view);
            switchFilter = itemView.findViewById(R.id.filter_view_holder_switch);
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public void setTvTitle(TextView tvTitle) {
            this.tvTitle = tvTitle;
        }

        public TextView getTvSubtitle() {
            return tvSubtitle;
        }

        public void setTvSubtitle(TextView tvSubtitle) {
            this.tvSubtitle = tvSubtitle;
        }

        public Switch getSwitchFilter() {
            return switchFilter;
        }

        public void setSwitchFilter(Switch switchFilter) {
            this.switchFilter = switchFilter;
        }
    }

    public FilterAdapter(ArrayList<String> eventTypes) {
        Log.d(TAG, "FilterAdapter: eventTypes: " + eventTypes.toString());
        this.eventTypes = eventTypes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_view_holder, viewGroup, false);
       ViewHolder viewHolder = new ViewHolder(view);
       return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String eventType = this.eventTypes.get(i);
        String eventTypeLower = eventType.toLowerCase();
        String[] individualWords = eventTypeLower.split(" ");
        String title = "";

        for (String word : individualWords) {
            word = word.substring(0, 1).toUpperCase() + word.substring(1);
            title += word;
            title += " ";
        }

        title = title.substring(0, title.length() - 1);

        viewHolder.getTvTitle().setText(title);
        viewHolder.getTvSubtitle().setText(String.format("FILTER BY %s EVENTS", eventType.toUpperCase()));
        viewHolder.getSwitchFilter().setChecked(UserDataStore.getInstance().getFilterPreferences().get(eventType));

        viewHolder.getSwitchFilter().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserDataStore.getInstance().getFilterPreferences().put(eventType, isChecked);
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventTypes.size();
    }

}
