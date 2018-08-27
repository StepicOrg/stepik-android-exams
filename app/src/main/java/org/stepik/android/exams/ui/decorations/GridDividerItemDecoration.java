package org.stepik.android.exams.ui.decorations;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.stepik.android.exams.R;

public class GridDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable horizontalDivider;
    private Drawable verticalDivider;

    public GridDividerItemDecoration(Context context) {
        horizontalDivider = ContextCompat.getDrawable(context, R.drawable.list_divider_h);
        verticalDivider = ContextCompat.getDrawable(context, R.drawable.list_divider_w);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int bottom = child.getBottom() + params.bottomMargin;
            int top = bottom - horizontalDivider.getIntrinsicHeight();
            int left = child.getLeft();
            int right = child.getRight();

            horizontalDivider.setBounds(left, top, right, bottom);
            horizontalDivider.draw(c);

            int leftVertical = Math.max(0, right - verticalDivider.getIntrinsicWidth());
            int topVertical = child.getTop();

            verticalDivider.setBounds(leftVertical, topVertical, right, bottom);
            verticalDivider.draw(c);
        }
    }
}