package carterdmorgan.com.familymap.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import carterdmorgan.com.familymap.EventActivity;
import carterdmorgan.com.familymap.R;
import carterdmorgan.com.familymap.containers.LifeEventsContainer;

public class LifeEventsAdapter extends RecyclerView.Adapter<LifeEventsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LifeEventsContainer> containers;

    public LifeEventsAdapter(Context context, ArrayList<LifeEventsContainer> containers) {
        this.context = context;
        this.containers = containers;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEventTitle;
        private TextView tvPersonName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEventTitle = itemView.findViewById(R.id.life_event_title_text_view);
            tvPersonName = itemView.findViewById(R.id.life_event_person_name_text_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.life_event_view_holder, viewGroup, false);
        LifeEventsAdapter.ViewHolder viewHolder = new LifeEventsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final LifeEventsContainer container = containers.get(i);
        String eventTitle = String.format("%s: %s, %s (%d)", container.getEvent().getEventType(),
                container.getEvent().getCity(), container.getEvent().getCountry(),
                container.getEvent().getYear());

        viewHolder.tvEventTitle.setText(eventTitle);
        viewHolder.tvPersonName.setText(container.getPersonName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("event", container.getEvent());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return containers.size();
    }
}
