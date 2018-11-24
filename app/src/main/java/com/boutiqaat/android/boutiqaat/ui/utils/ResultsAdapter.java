package com.boutiqaat.android.boutiqaat.ui.utils;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.databinding.RowItemBinding;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * Adapter for data of Images obtained from internet and shown in CatogoriesFragment.
 */
public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.MyViewHolder> {

    private LinkedHashMap<String, UsersRecentMediaModel> resultsMap;
    private LayoutInflater layoutInflater;
    private AdapterListener recentMediaAdapterListener;

    public ResultsAdapter(LinkedHashMap<String, UsersRecentMediaModel> resultsMap, AdapterListener listener) {
        this.resultsMap = resultsMap;
        recentMediaAdapterListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        RowItemBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.row_item, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Set<Map.Entry<String, UsersRecentMediaModel>> mapSet = resultsMap.entrySet();
        Map.Entry<String, UsersRecentMediaModel> entry = (Map.Entry<String, UsersRecentMediaModel>) mapSet.toArray()[position];
        UsersRecentMediaModel usersRecentMediaModel = resultsMap.get(entry.getKey());
        holder.binding.setUsersrecentmedia(usersRecentMediaModel);
        Log.d("trace", "adapter:" + resultsMap);
        class Listener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                Timber.d("----------onBindViewHolder:View clicked------");
                recentMediaAdapterListener.onPostClicked(usersRecentMediaModel, v.getId(), position);
            }
        }
        ;
        Listener listener = new Listener();
        holder.binding.cardViewImage.setOnClickListener(listener);
        holder.binding.cardViewImageTitle.setOnClickListener(listener);
        Timber.d("-------onBindViewHolder---------" + position);
    }

    @Override
    public int getItemCount() {
        return resultsMap.size();
    }

    public LinkedHashMap<String, UsersRecentMediaModel> getRecentMediaMap() {
        return resultsMap;
    }

    public LinkedHashMap<String, UsersRecentMediaModel> getResultsMap() {
        return resultsMap;
    }

    public void setResultsMap(LinkedHashMap<String, UsersRecentMediaModel> map) {
        resultsMap = map;
    }

    public interface AdapterListener {
        void onPostClicked(UsersRecentMediaModel usersRecentMediaModel, int viewId, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final RowItemBinding binding;

        public MyViewHolder(final RowItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

    }
}
