package com.ssverma.showtime.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridItemSpacingDecoration extends RecyclerView.ItemDecoration {

    private final int spanCount;
    private final int spacing;

    public GridItemSpacingDecoration(Context context, int spanCount, int spacing) {
        this.spanCount = spanCount;
        this.spacing = (int) ViewUtils.dpToPx(context, spacing);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        int columnNumber = position % spanCount;

        outRect.left = spacing - columnNumber * spacing / spanCount;
        outRect.right = (columnNumber + 1) * spacing / spanCount;
        outRect.bottom = spacing;

        if (position < spanCount) {
            outRect.top = spacing;
        }
    }
}
