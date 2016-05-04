package test.ui.actvivty;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import activity.ui.com.test.R;
import test.ui.adapter.MoveListAdapter;
import test.ui.view.MoveListView;

public class TestListActivity extends AppCompatActivity implements View.OnClickListener {


    private MoveListView listLv;
    private MoveListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);
        findViewById(R.id.test_move_btn).setOnClickListener(this);
        listLv = (MoveListView) findViewById(R.id.list_lv);
        listAdapter = new MoveListAdapter(this, initData());
        listLv.setAdapter(listAdapter);
        listLv.setOnItemListener(listAdapter.getItemListener());
    }

    public ArrayList<String> initData() {
        //数据结果
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < 4; i++) {
            data.add("A选项" + i);
        }
        return data;
    }

    private View view0;
    private int move = 10;

    @Override
    public void onClick(View v) {
        listAdapter.getItem(0);
        if (view0 == null) {
            view0 = listLv.getChildAt(0);
        }
        ViewCompat.setTranslationY(view0, move);
        move += 10;
    }

}
