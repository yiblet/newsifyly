package me.yiblet.yiblet.newsifyly;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {
    @BindView(R.id.wbArticle) ObservableWebView wbArticle;
    @BindView(R.id.fbBack) FloatingActionButton fbBack;
    @BindView(R.id.fbShare) FloatingActionButton fbShare;


    protected String url;
    protected Article article;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        wbArticle = (ObservableWebView) findViewById(R.id.wbArticle);
        Intent past = getIntent();
        url = past.getStringExtra("url");
//        article = past.getParcelableExtra("article");
//        this.setTitle(article.headline);
        ButterKnife.bind(this);

        clearCookies();
        wbArticle.getSettings().setLoadsImagesAutomatically(true);
        wbArticle.getSettings().setJavaScriptEnabled(true);
        wbArticle.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Configure the client to use when opening URLs
        wbArticle.setWebViewClient(new Browser());
        // Load the initial URL
        wbArticle.loadUrl(url);

//        fbBack = (FloatingActionButton) findViewById(R.id.fbBack);
        final ArticleActivity that = this;
        fbBack.hide();
        fbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                that.onBackPressed();
            }
        });

        fbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setData(Uri.parse(url));
                share.setType("text/plain");
//                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                if (article != null && article.headline != null){
                    share.putExtra(Intent.EXTRA_TITLE, article.headline);
                }
                share.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(share, "Share Link!"));
            }
        });
    }

    @SuppressWarnings("deprecation")
    private  void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("ArticleActivity", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d("ArticleActivity", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private static class Browser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


}
