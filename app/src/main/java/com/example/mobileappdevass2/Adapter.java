package com.example.mobileappdevass2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends BaseAdapter implements Filterable {

    private List<Location> locationList;
    private List<Location> originalLocationList;
    private LayoutInflater inflater;

    //constructor
    public Adapter(Context context, List<Location> locationList) {
        this.locationList = locationList;
        inflater = LayoutInflater.from(context);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
    }

    //number of locations getter
    @Override
    public int getCount() {
        return locationList.size();
    }

    //specific location getter
    @Override
    public Object getItem(int position) {
        return locationList.get(position);
    }

    //item ID getter
    @Override
    public long getItemId(int position) {
        return locationList.get(position).getId();
    }

    //view getter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        //check for view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview, parent, false);
            holder = new ViewHolder();
            holder.addressTextView = convertView.findViewById(R.id.addressText);
            holder.latitudeTextView = convertView.findViewById(R.id.longitudeText);
            holder.longitudeTextView = convertView.findViewById(R.id.longitudeText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //get loctaion
        Location currentLocation = locationList.get(position);

        //set data
        holder.addressTextView.setText(currentLocation.getAddress());
        holder.latitudeTextView.setText("Latitude: " + currentLocation.getLatitude());
        holder.longitudeTextView.setText("Longitude: " + currentLocation.getLongitude());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //update list
                locationList = (List<Location>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<Location> filteredList = new ArrayList<>();

                //save original list
                if (originalLocationList == null) {
                    originalLocationList = new ArrayList<>(locationList);
                }

                //error checking
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalLocationList);
                } else {
                    //filter
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Location location : originalLocationList) {
                        if (location.getAddress().toLowerCase().contains(filterPattern)) {
                            filteredList.add(location);
                        }
                    }
                }

                filterResults.values = filteredList;
                filterResults.count = filteredList.size();
                return filterResults;
            }
        };
    }

    //ViewHolder
    static class ViewHolder {
        TextView addressTextView;
        TextView latitudeTextView;
        TextView longitudeTextView;
    }
}
