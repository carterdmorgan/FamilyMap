package carterdmorgan.com.familymap.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import carterdmorgan.com.familymap.PersonActivity;
import carterdmorgan.com.familymap.R;

public class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.ViewHolder>{

    private ArrayList<PersonActivity.FamilyContainer> containers;
    private Context context;

    public FamilyAdapter(ArrayList<PersonActivity.FamilyContainer> containers, Context context) {
        this.containers = containers;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPersonIcon;
        private TextView tvName;
        private TextView tvRelationship;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPersonIcon = itemView.findViewById(R.id.family_person_icon_image_view);
            tvName = itemView.findViewById(R.id.family_name_text_view);
            tvRelationship = itemView.findViewById(R.id.family_relationship_text_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.family_view_holder, viewGroup, false);
        FamilyAdapter.ViewHolder viewHolder = new FamilyAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final PersonActivity.FamilyContainer container = containers.get(i);
        viewHolder.tvRelationship.setText(container.getRelationship());
        viewHolder.tvName.setText(container.getPerson().getFirstName() + " " + container.getPerson().getLastName());
        viewHolder.ivPersonIcon
                .setImageDrawable(container.getPerson().getGender().equals("m") ?
                        context.getDrawable(R.drawable.ic_person_blue_400_24dp) :
                        context.getDrawable(R.drawable.ic_person_pink_100_24dp));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra("person", container.getPerson());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return containers.size();
    }
}
