package test.ui.actvivty;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import activity.ui.com.test.R;
import test.ui.adapter.ExpListAdapter;
import test.ui.view.expansion.ExpListView;
import test.ui.view.expansion.ExpansionListListener;
import test.ui.view.expansion.MainExpandableListView;

public class TestExpListActivity extends AppCompatActivity implements
        ExpansionListListener {
    private LinearLayout headView;
    private ExpListAdapter adapter;
    private ExpListView lisElv;
    private MainExpandableListView mainListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_list);
        test();
    }

    private void test() {
        lisElv = (ExpListView) findViewById(R.id.list_elv);
        adapter = new ExpListAdapter();
        TextView testTv = new TextView(this);
        testTv.setText("123");
        TextView testTv2 = new TextView(this);
        testTv2.setText("123456");
        lisElv.addHeaderView(testTv);
        lisElv.addHeaderView(testTv2);
        lisElv.setAdapter(adapter);
        lisElv.setTopViewLayoutId(R.layout.exp_list_group, this);
        lisElv.setPullRenovation();
        lisElv.setListener(this);
    }

    private void text1() {
        mainListView = (MainExpandableListView) findViewById(R.id.list_elv);
        adapter = new ExpListAdapter();
        mainListView.setAdapter(adapter);
        mainListView.setPullRenovation();
    }

    @Override
    public void topViewClick(View view, int groupIndex) {
        boolean isOPen = lisElv.isGroupExpanded(groupIndex);
        if (isOPen) {
            lisElv.collapseGroup(groupIndex);
        } else {
            lisElv.expandGroup(groupIndex);
        }
        lisElv.setSelectedGroup(groupIndex);
    }

    @Override
    public void topViewData(View view, int groupIndex) {
        TextView headTv = (TextView) view.findViewById(R.id.group_title_tv);
        String title = adapter.groups.get(groupIndex);
        headTv.setText(title);
    }

    @Override
    public void onPullRefresh() {
        han.sendEmptyMessageDelayed(1, 1000);
    }

    private Handler han = new Han();

    class Han extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            lisElv.onRenovationComplete();
        }
    }
}
