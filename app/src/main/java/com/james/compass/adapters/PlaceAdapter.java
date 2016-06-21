package com.james.compass.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.james.compass.R;
import com.james.compass.activities.PlaceActivity;
import com.james.compass.data.AttributedPhoto;
import com.james.compass.data.PlaceData;
import com.james.compass.utils.PhotoTask;
import com.james.compass.utils.Saved;
import com.james.compass.views.CustomImageView;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<PlaceData> places;

    public PlaceAdapter(Activity activity, ArrayList<PlaceData> places) {
        this.activity = activity;
        this.places = places;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_place, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PlaceData place = places.get(position);

        TextView title = (TextView) holder.v.findViewById(R.id.title);

        View directions = holder.v.findViewById(R.id.directions);
        TextView address = (TextView) holder.v.findViewById(R.id.address);

        View phone = holder.v.findViewById(R.id.phone);
        TextView number = (TextView) holder.v.findViewById(R.id.number);

        AppCompatButton save = (AppCompatButton) holder.v.findViewById(R.id.save);
        AppCompatButton website = (AppCompatButton) holder.v.findViewById(R.id.website);

        title.setText(place.name);

        address.setText(place.address);
        directions.setVisibility(place.address != null && place.address.length() > 0 ? View.VISIBLE : View.GONE);
        directions.setOnClickListener(new View.OnClickListener() {
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
        phone.setVisibility(place.number != null && place.number.length() > 0 ? View.VISIBLE : View.GONE);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceData place = places.get(holder.getAdapterPosition());
                activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + place.number)));
            }
        });

        save.setText(Saved.isSavedPlace(activity, place) ? R.string.action_unsave : R.string.action_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceData place = places.get(holder.getAdapterPosition());
                if (Saved.isSavedPlace(activity, place)) Saved.removePlace(activity, place);
                else Saved.addPlace(activity, place);


            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, places.get(holder.getAdapterPosition()).website));
            }
        });

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PlaceActivity.class);
                intent.putExtra("place", places.get(holder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        new PhotoTask(activity, 1600, 900) {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    holder.v.findViewById(R.id.imageLayout).setVisibility(View.VISIBLE);
                    ((CustomImageView) holder.v.findViewById(R.id.image)).transition(activity, attributedPhoto.bitmap);
                    ((TextView) holder.v.findViewById(R.id.imageAttrs)).setText(activity.getString(R.string.image_credit_prefix) + Html.fromHtml(attributedPhoto.attribution));
                } else {
                    holder.v.findViewById(R.id.imageLayout).setVisibility(View.GONE);
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
