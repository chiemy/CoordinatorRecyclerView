package com.hotbody.coordinatorrecyclerview;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created: chiemy
 * Date: 16/9/20
 * Description:
 */
public class RecyclerViewBehavior extends AppBarLayout.Behavior implements AppBarLayout.OnOffsetChangedListener {
    private static final float CHANGE_COMPLETE_PERCENTAGE = 0.7f; // 到达此值变化完成

    private Map<RecyclerView, RecyclerViewScrollListener> scrollListenerMap;
    private OnScrollChangeListener mOnScrollChangeListener;

    public RecyclerViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrollListenerMap = new HashMap<>();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        child.addOnOffsetChangedListener(this);
        if (target instanceof RecyclerView) {
            final RecyclerView recyclerView = (RecyclerView) target;
            if (scrollListenerMap.get(recyclerView) == null) {
                final RecyclerViewScrollListener recyclerViewScrollListener = new RecyclerViewScrollListener(parent, child, target, this);
                scrollListenerMap.put(recyclerView, recyclerViewScrollListener);
                recyclerView.addOnScrollListener(recyclerViewScrollListener);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof OverScrollListenerLinearLayoutManager) {
                    ((OverScrollListenerLinearLayoutManager) layoutManager).setOverScrollListener(recyclerViewScrollListener);
                }
            }
        }
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public boolean onNestedFling(final CoordinatorLayout coordinatorLayout, final AppBarLayout child, final View target, float velocityX, float velocityY, boolean consumed) {
        if (target instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) target;
            if (scrollListenerMap.get(recyclerView) != null) {
                // 滚动距离大于0时, 滚动被RecyclerView消费掉, AppBarLayout不要滚动
                consumed = scrollListenerMap.get(recyclerView).getScrolledY() > 0;
            }
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    private boolean superNestedFling(final CoordinatorLayout coordinatorLayout, final AppBarLayout child, final View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScroll();
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        mOnScrollChangeListener = onScrollChangeListener;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float collapsePercentageFactor = Math.abs((float) verticalOffset) / appBarLayout.getTotalScrollRange();
        update(collapsePercentageFactor);
    }

    private void update(float collapsePercentageFactor) {
    }

    public interface OnScrollChangeListener {
        void onScroll();
    }

    private static class RecyclerViewScrollListener extends RecyclerView.OnScrollListener implements
            OverScrollListenerLinearLayoutManager.OverScrollListener {
        private boolean fling;
        private int scrolledY;
        private WeakReference<CoordinatorLayout> coordinatorLayoutRef;
        private WeakReference<AppBarLayout> childRef;
        private WeakReference<RecyclerViewBehavior> behaviorWeakReference;
        private WeakReference<View> targetWeakReference;

        public RecyclerViewScrollListener(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, RecyclerViewBehavior barBehavior) {
            coordinatorLayoutRef = new WeakReference<>(coordinatorLayout);
            childRef = new WeakReference<>(child);
            behaviorWeakReference = new WeakReference<>(barBehavior);
            targetWeakReference = new WeakReference<>(target);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                fling = true;
            }
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                fling = false;
            }
        }

        public int getScrolledY() {
            return scrolledY;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            scrolledY += dy;
        }

        @Override
        public void onOverScroll(int overscroll) {
            if (overscroll < 0) {
                flingTop(overscroll * 100);
            }
        }

        public void flingTop(int velocityY) {
            if (fling
                    && behaviorWeakReference.get() != null
                    && childRef.get() != null
                    && targetWeakReference.get() != null) {
                behaviorWeakReference.get().superNestedFling(
                        coordinatorLayoutRef.get(),
                        childRef.get(),
                        targetWeakReference.get(),
                        0,
                        velocityY,
                        false);
            }
        }
    }

}
