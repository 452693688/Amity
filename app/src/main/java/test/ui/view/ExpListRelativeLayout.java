package test.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/4/29.
 */
public class ExpListRelativeLayout extends RelativeLayout {
    public ExpListRelativeLayout(Context context) {
        super(context);
    }

    public ExpListRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpListRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        int count = getChildCount();
        if (count == 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof ExpandableListView) {
                return;
            }
        }
    }
}
