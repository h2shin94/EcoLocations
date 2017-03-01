package uk.ac.cam.cl.foxtrot.ecolocations;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class EcoLocationsAdapter extends RecyclerView.Adapter<EcoLocationsAdapter.ViewHolder> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_location, parent, false);
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.8);
        v.setLayoutParams(layoutParams);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EcoLocation loc = EcoLocationManager.getEcoLocationsList().get(position);

        holder.parentView.setOnClickListener((v) -> {
            Intent intent = new Intent(v.getContext(), EcoLocationDetailsActivity.class);
            intent.putExtra("EcoLocation", loc);
            v.getContext().startActivity(intent);
        });

        holder.locationName.setText(loc.getNAME());
        Picasso.with(holder.locationImage.getContext())
                .load(String.format("https://protectedplanet.net/assets/tiles/%s?type=protected_area", loc.getWDPA_PID()))
                .into(holder.locationImage);

        holder.locationArea.setText(String.format(Locale.getDefault(),
                "%f", loc.getGIS_AREA() + loc.getGIS_M_AREA()));

        holder.locationCover.setText(loc.getLANDCOVER());
        holder.locationDesignation.setText(loc.getDESIG_ENG());
    }

    @Override
    public int getItemCount() {
        return EcoLocationManager.getEcoLocationsList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView locationImage;
        public TextView locationName;
        public TextView locationDesignation;
        public TextView locationArea;
        public TextView locationCover;
        public View parentView;

        public ViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            locationImage = (ImageView) itemView.findViewById(R.id.location_image);
            locationName = (TextView) itemView.findViewById(R.id.location_name);
            locationDesignation = (TextView) itemView.findViewById(R.id.location_designation);
            locationArea = (TextView) itemView.findViewById(R.id.location_area);
            locationCover = (TextView) itemView.findViewById(R.id.location_cover);
        }
    }
}
