package com.hotbody.coordinatorrecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created: chiemy
 * Date: 16/9/26
 * Description: 监听 RecyclerView 到达 OverScroll 状态的 LinearLayoutManager
 */
public class OverScrollListenerLinearLayoutManager extends LinearLayoutManager {
    public OverScrollListenerLinearLayoutManager(Context context) {
        super(context);
    }

    public OverScrollListenerLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public OverScrollListenerLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int scrollVerticallyBy (int dy, RecyclerView.Recycler recycler, RecyclerView.State state ) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
        int overscroll = dy - scrollRange;
        if (overscroll != 0 && mOverScrollListener != null) {
            mOverScrollListener.onOverScroll(overscroll);
        }
        return scrollRange;
    }

    private OverScrollListener mOverScrollListener;

    public void setOverScrollListener(OverScrollListener overScrollListener) {
        mOverScrollListener = overScrollListener;
    }

    public interface OverScrollListener {
        void onOverScroll(int overscroll);
    }
}
