package test.ui.view.expansion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import activity.ui.com.test.R;

/**
 * Created by Administrator on 2016/5/3.
 */
public class MainExpHead {
    //松开刷新
    private final static int RELEASE_TO_REFRESH = 0;//
    private final static int PULL_TO_REFRESH = 1;//下拉刷新

    private final static int REFRESHING = 2;//正在刷新

    private final static int DONE = 3;
    private final static int RATIO = 2;//实际的padding的距离与界面 上偏移距离 的比例
    private LayoutInflater inflater;
    private LinearLayout headLayout;//头linearlayout
    private TextView tipsTextview;
    private ImageView arrowImageView;//箭头的图标

    private ProgressBar progressBar;

    private RotateAnimation animation;
    // 反转动画
    private RotateAnimation reverseAnimation;


    private LinearLayout headView;
    private int headContentHeight;

    /**
     * 手势按下的起点位置
     */
    private int startY;
    private int firstItemIndex;

    private int state;

    private boolean isBack;
    private MainExpandableListView MainExpandableListView;
    private boolean isRefreshable99;

    public MainExpHead(MainExpandableListView MainExpandableListView, Context contex) {
        this.MainExpandableListView = MainExpandableListView;
        init(contex);
    }

    public void init(Context context) {
        inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.exp_list_head, null);
        arrowImageView = (ImageView) headView.findViewById(R.id.head_arrows_iv);//箭头
        arrowImageView.setMinimumWidth(70);
        arrowImageView.setMinimumHeight(50);
        progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
        tipsTextview = (TextView) headView.findViewById(R.id.head_title_tv);
        // lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

        headView.measure(0, 0);
        headContentHeight = headView.getMeasuredHeight();

        headView.setPadding(0, -headContentHeight, 0, 0);//把headview隐藏到顶部
        headView.invalidate();//刷新界面
        MainExpandableListView.addHeaderView(headView, null, false);
        animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        state = DONE;
    }

    /**
     * 设置触摸事件 总的思路就是
     * <p/>
     * 1 ACTION_DOWN：记录起始位置
     * <p/>
     * 2 ACTION_MOVE：计算当前位置与起始位置的距离，来设置state的状态
     * <p/>
     * 3 ACTION_UP：根据state的状态来判断是否下载
     */
    public void onTouchEvent(MotionEvent event) {
        if (firstItemIndex != 0) {
            return;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();

                break;

            case MotionEvent.ACTION_MOVE:
                int tempY = (int) event.getY();
                if (state == PULL_TO_REFRESH) {
                    MainExpandableListView.setSelection(0);
                    //下拉到可以release_to_refresh的状态
                    if ((tempY - startY) / RATIO >= headContentHeight) {
                        state = RELEASE_TO_REFRESH;
                        isBack = true;
                        changeHeaderViewByState();
                    } else if (tempY - startY <= 0) {
                        state = DONE;
                        changeHeaderViewByState();
                    }
                    headView.setPadding(0, -headContentHeight + (tempY - startY) / RATIO, 0, 0);
                }
                if (state == RELEASE_TO_REFRESH) {
                    MainExpandableListView.setSelection(0);
                    // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                    if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0) {
                        state = PULL_TO_REFRESH;
                        changeHeaderViewByState();
                    }
                    headView.setPadding(0, -headContentHeight + (tempY - startY) / RATIO, 0, 0);
                }
                // done状态下
                if (state == DONE) {
                    if (tempY - startY > 0) {
                        state = PULL_TO_REFRESH;
                        changeHeaderViewByState();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (state != REFRESHING) {
                    // 不在刷新状态
                    if (state == PULL_TO_REFRESH) {
                        state = DONE;
                        changeHeaderViewByState();
                    }
                    if (state == RELEASE_TO_REFRESH) {
                        state = REFRESHING;
                        changeHeaderViewByState();
                    }
                }
                isBack = false;
                break;

        }

    }


    //当状态改变时候，调用 该方法，以更新界面
    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_TO_REFRESH:
                arrowImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tipsTextview.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.startAnimation(animation);
                tipsTextview.setText("松开刷新");

                break;
            case PULL_TO_REFRESH:
                progressBar.setVisibility(View.GONE);
                tipsTextview.setVisibility(View.VISIBLE);

                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.VISIBLE);
                tipsTextview.setText("下拉刷新");
                // 是RELEASE_To_REFRESH状态转变来的
                if (isBack) {
                    isBack = false;
                    arrowImageView.startAnimation(reverseAnimation);
                }
                break;

            case REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                progressBar.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.GONE);
                tipsTextview.setText("正在刷新...");
                break;
            case DONE:
                headView.setPadding(0, -headContentHeight, 0, 0);
                progressBar.setVisibility(View.GONE);
                arrowImageView.clearAnimation();
                arrowImageView.setImageResource(R.mipmap.arrow);
                tipsTextview.setText("下拉刷新");
                break;
        }
    }


    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstItemIndex = firstVisibleItem;

    }
}
