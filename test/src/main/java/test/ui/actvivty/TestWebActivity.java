package test.ui.actvivty;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import activity.ui.com.test.R;

public class TestWebActivity extends AppCompatActivity {

    private WebView contentWebView;
    private String imag = "http://d.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=603e37439313b07ebde8580c39e7bd15/a8014c086e061d9591b7875a7bf40ad163d9cadb.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_web);
        contentWebView = (WebView) findViewById(R.id.wv);
        // 启用javascript
        contentWebView.getSettings().setJavaScriptEnabled(true);
        // 随便找了个带图片的网站
        //  contentWebView.loadUrl(str);
        loadingLocalityImage();
        // 添加js交互接口类，并起别名 imagelistner
        contentWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
        contentWebView.setWebViewClient(new MyWebViewClient());
    }
    //加载本地图片
    private void loadingLocalityImage() {
        String test1 = "<body>"+"<img src=\"zan_false.png\"/>"+"<img src=\""+imag+"\""+"/>"+"</body>";
        String test2 = "<body>12123124343412312</body>"
                +"<body>"
                +"<div align=\"center\">"
                +"<img src=\"zan_false.png\" style=\"position:relative; top:40px\" />"
                +"</br>"
                +"<font color=\"#999999\" size=\"2\" style=\"position:relative; top:45px; bottom:20px \">"
                +"我字体大小为2"
                +"</font>"
                +"</div>"
                +"</body>";

        contentWebView.loadDataWithBaseURL("file:///android_asset/",test2 , "text/html", "utf-8",null);

    }

    // 注入js函数监听
    private void addImageClickListner() {
         contentWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    // js通信接口

    class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            Toast.makeText(TestWebActivity.this, img, Toast.LENGTH_LONG).show();
        }
    }

    // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            view.getSettings().setJavaScriptEnabled(true);

            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();

        }

    }
}
