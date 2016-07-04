package test.ui.actvivty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import activity.ui.com.test.R;


public class TestMainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        findViewById(R.id.test_activity_btn).setOnClickListener(this);
        findViewById(R.id.test_list_activity_btn).setOnClickListener(this);
        findViewById(R.id.test_expList_activity_btn).setOnClickListener(this);
        findViewById(R.id.test_webview_activity_btn).setOnClickListener(this);
        findViewById(R.id.test_copy_activity_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent it = new Intent();
        switch (v.getId()) {
            case R.id.test_activity_btn:
                it.setClass(this, TestActivity.class);
                break;
            case R.id.test_list_activity_btn:
                it.setClass(this, TestListActivity.class);
                break;
            case R.id.test_expList_activity_btn:
                it.setClass(this, TestExpListActivity.class);
                break;
            case R.id.test_webview_activity_btn:
                it.setClass(this, TestWebActivity.class);
                break;
            case R.id.test_copy_activity_btn:
                it.setClass(this, TestCopyActivity.class);
                break;
        }
        startActivity(it);
    }
}
