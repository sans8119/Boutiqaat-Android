package com.boutiqaat.android.boutiqaat.ui.utils;

import android.databinding.DataBindingUtil;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.databinding.RowItemLocationBinding;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;


/**
 * Adapter for data of Location updates shown in LocationFragment.
 */
public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {
    private LinkedHashMap<String, CustomerLocation> resultsMap;
    private LayoutInflater layoutInflater;
    private AdapterListener listener;


    public LocationsAdapter(LinkedHashMap<String, CustomerLocation> resultsMap, AdapterListener listener) {
        this.resultsMap = resultsMap;
        this.listener = listener;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        RowItemLocationBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.row_item_location, parent, false);
        return new LocationViewHolder(binding);
    }

    /**
     * Creating the row ui and filling with location information of the location update.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(LocationViewHolder holder, final int position) {
        if (position == 0) {
            holder.binding.getRoot().setBackgroundColor(ResourcesCompat.getColor(holder.binding.cardViewImageTitleLocation.getResources(),
                    R.color.grey, null));
        } else {
            holder.binding.getRoot().setBackgroundColor(ResourcesCompat.getColor(holder.binding.cardViewImageTitleLocation.getResources(),
                    R.color.white, null));
        }
        Set<Map.Entry<String, CustomerLocation>> mapSet = resultsMap.entrySet();
        Map.Entry<String, CustomerLocation> entry = (Map.Entry<String, CustomerLocation>) mapSet.toArray()[position];
        CustomerLocation customerLoc = resultsMap.get(entry.getKey());
        String prefix=holder.binding.cardViewImageTitleLocation.getContext().getString(R.string.you_are_at);
        customerLoc.locationString = prefix + "\n" + customerLoc.locationString.replaceAll(prefix,"").trim();
        holder.binding.setCustomerLocation(customerLoc);
        class Listener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                Timber.d("----------onBindViewHolder:View clicked------");
                listener.onPostClicked(customerLoc, v.getId(), position);
            }
        }
        ;
        Listener listener = new Listener();
        holder.binding.cardViewImageTitleLocation.setOnClickListener(listener);
        Timber.d("-------onBindViewHolder---------" + position);
    }

    @Override
    public int getItemCount() {
        return resultsMap.size();
    }

    public LinkedHashMap<String, CustomerLocation> getRecentMediaMap() {
        return resultsMap;
    }

    public LinkedHashMap<String, CustomerLocation> getResultsMap() {
        return resultsMap;
    }

    public void setResultsMap(LinkedHashMap<String, CustomerLocation> map) {
        resultsMap = map;
    }

    public interface AdapterListener {
        void onPostClicked(CustomerLocation customerLocation, int viewId, int position);
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        private final RowItemLocationBinding binding;

        public LocationViewHolder(final RowItemLocationBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

    }
}
