package test.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import activity.ui.com.test.R;
import test.ui.able.ItemListener;

/***
 * 自定义适配器
 *
 * @author zhangjia
 */
public class MoveListAdapter extends BaseAdapter {
    private static final String TAG = "DragListAdapter";
    private ArrayList<String> datas;
    private Context context;
    public boolean isHidden;
    private ArrayList<String> datasTemp = new ArrayList<String>();

    public MoveListAdapter(Context context, ArrayList<String> datas) {
        this.context = context;
        this.datas = datas;
        datasTemp.clear();
        for (String str : datas) {
            datasTemp.add(str);
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Hodle {
        public TextView textView;
        public ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Hodle hodle=null;
        if(convertView!=null){
            hodle = (Hodle) convertView.getTag();
        }
        if (isHodle||hodle==null||convertView==null) {
            hodle = new Hodle();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.drag_list_item, null);
            hodle.textView = (TextView) convertView
                    .findViewById(R.id.drag_list_item_text);
            hodle.imageView = (ImageView) convertView
                    .findViewById(R.id.drag_list_item_image);
        }
        if(!isHodle){
            convertView.setTag(hodle);
        }
        if (position == itemIndex) {
             convertView.findViewById(R.id.hint_view).setVisibility(
                    View.INVISIBLE);
            return convertView;
        }
        hodle.textView.setText(datas.get(position));
        if (lastFlag == 1) {
            if (position > itemIndex) {
                Animation animation;
                animation = getFromSelfAnimation(0, -height);
                // convertView.startAnimation(animation);
            }
        }
        if (lastFlag == 0) {
            if (position < itemIndex) {
                Animation animation;
                animation = getFromSelfAnimation(0, height);
                //  convertView.startAnimation(animation);
            }
        }

        return convertView;
    }


    // 交换item
    public void itemExchange(int startPosition, int endPosition) {
        String copyBean = datasTemp.get(startPosition);
        if (startPosition < endPosition) {
            datasTemp.add(endPosition + 1, copyBean);
            datasTemp.remove(startPosition);
        } else {
            datasTemp.add(endPosition, copyBean);
            datasTemp.remove(startPosition + 1);
        }

    }

    public Animation getFromSelfAnimation(int x, int y) {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, x,
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(100);
        animation.setInterpolator(new AccelerateInterpolator());
        return animation;
    }

    public ItemListener getItemListener() {
        return itemListener;
    }

    private ItemChangeListener itemListener = new ItemChangeListener();
    private int itemIndex = -1;
    private int lastFlag = -1;
    private int height;
    private boolean isHodle;
    class ItemChangeListener implements ItemListener {

        @Override
        public void itemHide(int itemIndex) {
            isHodle=true;
            MoveListAdapter.this.itemIndex = itemIndex;
        }

        @Override
        public void itemExchange(int startItem, int endItem) {

            MoveListAdapter.this.itemExchange(startItem, endItem);
        }

        @Override
        public void itemSlideComplete() {
            datas.clear();
            for (String str : datasTemp) {
                datas.add(str);
            }
        }

        @Override
        public void itemHight(int height) {
            MoveListAdapter.this.height = height;
        }

        @Override
        public int getTouchViewId() {
            return R.id.drag_list_item_image;
        }

        @Override
        public void itemSlideFlag(int flag) {
            MoveListAdapter.this.lastFlag = flag;
        }

        @Override
        public void notifyDataSetChanged() {
            MoveListAdapter.this.notifyDataSetChanged();
        }
    }
}