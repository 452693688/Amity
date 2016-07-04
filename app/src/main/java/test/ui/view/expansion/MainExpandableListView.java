package test.ui.view.expansion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

/**
 * Created by Administrator on 2016/5/3.
 */
public class MainExpandableListView extends ExpandableListView implements
        AbsListView.OnScrollListener {

    private MainExpHead mainExpHead;

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    public MainExpandableListView(Context context) {
        super(context);
    }

    public MainExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPullRenovation() {
        mainExpHead = new MainExpHead(this, getContext());
        setOnScrollListener(this);
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mainExpHead != null) {
            mainExpHead.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public boolean onTouchEvent(MotionEvent event) {
        if (mainExpHead != null) {
            mainExpHead.onTouchEvent(event);
        }
        return super.onTouchEvent(event);

    }

}
