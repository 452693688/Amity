package test.ui.view.expansion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import activity.ui.com.test.R;

/**
 * Created by 郭敏 on 2016/4/29.
 */
public class ExpTopView {
    private ExpandableListView listView;
    private FrameLayout.LayoutParams layoutParams;

    public ExpTopView(ExpandableListView listView) {
        this.listView = listView;
    }

    public void setListener(ExpansionListListener listener) {
        this.listener = listener;
    }

    public void setHeadCount(int addHeadCount) {
        this.addHeadCount = addHeadCount;
    }

    //设置标签view
    public void setTopViewLayoutId(int layoutId, Context context) {
        ViewParent parent = listView.getParent();
        if (!(parent instanceof FrameLayout)) {
            return;
        }
         topView = LayoutInflater.from(context).inflate(layoutId, null);
        if (topView == null) {
            return;
        }
        FrameLayout  rootViw= (FrameLayout)parent;
        TextView testTv = new TextView(context);
        testTv.setText("1234567890");
      //  rootViw.addView(testTv);
        headTv = (TextView) topView.findViewById(R.id.group_title_tv);
          layoutParams = new FrameLayout.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        topView.setLayoutParams(layoutParams);
        rootViw.addView(topView);
        isInoperation = true;
        topView.setOnClickListener(new TopViewClick());
       topView.setVisibility(View.GONE);
    }

    //事件回调接口
    private ExpansionListListener listener;
    private TextView headTv;
    //添加的headView的数量
    private int addHeadCount = 0;
    //false：不显示 有top标签的list
    private boolean isInoperation;
    //标签view
    private View topView;
    //标签代表的在listGroups里面groupView的索引
    private int indicatorGroupIndex = -1;
    //标签高度
    private int indicatorGroupHeight = 0;

    public void viewScroll(AbsListView view, int firstVisibleItem,
                           int visibleItemCount, int totalItemCount) {
        if (!isInoperation) {
            return;
        }
        if (firstVisibleItem < addHeadCount) {
            hideIndicator();
            return;
        }
        if (firstVisibleItem == AdapterView.INVALID_POSITION) {
            return;
        }
        long id = listView.getExpandableListPosition(firstVisibleItem);
        // 获取第一行child的id
        int childIndex = ExpandableListView.getPackedPositionChild(id);
        //返回所选择的组  获取第一行group的id
        int groupIndex = ExpandableListView.getPackedPositionGroup(id);
        //获取高度
        if (groupIndex != AdapterView.INVALID_POSITION && indicatorGroupHeight == 0) {
            View groupView = listView.getChildAt(groupIndex);
            if (groupView != null) {
                indicatorGroupHeight = groupView.getHeight();
            }
        }
        if (indicatorGroupHeight == 0) {
            return;
        }
        boolean isShow = false;
        // 第一行是显示child 显示指示器
        if (childIndex != AdapterView.INVALID_POSITION && indicatorGroupIndex >= 0) {
            showIndicator(indicatorGroupIndex);
            isShow = true;
        }
        // 第一行是显示group,并且是展开的 显示指示器
        if (groupIndex != AdapterView.INVALID_POSITION && listView.isGroupExpanded(groupIndex)) {
            indicatorGroupIndex = groupIndex;
            showIndicator(indicatorGroupIndex);
            isShow = true;
        }
        if (!isShow) {
            hideIndicator();
            return;
        }
        /**
         *下面是形成往上推出,下拉的效果
         */
        int showHeight = indicatorGroupHeight;
        int nextViewIndex = listView.pointToPosition(0, indicatorGroupHeight);
        if (nextViewIndex == AdapterView.INVALID_POSITION) {
            return;
        }
        long nextViewId = listView.getExpandableListPosition(nextViewIndex);
        int nextViewgroupIndex = ExpandableListView.getPackedPositionGroup(nextViewId);
        if (nextViewgroupIndex != indicatorGroupIndex) {
            View nextView = listView.getChildAt(nextViewIndex
                    - listView.getFirstVisiblePosition());
            showHeight = nextView.getTop();
        }

        updataIndicator(showHeight - indicatorGroupHeight);
    }

    private void hideIndicator() {
        topView.setVisibility(View.GONE);
        updataIndicator(0);

    }

    private void showIndicator(int groupIndex) {
        topView.setTag(groupIndex);
        //String title = adapter.groups.get(groupIndex);
        //headTv.setText("title");
        topView.setVisibility(View.VISIBLE);
        if (listener != null) {
            listener.topViewData(topView, groupIndex);
        }
    }

    private void updataIndicator(int top) {

        layoutParams.topMargin = top;
        topView.setLayoutParams(layoutParams);
    }

    class TopViewClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Object tag = topView.getTag();
            int index = -1;
            if (tag != null) {
                index = (int) tag;
            }
            if (index < 0) {
                return;
            }
            if (listener != null) {
                listener.topViewClick(v, index);
                return;
            }
            boolean isOPen = listView.isGroupExpanded(index);
            if (isOPen) {
                listView.collapseGroup(index);
            } else {
                listView.expandGroup(index);
            }
            listView.setSelectedGroup(index);
        }
    }
}
