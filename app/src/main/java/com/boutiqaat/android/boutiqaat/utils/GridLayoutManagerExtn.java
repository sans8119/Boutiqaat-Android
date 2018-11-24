package com.boutiqaat.android.boutiqaat.utils;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import timber.log.Timber;

/**
 * Ui element for managing items in the Grid; this is used for showing images in the CatogoriesFragment.
 */
public class GridLayoutManagerExtn extends GridLayoutManager {

    public GridLayoutManagerExtn(Context context, AttributeSet attrs, int defStyleAttr,
                                 int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public GridLayoutManagerExtn(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerExtn(Context context, int spanCount,
                                 @RecyclerView.Orientation int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Timber.e("Handling ' Invalid view holder adapter positionViewHolder ' in RecyclerView");
        }
    }
}
