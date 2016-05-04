package test.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import test.ui.able.ItemListener;

/**
 * 有隐藏
 */
public class MoveListView extends ListView {

    private WindowManager windowManager;// windows窗口控制类
    private WindowManager.LayoutParams windowParams;// 用于控制拖拽项的显示的参数

    private int scaledTouchSlop;// 判断滑动的一个距离,scroll的时候会用到(24)

    private ImageView dragImageView;// 被拖拽的项(item)，其实就是一个ImageView
    /**
     * 手指拖动项原始在列表中的索引
     */
    private int indexStart;
    /**
     * 拖动项的位置在列表中的索引
     */
    private int indexMove;
    /**
     * 时时计算悬浮项在列表中的索引
     */
    private int indexCount;

    /**
     * 触摸点到这个itemView的距离
     */
    private int touchItemTop;
    /**
     * 这个值是固定的:其实就是ListView这个控件与屏幕最顶部的距离（一般为标题栏+状态栏）
     */
    private int fixation;

    // 拖动的时候，开始向上滚动的边界
    private int viewScrollUp;
    // 拖动的时候，开始向下滚动的边界
    private int viewScrollDown;

    private final static int step = 1;// ListView 滑动步伐.


    //子view的高度
    private int chileViewHeight = -1;


    private static final int ANIMATION_DURATION = 200;

    private ItemListener itemListener;

    public MoveListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

    }


    public void setOnItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    private void getSpacing() {
        viewScrollUp = getHeight() / 3;// 取得向上滚动的边际，大概为该控件的1/3
        viewScrollDown = getHeight() * 2 / 3;// 取得向下滚动的边际，大概为该控件的2/3
        int[] tempLocation0 = new int[2];
        int[] tempLocation1 = new int[2];
        ViewGroup itemView0 = (ViewGroup) getChildAt(0);
        ViewGroup itemView1 = (ViewGroup) getChildAt(1);
        if (itemView0 != null) {
            itemView0.getLocationOnScreen(tempLocation0);
        }
        if (itemView1 != null) {
            itemView1.getLocationOnScreen(tempLocation1);
            chileViewHeight = Math.abs(tempLocation1[1] - tempLocation0[1]);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();// 获取相对与ListView的x坐标
            int y = (int) ev.getY();// 获取相应与ListView的y坐标
            indexCount = indexStart = indexMove = pointToPosition(x, y);
            // 无效不进行处理
            if (indexMove == AdapterView.INVALID_POSITION) {
                return super.onInterceptTouchEvent(ev);
            }
            if (chileViewHeight == -1) {
                getSpacing();
            }

            // 获取当前位置的视图(可见状态)
            ViewGroup dragger = (ViewGroup) getChildAt(indexMove
                    - getFirstVisiblePosition());
            // 获取到的dragPoint其实就是在你点击指定item项中的高度.
            touchItemTop = y - dragger.getTop();
            // 这个值是固定的:其实就是ListView这个控件与屏幕最顶部的距离（一般为标题栏+状态栏）.
            fixation = (int) (ev.getRawY() - y);
            // 获取可拖拽的图标
            View touchView = dragger.findViewById(itemListener.getTouchViewId());
            if (touchView != null && x > touchView.getLeft() - 20) {
                dragger.destroyDrawingCache();
                dragger.setDrawingCacheEnabled(true);// 开启cache.
                dragger.setBackgroundColor(0x55555555);
                Bitmap bm = Bitmap.createBitmap(dragger.getDrawingCache(true));// 根据cache创建一个新的bitmap对象.
                itemListener.itemHide(indexStart);
                itemListener.notifyDataSetChanged();
                suspensionInit(bm, y);// 初始化影像
            }
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }


    /**
     * 触摸事件处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (dragImageView != null && indexMove != INVALID_POSITION) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    int upY = (int) ev.getY();
                    suspensionStop();
                    itemMoveStop();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) ev.getY();
                    suspensionMove(moveY);
                    itemMoveSlide(moveY);
                    break;
                case MotionEvent.ACTION_DOWN:
                    break;
                default:
                    break;
            }
            return true;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 准备拖动，初始化拖动项的图像
     *
     * @param bm
     * @param y
     */
    private void suspensionInit(Bitmap bm, int y) {
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = y - touchItemTop + fixation;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 不需获取焦点
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 不需接受触摸事件
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON// 保持设备常开，并保持亮度不变。
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;// 窗口占满整个屏幕，忽略周围的装饰边框（例如状态栏）。此窗口需考虑到装饰边框的内容。

        // windowParams.format = PixelFormat.TRANSLUCENT;// 默认为不透明，这里设成透明效果.
        windowParams.windowAnimations = 0;// 窗口所使用的动画设置

        windowParams.alpha = 0.8f;
        windowParams.format = PixelFormat.TRANSLUCENT;

        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);

        windowManager.addView(imageView, windowParams);
        dragImageView = imageView;
    }

    /**
     * 悬浮图片移动
     *
     * @param y
     */
    public void suspensionMove(int y) {
        y -= touchItemTop;
        if (y < 0) {
            y = 0;
        }
        int lastY = getChildAt(getChildCount() - 1).getTop();
        if (y > lastY) {
            y = lastY;
        }

        if (dragImageView != null) {
            windowParams.alpha = 1.0f;
            windowParams.y = y + fixation;
            windowManager.updateViewLayout(dragImageView, windowParams);// 时时移动.
        }
        listScroller(y);
    }

    /**
     * 悬浮图片去除
     */
    public void suspensionStop() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
        isSameDragDirection = true;
        lastFlag = -1;
        itemListener.itemSlideFlag(lastFlag);
        itemListener.itemSlideComplete();
    }


    private boolean isSameDragDirection = true;
    private int lastFlag = -1; //-1,0 == down,1== up

    //itemView移动
    private void itemMoveSlide(int y) {
        int tempPosition = pointToPosition(0, y);
        if (tempPosition == INVALID_POSITION || tempPosition == indexCount) {
            return;
        }
        indexMove = tempPosition;
        if (indexCount != indexMove) {
            itemListener.itemExchange(indexCount, indexMove);
        }
        int MoveNum = tempPosition - indexCount;
        int count = Math.abs(MoveNum);
        for (int i = 1; i <= count; i++) {
            int xAbsOffset, yAbsOffset;
            //向下drag
            if (MoveNum > 0) {
                if (lastFlag == -1) {
                    lastFlag = 0;
                    isSameDragDirection = true;
                }
                if (lastFlag == 1) {
                    lastFlag = 0;
                    isSameDragDirection = !isSameDragDirection;
                }
                if (isSameDragDirection) {
                    holdPosition = indexCount + 1;
                } else {
                    if (indexStart < tempPosition) {
                        holdPosition = indexCount + 1;
                        isSameDragDirection = !isSameDragDirection;
                    } else {
                        holdPosition = indexCount;
                    }
                }
                xAbsOffset = 0;
                yAbsOffset = -chileViewHeight;
                indexCount++;
            }
            //向上drag
            else {
                if (lastFlag == -1) {
                    lastFlag = 1;
                    isSameDragDirection = true;
                }
                if (lastFlag == 0) {
                    lastFlag = 1;
                    isSameDragDirection = !isSameDragDirection;
                }
                if (isSameDragDirection) {
                    holdPosition = indexCount - 1;
                } else {
                    if (indexStart > tempPosition) {
                        holdPosition = indexCount - 1;
                        isSameDragDirection = !isSameDragDirection;
                    } else {
                        holdPosition = indexCount;
                    }
                }
                xAbsOffset = 0;
                yAbsOffset = chileViewHeight;
                indexCount--;
            }
            itemListener.itemHight(chileViewHeight);
            itemListener.itemSlideFlag(lastFlag);

            ViewGroup moveView = (ViewGroup) getChildAt(holdPosition - getFirstVisiblePosition());
            Animation animation;
            if (isSameDragDirection) {
                animation = getFromSelfAnimation(xAbsOffset, yAbsOffset);
            } else {
                animation = getToSelfAnimation(xAbsOffset, -yAbsOffset);
            }
            moveView.startAnimation(animation);
        }
    }

    // 停止移动item
    private void itemMoveStop() {
        itemListener.itemHide(-1);
        itemListener.notifyDataSetChanged();
    }

    //list滚动
    public void listScroller(int y) {
        int tempStep = 0;
        if (y < viewScrollUp) {
            // ListView需要下滑
            tempStep = step + (viewScrollUp - y) / 10;// 时时步伐
        } else if (y > viewScrollDown) {
            // ListView需要上滑
            tempStep = -(step + (y - viewScrollDown)) / 10;// 时时步伐
        }
        // 获取你拖拽滑动到位置及显示item相应的view上（注：可显示部分）（position）
        View view = getChildAt(indexMove - getFirstVisiblePosition());
        // 真正滚动的方法setSelectionFromTop()
        setSelectionFromTop(indexMove, view.getTop() + tempStep);

    }


    private int holdPosition;


    public Animation getFromSelfAnimation(int x, int y) {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, x,
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(ANIMATION_DURATION);
        animation.setInterpolator(new AccelerateInterpolator());
        return animation;
    }

    public Animation getToSelfAnimation(int x, int y) {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.ABSOLUTE, x, Animation.RELATIVE_TO_SELF, 0,
                Animation.ABSOLUTE, y, Animation.RELATIVE_TO_SELF, 0);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(ANIMATION_DURATION);
        animation.setInterpolator(new AccelerateInterpolator());
        return animation;
    }


}