package com.edu.tutorialaction.util;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.ListView;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private ListView list;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setList(ListView list) {
        this.list = list;
    }

    @Override
    public boolean canChildScrollUp() {
        if (list != null) {
            return canScrollUpListView();
        } else {
            // Fall back to default implementation
            return super.canChildScrollUp();
        }
    }

    private boolean canScrollUpListView() {
        // Firstly, the wrapped ListView must have at least one item
        return (list.getChildCount() > 0) &&
                // And then, the first visible item must not be the first item
                ((list.getFirstVisiblePosition() > 0) ||
                        // If the first visible item is the first item,
                        // (we've reached the first item)
                        // make sure that its top must not cross over the padding top of the wrapped ListView
                        (list.getChildAt(0).getTop() < 0));
    }
}