package test.ui.view.expansion;

import android.view.View;

/**
 * Created by 郭敏 on 2016/4/29.
 */
public interface ExpansionListListener {
    public void topViewClick(View view,int groupIndex);
    public void topViewData(View view,int groupIndex);
    public void onPullRefresh();
}
