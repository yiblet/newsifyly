package me.yiblet.yiblet.newsifyly;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener{

    ListeningSearchView svSearch;
    ArrayList<Article> articles = new ArrayList<Article>();
    ArrayList<Article> TopStories;
    ArticleAdapter adapter;
    @BindView(R.id.rvResults)
    RecyclerView rvResults;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fmMenu) FloatingActionMenu fmMenu;
    @BindView(R.id.ablSearch)
    AppBarLayout ablSearch;
    @BindView(R.id.fbStartDate) FloatingActionButton fbStartDate;
    @BindView(R.id.fbEndDate) FloatingActionButton fbEndDate;
    @BindView(R.id.fbSortOrder) FloatingActionButton fbSortOrder;
    @BindView(R.id.fbCategories) FloatingActionButton fbCategories;
    @BindView(R.id.fbReset) FloatingActionButton fbReset;

    RecyclerView.OnScrollListener listener;
    String TAG = "SearchActivity";
    boolean Filtering = false;
    Query query;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvResults = (RecyclerView) findViewById(R.id.rvResults);

        fmMenu.hideMenuButton(false);
        fmMenu.setOpenedIcon(getResources().getDrawable(R.drawable.search));
        fmMenu.setClosedIcon(getResources().getDrawable(R.drawable.filter));

        adapter = new ArticleAdapter(articles, this);
        query = new Query();
        // Attach the adapter to the recyclerview to populate items
        rvResults.setAdapter(adapter);
        rvResults.addOnItemTouchListener(adapter);
//        rvResults.setNestedScrollingEnabled(false);





//        toolbar.setTitle("Top Stories");
        // Set layout manager to position the items
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int val;
        if (isLandscape) {
            val = 4;
        } else {
            val = 2;
        }
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(val, StaggeredGridLayoutManager.VERTICAL);
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

//        ablSearch.setExpanded(false, false);

//        fbFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                svSearch.requestFocus();
//                ablSearch.setExpanded(true);
//                Filtering = true;
//            }
//        });


        fbStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int y, int m, int d) {
                                String year = Integer.toString(y);
                                String month = String.format("%02d", m);
                                String day = String.format("%02d", d);
                                Date startDate = new Date(y, m, d);
                                if (query.getEndDate() == null || query.getEndDate().after(startDate)){
                                    fbStartDate.setLabelText(day + "/" + month + "/" + year);
                                    query.setStartDate(startDate);
                                    query.setStartDateString(year + month + day);
                                } else {
                                    Toast.makeText(SearchActivity.this, "Error: Start Date is After End Date", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        fbEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int y, int m, int d) {
                                String year = Integer.toString(y);
                                String month = String.format("%02d", m);
                                String day = String.format("%02d", d);
                                Date endDate = new Date(y, m, d);
                                if (query.getStartDate() == null || query.getStartDate().before(endDate)) {
                                    fbEndDate.setLabelText(day + "/" + month + "/" + year);
                                    query.setEndDate(endDate);
                                    query.setEndDateString(year + month + day);
                                } else {
                                    Toast.makeText(SearchActivity.this, "Error: End Date is Before Start Date", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        fbSortOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onCreateOptionsMenu: " + fmMenu.isOpened());
                if (query.getAscending() == null || query.getAscending().equals("false")) {
                    fbSortOrder.setLabelText("ascending");
                    fbSortOrder.setImageDrawable(getResources().getDrawable(R.drawable.up));
                    query.setAscending("true");
                } else {
                    fbSortOrder.setLabelText("descending");
                    fbSortOrder.setImageDrawable(getResources().getDrawable(R.drawable.down));
                    query.setAscending("false");
                }
            }
        });

        fbCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                CategoryDialogFragment dialog = CategoryDialogFragment.newInstance(query.getCategories());
                dialog.setPasser(new CategoryDialogFragment.PassData() {
                    @Override
                    public void passData(String categories) {
                        query.setCategories(categories);
                        fbCategories.setImageDrawable(getResources().getDrawable(R.drawable.checkbox));
                        fbCategories.setLabelText("Categories Chosen");
                    }

                    @Override
                    public void noData() {
                    }
                });
                dialog.show(fm, "category_dialog");
            }
        });

        fbReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String q = SearchActivity.this.query.getQ();
                int page = SearchActivity.this.query.getPage();
                resetFilter();
                query.setQ(q);
                query.forcePage(page);
            }
        });

        fmMenu.setButtonPressListener(new FloatingActionMenu.ButtonPressListener() {
            @Override
            public void onOpen(View v) {
                Log.d(TAG, "onOpen: " + "Menu opened");
            }

            @Override
            public void onClose(View v) {
                Log.d(TAG, "onClose: " + "Menu closed");
                if (query.isChanged()){
                    adapter.clear();
                    makeQuery();
                }
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

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                newScrollListener();
                resetFilter();
                query.setQ(q);
                queryNYT();
                svSearch.clearFocus();
                fmMenu.showMenuButton(true);
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
                Filtering = false;
                resetFilter();
                return true;
            }
        });
        svSearch.setActionViewListener(new ListeningSearchView.ActionViewListener() {
            @Override
            public void onActionViewCollapsed(View v) {
                Log.d(TAG, "onActionViewCollapsed: " + "collapsing");
//                ablSearch.setExpanded(false);
                Filtering = false;
                adapter.clear();
                resetFilter();
                getTopStories();
                fmMenu.hideMenuButton(true);
//                fbFilter.hide();
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


    @Override
    public boolean onQueryTextChange(String newText) {
        //implement the filterng techniques
        return false;
    }
    
    public void resetFilter(){
        fbEndDate.setLabelText(getResources().getString(R.string.fbEndDate));
        fbStartDate.setLabelText(getResources().getString(R.string.fbStartDate));
        fbSortOrder.setLabelText(getResources().getString(R.string.fbSortOrder));
        fbSortOrder.setImageDrawable(getResources().getDrawable(R.drawable.sort));
        fbCategories.setLabelText(getString(R.string.fbCategories));
        fbCategories.setImageDrawable(getResources().getDrawable(R.drawable.uncheckbox));
        query = new Query();
    }


    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            JSONArray rawArticles = null;
            try {
                rawArticles = response.getJSONObject("response").getJSONArray("docs");
//                    Log.d(TAG, "onSuccess: " + rawArticles.toString());
                int length = rawArticles.length();
                Log.d(TAG, "onSuccess: " + length);
                if (length == 0) Toast.makeText(SearchActivity.this,"No Matching Articles", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < length; i++) {
                    Article newArticle = new Article(rawArticles.getJSONObject(i));
                    articles.add(newArticle);
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
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private void makeQuery() {
        query.makeQuery(this, handler);
    }

    protected void queryNYT() {
        adapter.clear();
        makeQuery();
    }

    protected void addPages() {
//        RequestParams params = new RequestParams();
//        params.add("api_key", Config.NYT_API_KEY);
//        if (query.getQ() != null && ! query.getQ().equals("")) {
//            params.add("q", query.getQ());
//        }
//        query.setPage(query.getPage() + 1);
//        params.add("page", Integer.toString(this.query.getPage()));
        query.addPage();
        makeQuery();
    }

    private void dummy(){

    }

    final String TOP_STORIES = "https://api.nytimes.com/svc/topstories/v2/home.json";
    private void getTopStories() {
        if (TopStories == null) {
            TopStories = new ArrayList<Article>();
            this.setTitle("Top Stories");
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
                            TopStories.add(Article.fromTopArticle(rawTopArticles.getJSONObject(i)));
                        }
                        articles.clear();
                        articles.addAll(TopStories);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("SearchActivity", "onSuccess: " + e.toString());
                    }
                }
            });
        } else {
            articles.clear();
            articles.addAll(TopStories);
            resetFilter();
            adapter.notifyDataSetChanged();
        }
    }
}
