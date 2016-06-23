package me.yiblet.yiblet.newsifyly;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ListeningSearchView svSearch;
    ArrayList<Article> articles = new ArrayList<Article>();
    ArticleAdapter adapter;
    @BindView(R.id.rvResults) RecyclerView rvResults;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fbFilter) FloatingActionButton fbFilter;
    @BindView(R.id.ablSearch) AppBarLayout ablSearch;
    RecyclerView.OnScrollListener listener;
    String TAG = "SearchActivity";
    String lastQuery;
    int lastPage = 1;
    boolean Filtering = false;

    final String ARTICLE_SEARCH = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    final String TOP_STORIES = "https://api.nytimes.com/svc/topstories/v2/home.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvResults = (RecyclerView) findViewById(R.id.rvResults);

        adapter = new ArticleAdapter(articles, this);
        // Attach the adapter to the recyclerview to populate items
        rvResults.setAdapter(adapter);
        rvResults.addOnItemTouchListener(adapter);
//        rvResults.setNestedScrollingEnabled(false);



//        toolbar.setTitle("Top Stories");
        // Set layout manager to position the items
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(manager);
        ItemClickSupport.addTo(rvResults).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent i = new Intent(SearchActivity.this, ArticleActivity.class);
                        i.putExtra("url", articles.get(position).web_url);
                        i.putExtra("article", articles.get(position));
                        startActivity(i);
                    }
                }
        );

        ablSearch.setExpanded(false, false);

        fbFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                svSearch.requestFocus();
                ablSearch.setExpanded(true);
                Filtering = true;
            }
        });

        getTopStories();
    }



    private void newScrollListener(){
        final SearchActivity that = this;
        if (listener != null) {
            rvResults.removeOnScrollListener(listener);
        }
        listener = new EndlessRecyclerViewScrollListener((StaggeredGridLayoutManager) rvResults.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                that.addPages();
            }
        };
        rvResults.addOnScrollListener(listener);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.svSearch);
        svSearch = (ListeningSearchView) MenuItemCompat.getActionView(searchItem);
//        searchItem.
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                newScrollListener();
                queryNYT(query);

                svSearch.clearFocus();
                toolbar.setTitle(query);
                fbFilter.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "onClose: " + "closing");
                ablSearch.setExpanded(false);
                Filtering = false;
                return true;
            }
        });
        svSearch.setActionViewListener(new ListeningSearchView.ActionViewListener() {
            @Override
            public void onActionViewCollapsed(View v) {
                Log.d(TAG, "onActionViewCollapsed: " + "collapsing");
                ablSearch.setExpanded(false);
                Filtering = false;
                fbFilter.hide();
            }

            @Override
            public void onActionViewExpanded(View v) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
        return super.onOptionsItemSelected(item);
    }


    private void setupSearchView() {
        svSearch.setIconifiedByDefault(true);
        svSearch.setOnQueryTextListener(this);
        svSearch.setQueryHint("Search Here");
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        //implement the filterng techniques
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private void makeQuery(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(this, ARTICLE_SEARCH, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONArray rawArticles = null;
                try {
                    rawArticles = response.getJSONObject("response").getJSONArray("docs");
//                    Log.d(TAG, "onSuccess: " + rawArticles.toString());
                    int length = rawArticles.length();
                    Log.d(TAG, "onSuccess: " + length);
                    for (int i = 0; i < length; i++) {
                        Article newArticle = new Article(rawArticles.getJSONObject(i));
                        if (newArticle.thumbnail_url != null) {
                            articles.add(newArticle);
                        }
                    }
//                    Log.d(TAG, "onSuccess: " + articles.get(length - 1).toString());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("SearchActivity", "onSuccess: " + e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + responseString);
//                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) Log.e(TAG, "onFailure: " + errorResponse.toString());
            }
        });
    }

    protected void queryNYT(String query) {
        RequestParams params = new RequestParams();
        params.add("api_key", Config.NYT_API_KEY);
        if (query != null && (! query.equals(""))) {
            params.add("q", query);
        }
//        adapter.clear();
        articles.clear();
//        params.add("page", Integer.toString(lastPage));
        makeQuery(params);
    }

    protected void addPages() {
        RequestParams params = new RequestParams();
        params.add("api_key", Config.NYT_API_KEY);
        if (lastQuery != null && ! lastQuery.equals("")) {
            params.add("q", lastQuery);
        }
        lastPage++;
        params.add("page", Integer.toString(this.lastPage));
        makeQuery(params);
    }

    protected void queryNYT(){
        queryNYT("");
    }

    private void getTopStories() {
        RequestParams params = new RequestParams();
        params.add("api_key", Config.NYT_API_KEY);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, TOP_STORIES, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    JSONArray rawTopArticles = response.getJSONArray("results");
                    for (int i = 0; i < rawTopArticles.length(); i++) {
                        articles.add(Article.fromTopArticle(rawTopArticles.getJSONObject(i)));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("SearchActivity", "onSuccess: " + e.toString());
                }
            }
        });
    }
}
