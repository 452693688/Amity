package test.ui.actvivty;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import activity.ui.com.test.R;
import test.ui.view.tab.TabLinearLayout;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView testTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TabLinearLayout tabLayout = (TabLinearLayout) findViewById(R.id.tab_layout);

        tabLayout.setView(80, 28, 2);
        tabLayout.setText(1,"123");
        tabLayout.setText(0,"456");
        //testTv = (TextView) findViewById(R.id.test_tv);
        // testTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String str = testTv.getText().toString();
        str += str;
        testTv.setText(str);
        int count = testTv.getLineCount();
        int height = testTv.getLineHeight();
        Toast.makeText(this, "count:" + count + " height:" + height, Toast.LENGTH_LONG).show();
    }
}
