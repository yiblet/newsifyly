package me.yiblet.yiblet.newsifyly;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Date;

/**
 * Created by yiblet on 6/23/16.
 */
public class Query implements Parcelable {
    private Date startDate;
    private Date endDate;
    private String ascending;
    private String q;
    private int page = 1;
    private String startDateString;
    private String endDateString;
    private String categories;
    private boolean filtering = false;
    private boolean changed = false;
    private boolean ChangedFilter = false;

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        filtering = true;
        changed = true;
        this.categories = categories;
    }

    protected void forcePage(int page) {
        this.page = page;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        filtering = true;
        changed = true;
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        filtering = true;
        changed = true;
        this.endDate = endDate;
    }

    public String getAscending() {
        return ascending;
    }

    public void setAscending(String ascending) {
        filtering = true;
        changed = true;
        this.ascending = ascending;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        changed = true;
        this.q = q;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        changed = true;
        this.page = page;
    }

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        changed = true;
        filtering = true;
        this.startDateString = startDateString;
    }

    public String getEndDateString() {
        return endDateString;
    }

    public void setEndDateString(String endDateString) {
        changed = true;
        filtering = true;
        this.endDateString = endDateString;
    }

    public Query() {
    }

    private RequestParams getParams() {
        RequestParams results = new RequestParams();
        results.add("api_key", Config.NYT_API_KEY);
        if (filtering) {
            if (startDateString != null && ! startDateString.equals("")) {
                results.add("begin_date", startDateString);
            }
            if (endDateString != null && ! endDateString.equals("")) {
                results.add("end_date", endDateString);
            }
            if (ascending != null && ! ascending.equals("")) {
                if (ascending.equals("true")) {
                    results.add("sort", "newest");
                } else if (ascending.equals("false")) {
                    results.add("sort", "oldest");
                }
            }
            if (this.q != null && (! this.q.equals(""))) {
                results.add("q", this.q);
            }
            if (this.categories != null && categories != "") {
                results.add("fq", "news_desk:(" + categories + ")");
            }
        } else {
            if (this.q != null && (! this.q.equals(""))) {
                results.add("q", this.q);
            }
        }
        results.add("page", Integer.toString(this.page));
        return results;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.startDate != null ? this.startDate.getTime() : -1);
        dest.writeLong(this.endDate != null ? this.endDate.getTime() : -1);
        dest.writeString(this.ascending);
        dest.writeString(this.q);
        dest.writeInt(this.page);
        dest.writeString(this.startDateString);
        dest.writeString(this.endDateString);
        dest.writeString(this.categories);
    }

    protected Query(Parcel in) {
        long tmpStartDate = in.readLong();
        this.startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        this.ascending = in.readString();
        this.q = in.readString();
        this.page = in.readInt();
        this.startDateString = in.readString();
        this.endDateString = in.readString();
        this.categories = in.readString();
    }

    public static final Parcelable.Creator<Query> CREATOR = new Parcelable.Creator<Query>() {
        @Override
        public Query createFromParcel(Parcel source) {
            return new Query(source);
        }

        @Override
        public Query[] newArray(int size) {
            return new Query[size];
        }
    };

    protected void makeQuery(Context context, JsonHttpResponseHandler handler) {
        Log.d("Query", "makeQuery: " + "page=" + this.page);
        if (changed) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(context, ARTICLE_SEARCH, getParams(), handler);
        }
        changed = false;
        pageChanged = false;
    }

    boolean pageChanged = false;


    protected void  addPage() {
        changed = true;
        pageChanged = true;
        this.page = page + 1;
    }

    public boolean isChanged() {
        return changed;
    }

    final String ARTICLE_SEARCH = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
}
