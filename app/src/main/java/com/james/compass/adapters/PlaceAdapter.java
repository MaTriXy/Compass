package com.james.compass.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.james.compass.Compass;
import com.james.compass.R;
import com.james.compass.data.AttributedPhoto;
import com.james.compass.data.PlaceData;
import com.james.compass.utils.PhotoTask;
import com.james.compass.views.CustomImageView;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private Compass compass;
    private Activity activity;
    private ArrayList<PlaceData> places;

    public PlaceAdapter(Activity activity, ArrayList<PlaceData> places) {
        this.activity = activity;
        this.places = places;
        compass = (Compass) activity.getApplicationContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_place, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PlaceData place = places.get(position);

        TextView title = (TextView) holder.v.findViewById(R.id.title);
        TextView subtitle = (TextView) holder.v.findViewById(R.id.subtitle);
        TextView address = (TextView) holder.v.findViewById(R.id.address);
        TextView number = (TextView) holder.v.findViewById(R.id.number);

        title.setText(place.name);
        subtitle.setText(place.description);

        address.setText(place.address);
        holder.v.findViewById(R.id.directions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceData place = places.get(holder.getAdapterPosition());

                String url = "http://maps.google.com/maps?daddr=" + place.latitude +"," + place.longitude;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                activity.startActivity(intent);
            }
        });

        number.setText(place.number);
        holder.v.findViewById(R.id.phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceData place = places.get(holder.getAdapterPosition());
                activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + place.number)));
            }
        });

        new PhotoTask(activity, 1600, 900) {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    ((CustomImageView) holder.v.findViewById(R.id.image)).transition(activity, attributedPhoto.bitmap);

                    TextView subtitle = (TextView) holder.v.findViewById(R.id.subtitle);
                    if (attributedPhoto.attribution == null) {
                        subtitle.setVisibility(View.GONE);
                    } else {
                        subtitle.setVisibility(View.VISIBLE);
                        subtitle.setText(Html.fromHtml(attributedPhoto.attribution));
                    }

                }
            }
        }.execute(place.id);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View v;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
        }
    }
}
