package com.boutiqaat.android.boutiqaat.ui.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.databinding.CatogoriesBinding;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;
import com.boutiqaat.android.boutiqaat.ui.FragmentView;
import com.boutiqaat.android.boutiqaat.ui.utils.ResultsAdapter;
import com.boutiqaat.android.boutiqaat.utils.GridLayoutManagerExtn;
import com.boutiqaat.android.boutiqaat.utils.Utils;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.CategoriesViewModel;

import java.util.LinkedHashMap;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

/**
 * This is the user interface class for showing 20 images downloaded from the instagram servers in the internet.
 */
public class CategoriesFragment extends DaggerFragment implements ResultsAdapter.AdapterListener,FragmentView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private CatogoriesBinding binding;
    private RecyclerView recyclerView;
    private CategoriesViewModel viewModel;
    private ResultsAdapter mAdapter;
    private boolean flag;
    private long t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);
        if(!new Utils().checkConnection(getActivity())){
            showDialog();
        }
        return binding.getRoot();
    }

    public void showDialog(){
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.no_connection)
                .setPositiveButton(R.string.ok, (dialog, button) -> dialog.dismiss())
                .show();
    }

    /**
     * Initializing all the UI components
     *
     * @param inflater
     * @param container
     */
    public void init(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.catogories_activity, container, false);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CategoriesViewModel.class);
        Timber.d("viewModelFactory:" + viewModelFactory + ", " + viewModel);
        initRecyclerView();
        initObservers();
        setCheckedChangedListener();
        binding.pb.setVisibility(View.VISIBLE);
        viewModel.fetchImagesDataFromServer();
        t = System.currentTimeMillis();
    }

    /**
     * method to recieve click events of radio button used for changing list to grid and vice versa.
     */
    public void setCheckedChangedListener() {
        binding.up.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("onCheckedChanged-->" + buttonView + " , " + isChecked);
                if (isChecked) {
                    binding.up.setText(getString(R.string.grid));
                    recyclerView.setLayoutManager(new GridLayoutManagerExtn(getActivity(), 1));
                } else {
                    binding.up.setText(getString(R.string.list));
                    recyclerView.setLayoutManager(new GridLayoutManagerExtn(getActivity(), 3));
                }
            }
        });
    }

    /**
     * Instantiating list and grid user interface.
     */
    private void initRecyclerView() {
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManagerExtn(this.getActivity(), 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        LinkedHashMap<String, UsersRecentMediaModel> map = new LinkedHashMap<String, UsersRecentMediaModel>();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.badge_no_image);
        for (int i = 0; i < 20; i++) {
            UsersRecentMediaModel user = new UsersRecentMediaModel();
            user.thumbNailImg = bitmap;
            map.put(String.valueOf(i), user);
        }
        flag = true;
        mAdapter = new ResultsAdapter(map, CategoriesFragment.this);

        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Registering observers for recieving images data obtained and processed by Repository class.
     */
    public void initObservers() {
        viewModel.getNetworkOrDbLiveData().observe(this, map -> {
            binding.pb.setVisibility(View.GONE);

            if (Objects.requireNonNull(map).size() == 0) {
            } else {
                Timber.i("Total time taken to show data: " + (System.currentTimeMillis() - t)
                        + "-----Size of data obtained from server: " + map.keySet().size());
                if (mAdapter == null) {
                    mAdapter = new ResultsAdapter(map, CategoriesFragment.this);
                    recyclerView.setAdapter(mAdapter);
                } else {
                    if (flag) {
                        mAdapter.setResultsMap(map);
                        mAdapter.notifyDataSetChanged();
                        flag = false;
                    } else {
                        int mapSize = mAdapter.getResultsMap().keySet().size();
                        mAdapter.setResultsMap(map);
                        mAdapter.notifyItemRangeInserted(mapSize, map.size() - 1);
                    }
                }
            }
            binding.pb.setVisibility(View.GONE);
        });
    }

    @Override
    public void onPostClicked(UsersRecentMediaModel results, int viewId, int position) {
    }


}
