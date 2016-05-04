package test.ui.view.expansion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import activity.ui.com.test.R;

/**
 * Created by 郭敏 on 2016/4/29.
 */

/**
 * Created by 郭敏 on 2016/4/29.
 */
public class ExpPullHeadView {
    private ExpandableListView listView;
    private View handVeiw;
    private int headViewHeight;
    //
    private ImageView arrows;
    private TextView title;
    private ProgressBar progressBar;
    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;
    // 事件回调接口
    private ExpansionListListener listener;
    private Scroller mScroller;


    public ExpPullHeadView(ExpandableListView listView, Scroller mScroller) {
        this.listView = listView;
        this.mScroller = mScroller;
        init(listView.getContext());
    }

    public void setListener(ExpansionListListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        handVeiw = LayoutInflater.from(context).inflate(
                R.layout.exp_list_head, null);
        arrows = (ImageView) handVeiw.findViewById(R.id.head_arrows_iv);
        title = (TextView) handVeiw
                .findViewById(R.id.head_title_tv);
        progressBar = (ProgressBar) handVeiw
                .findViewById(R.id.head_progressBar);
        measureView(handVeiw);
        headViewHeight = handVeiw.getMeasuredHeight();
        // 设置箭头布局的最小高和宽：目的 当箭头旋转的时候，箭头看起来不会残缺不全
        arrows.setMinimumWidth(70);
        arrows.setMinimumHeight(50);
        // 初始化时，头部被盖住
        handVeiw.setPadding(0, -1 * headViewHeight, 0, 0);
        handVeiw.invalidate();
        listView.addHeaderView(handVeiw);
        // 旋转180度
        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);
        // 旋转-180度
        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);
    }

    private void measureView(View headView) {

        ViewGroup.LayoutParams p = headView.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
        }
        headView.measure(childWidthSpec, childHeightSpec);
    }

    private float downY;


    public boolean onTouchEvent(MotionEvent ev) {
        if (state == state_underway) {
            return false;
        }
        if (state == state_complete) {
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int tempY = (int) ev.getY();
                int moveY = (int) (tempY - downY);
                if (listView.getFirstVisiblePosition() == 0 && moveY > 0) {
                    move(moveY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                int upY = (int) ev.getY();
                int stopY = (int) (upY - downY);
                if (state == state_can) {
                    // 可以去刷新
                    state = state_underway;
                    stateChange();
                }
                if (state == -1) {
                    int paddingTop = handVeiw.getPaddingTop();
                    int showHeight = headViewHeight - Math.abs(paddingTop);
                    Log.e("setPadding:" + handVeiw.getPaddingTop(), "showHeight:" + showHeight +
                            " 时间:");
                    if (showHeight != 0) {
                        float d = (float) duration * ((float) backHeight / (float) headViewHeight);
                        headViewScrooll(showHeight,headViewHeight,(int)d);
                    }
                }
                if (listView.getFirstVisiblePosition() == 0 && stopY > 0) {
                    return true;
                }
                break;

        }
        return false;
    }

    //要回退的高度
    private int backHeight;
    //要回退至的高度
    private int aimHeight;
    //自动退回持续时间；
    private int duration = 400;

    private void headViewScrooll(int backHeight,int aimHeight,int durations) {
        this.backHeight = backHeight;
        this.aimHeight = aimHeight;
        isUpBack = true;
        listView.postInvalidate();
         mScroller.startScroll(0, 0, 0, backHeight, durations);
    }

    private boolean isUpBack;

    public void computeScroll() {
        // isScroller :true 正在滚动
        boolean isScroller = mScroller.computeScrollOffset();
        if (isScroller && isUpBack) {
             int scroller = mScroller.getCurrY();
            Log.e("滚动值：" + scroller, "------------");
            int paddingTop = aimHeight - backHeight + scroller;
            setPaddingTop(-paddingTop);
        } else {
            isUpBack = false;
        }

    }

    private void setPaddingTop(int paddingTop) {
        handVeiw.setPadding(0, paddingTop, 0, 0);
    }

    private void move(int moveY) {
        handVeiw.setPadding(0, -1 * headViewHeight + moveY, 0, 0);
        int stateTemp = -1;
        if (moveY >= headViewHeight) {
            stateTemp = state_can;
        }
        if (state != stateTemp) {
            state = stateTemp;
            stateChange();
        }
    }

    //
    private int state = -1;
    /**
     * 达到可刷新值
     */
    private final int state_can = 21;
    /**
     * 刷新中
     */
    private final int state_underway = 3;

    /**
     * 刷新完成
     */
    private final int state_complete = 4;

    private void stateChange() {
        switch (state) {
            case -1:
                progressBar.setVisibility(View.GONE);
                arrows.clearAnimation();
                arrows.setVisibility(View.VISIBLE);
                arrows.startAnimation(reverseAnimation);
                title.setText("下拉刷新");
                break;
            case state_can:
                arrows.clearAnimation();
                arrows.startAnimation(animation);
                title.setText("松开刷新");
                break;
            case state_underway:
                // 正在刷新
                arrows.clearAnimation();
                arrows.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                title.setText("刷新中...");
                //多余的回滚调，只留下headViewHeight
                int paddingTop = handVeiw.getPaddingTop();
                if(paddingTop>0) {
                    Log.e("回退",""+paddingTop);
                    headViewScrooll(paddingTop,0,100);
                }
                if (listener != null) {
                    listener.onPullRefresh();
                }
                break;
            case state_complete:
                // 刷新完成
                resetHeaderHeight();
                break;
        }
    }

    public void onRenovationComplete() {
        state = state_complete;
        stateChange();
    }


    /**
     * 重置头视图的高度
     */
    private void resetHeaderHeight() {
        headViewScrooll(headViewHeight,headViewHeight,400);
        state = -1;
        stateChange();
    }


}
