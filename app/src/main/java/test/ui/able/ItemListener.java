package test.ui.able;

/**
 * Created by Administrator on 2016/4/26.
 */
public interface ItemListener {
    /**
     * 隐藏item
     */
    public void itemHide(int itemIndex);

    /**
     * item的高度
     */
    public void itemHight(int height);

    /**
     * 交换item
     */
    public void itemExchange(int startItem, int endItem);

    /**
     * 滑动完成
     */
    public void itemSlideComplete();

    /**
     * 触摸可以触发移动的控件id
     */
    public int getTouchViewId();
    /**滑动状态 -1,0 == down,1== up*/
    public void  itemSlideFlag(int flag);
    public void notifyDataSetChanged();
}
