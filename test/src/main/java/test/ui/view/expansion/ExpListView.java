package test.ui.view.expansion;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Scroller;

import java.util.ArrayList;

/**
 * Created by 郭敏 on 2016/4/27.
 */
public class ExpListView extends ExpandableListView implements AbsListView.OnScrollListener {


    private ExpansionListListener listener;
    private Scroller mScroller;

    public ExpListView(Context context) {
        super(context);
        init();
    }

    public ExpListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
         setOnScrollListener(this);
    }

    private ArrayList<View> headViews = new ArrayList<View>();

    @Override
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        super.addHeaderView(v, data, isSelectable);
        if (expTopView != null) {
            return;
        }
        headViews.add(v);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
       // setOnScrollListener(this);
    }

    private ExpTopView expTopView;
    private ExpPullHeadView expPullHeadView;

    //设置标签view
    public void setTopViewLayoutId(int layoutId, Context context) {
        expTopView = new ExpTopView(this);
        expTopView.setTopViewLayoutId(layoutId, context);
        if (listener != null) {
            expTopView.setListener(listener);
        }
    }

    //设置使用下拉刷新
    public void setPullRenovation() {
        if (expPullHeadView != null) {
            return;
        }
        boolean isRemove = false;
        for (int i = 0; i < headViews.size(); i++) {
            removeHeaderView(headViews.get(i));
            isRemove = true;
        }
        expPullHeadView = new ExpPullHeadView(this, mScroller);
        if (listener != null) {
            expPullHeadView.setListener(listener);
        }
        if (!isRemove) {
            return;
        }
        for (int i = 0; i < headViews.size(); i++) {
            addHeaderView(headViews.get(i));
        }
    }


    public void onRenovationComplete() {
        if (expPullHeadView != null) {
            expPullHeadView.onRenovationComplete();
         }
    }

    //设置监听
    public void setListener(ExpansionListListener listener) {
        this.listener = listener;
        if (expTopView != null) {
            expTopView.setListener(listener);
        }
        if (expPullHeadView != null) {
            expPullHeadView.setListener(listener);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                 Log.e("加载", "----------");
                break;
            default:
                Log.e("暂停加载", "----------");
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (expTopView != null) {
            expTopView.viewScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
     }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean isOnTouch=false;
        if (expPullHeadView != null) {
            isOnTouch= expPullHeadView.onTouchEvent(ev);
        }
        if(isOnTouch){
            return true;
        }
        return   super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (expPullHeadView != null) {
            expPullHeadView.computeScroll();
        }
        super.computeScroll();
    }
}
