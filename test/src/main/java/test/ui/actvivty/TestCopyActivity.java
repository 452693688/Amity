package test.ui.actvivty;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import activity.ui.com.test.R;

public class TestCopyActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView copyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_copy);
        findViewById(R.id.copy_btn).setOnClickListener(this);
        copyTv = (TextView) findViewById(R.id.copy_tv);
    }

    @Override
    public void onClick(View v) {
        ClipboardManager cm= (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData pc = cm.getPrimaryClip();
        ClipData.Item item = pc.getItemAt(0);
        String htmlText=item.getHtmlText();
        CharSequence text = item.getText();
        Uri uri = item.getUri();
        String str="html:"+htmlText+"\n"
                +"text:"+text+"\n"
                +"uri:"+uri;
        copyTv.setText(str);
    }
}
