package test.ui.view.tab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/21.
 */
public class TabLinearLayout extends LinearLayout {
    public TabLinearLayout(Context context) {
        super(context);
    }

    public TabLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TabLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //0:选中的颜色，1：非选中的颜色
    private int[] tabBackgroundColors = new int[]{0xffffffff, 0xff3496E1};
    //0:选中的颜色，1：非选中的颜色
    private int[] textColors = new int[]{0xff3496E1, 0xffffffff};
    //背景色
    private int layoutBackgroundColors = 0xff3496E1;

    public void setColors(int[] tabBackgroundColors, int[] textColors, int layoutBackgroundColors) {
        this.tabBackgroundColors = tabBackgroundColors;
        this.textColors = textColors;
        this.layoutBackgroundColors = layoutBackgroundColors;
    }

    private DisplayMetrics displaysMetrics;
    private int tabWidth;
    private int tabHeight;

    public void setView(int tabWidth, int tabHeight, int tabSize) {
        this.tabWidth = tabWidth;
        this.tabHeight = tabHeight;
        setOrientation(LinearLayout.HORIZONTAL);
        displaysMetrics = getContext().getResources().getDisplayMetrics();
        //
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, displaysMetrics);
        int paramsWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                tabWidth * tabSize, displaysMetrics) + 2 * padding;
        int paramsHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                tabHeight, displaysMetrics) + 2 * padding;
        //
        ViewGroup.LayoutParams layuotGroupParams = getLayoutParams();
        if (layuotGroupParams != null) {
            layuotGroupParams.width = paramsWidth;
            layuotGroupParams.height = paramsHeight;
            setLayoutParams(layuotGroupParams);
        } else {
            ViewGroup.LayoutParams viewGroupParams = new ViewGroup.LayoutParams(paramsWidth, paramsHeight);
            setLayoutParams(viewGroupParams);
        }
        setPadding(padding, padding, padding, padding);
        //
        for (int i = 0; i < tabSize; i++) {
            TextTextView tabView = getTabView(i, tabSize);
            tabView.setId(i);
            addView(tabView);
            tabView.setOnClickListener(tabClickListener);
        }
        setBackgroundColor(0x0000);
    }

    private LinearLayout.LayoutParams tabParams;

    private LinearLayout.LayoutParams getTabParams() {
        if (tabParams == null) {
            float w = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    tabWidth, displaysMetrics);
            float h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    tabHeight, displaysMetrics);
            tabParams = new LinearLayout.LayoutParams((int) w, (int) h);
        }
        return tabParams;
    }

    //获取tabView
    private TextTextView getTabView(int index, int size) {
        TextTextView textView = new TextTextView(getContext());
        textView.setTabIndex(index, size);
        setTabSelect(textView, index == tabSelectIndex);
        textView.setLayoutParams(getTabParams());
        if (index < txts.size()) {
            textView.setText(txts.get(index));
        }
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    //设置是否选中
    private void setTabSelect(TextTextView textView, boolean isSelect) {
        if (isSelect) {
            textView.setBackgroundColor(tabBackgroundColors[0]);
            textView.setTextColor(textColors[0]);
        } else {
            textView.setBackgroundColor(tabBackgroundColors[1]);
            textView.setTextColor(textColors[1]);
        }
    }

    private ArrayList<String> txts = new ArrayList<String>();

    public void setText(int index, String txt) {
        int count = getChildCount();
        if (count == 0) {
            txts.add(index, txt);
            return;
        }
        if (index >= count) {
            txts.add(index, txt);
            return;
        }
        TextTextView textView = (TextTextView) getChildAt(index);
        textView.setText(txt);
    }

    private Paint mBgPaint;
    private PaintFlagsDrawFilter pfd;
    private RectF rectF;

    private void init() {
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mBgPaint = new Paint();
        if (layoutBackgroundColors != 0) {
            mBgPaint.setColor(layoutBackgroundColors);
        }
        mBgPaint.setAntiAlias(true);
        rectF = new RectF();
        rectF.left = 0;
        rectF.top = 0;
        rectF.right = getWidth();
        rectF.bottom = getHeight();
        if (rectF.right == 0) {
            rectF = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (rectF == null) {
            init();
        }
        if (rectF != null) {
            canvas.setDrawFilter(pfd);//给Canvas加上抗锯齿标志
            canvas.drawRoundRect(rectF, 20, 20, mBgPaint);
        }
        super.onDraw(canvas);
    }

    private TabClickListener tabClickListener = new TabClickListener();
    private int tabSelectIndex = 0;

    //tabView点击监听
    class TabClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int selectIndex = v.getId();
            if (tabSelectIndex == selectIndex) {
                return;
            }
            tabSelectIndex = selectIndex;
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                TextTextView textView = (TextTextView) getChildAt(i);
                setTabSelect(textView, selectIndex == i);
            }
            if (onTabCutListener != null) {
                onTabCutListener.OnTabCut(selectIndex);
            }
        }
    }

    private OnTabCutListener onTabCutListener;

    private void setOnTabCutListener(OnTabCutListener onTabCutListener) {
        this.onTabCutListener = onTabCutListener;
    }

    public interface OnTabCutListener {
        public void OnTabCut(int index);
    }
}
