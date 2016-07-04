package test.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import activity.ui.com.test.R;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ExpListAdapter extends BaseExpandableListAdapter {
    public ArrayList<String> groups = new ArrayList<String>();
    public ArrayList<ArrayList<String>> childs = new ArrayList<ArrayList<String>>();

    public ExpListAdapter() {
        for (int i = 0; i < 20; i++) {
            groups.add(i + "");
        }

        childs.add(groups);
        childs.add(groups);
        childs.add(groups);
        childs.add(groups);
        childs.add(groups);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int size=0;
        if(groupPosition<childs.size()){
            size= childs.get(groupPosition).size();
        }
        return  size;
    }

    @Override
    public Object getGroup(int groupPosition) {

        return  groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView=  LayoutInflater.from(parent.getContext()).inflate(R.layout.exp_list_group,null);
        TextView titleTv = (TextView) convertView.findViewById(R.id.group_title_tv);
         titleTv.setText(groups.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView=  LayoutInflater.from(parent.getContext()).inflate(R.layout.exp_list_child,null);
        TextView titleTv = (TextView) convertView.findViewById(R.id.child_title_tv);
        titleTv.setText(childs.get(groupPosition).get(childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
