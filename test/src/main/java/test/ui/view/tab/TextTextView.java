package test.ui.view.tab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/4/21.
 */
public class TextTextView extends TextView {
    public TextTextView(Context context, int index, int tabSize) {
        super(context);
        this.index = index;
        this.tabSize = tabSize;
    }

    public TextTextView(Context context) {
        super(context);
    }

    public TextTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    private Paint mBgPaint;
    private PaintFlagsDrawFilter pfd;
    private RectF rectF;

    private void init() {
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mBgPaint = new Paint();
        if (backgroundColor != 0) {
            mBgPaint.setColor(backgroundColor);
        }
        mBgPaint.setAntiAlias(true);
        rectF = new RectF();
        rectF.left = 0;
        rectF.top = 0;
        rectF.right = getWidth();
        rectF.bottom = getHeight();
    }

    private int tabSize;
    private int index;
    private int backgroundColor;

    public void setTabIndex(int index, int tabSize) {
        this.index = index;
        this.tabSize = tabSize;
    }

    @Override
    public void setBackgroundColor(int color) {
        backgroundColor = color;
        if (rectF != null) {
            mBgPaint.setColor(color);
            postInvalidate();
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (rectF == null) {
            init();
        }
        canvas.setDrawFilter(pfd);//给Canvas加上抗锯齿标志
        canvas.drawRoundRect(rectF, 20, 20, mBgPaint);
        float half = rectF.right / 2;
        if (index == 0) {
            canvas.drawRect(rectF.left + half, rectF.top, rectF.right, rectF.bottom, mBgPaint);
        }
        if (index == (tabSize - 1)) {
            canvas.drawRect(rectF.left, rectF.top, rectF.right - half, rectF.bottom, mBgPaint);
        }
        super.onDraw(canvas);

    }
}
