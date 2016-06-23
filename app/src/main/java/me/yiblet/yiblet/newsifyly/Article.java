package me.yiblet.yiblet.newsifyly;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yiblet on 6/20/16.
 */
public class Article implements Parcelable {
    protected JSONObject raw;
    private boolean hasRaw = false;
    String web_url;
    String lead_paragraph;
    String thumbnail_url;
    String wide_url;
    String large_url;
    String headline;
    int word_count;

    public Article(JSONObject raw) {
        this.raw = raw;
        hasRaw = true;
        try {
            web_url = raw.getString("web_url");
            lead_paragraph = raw.getString("lead_paragraph");
            JSONArray mult = raw.getJSONArray("multimedia");
            for (int i = 0; i < mult.length(); i++) {
                JSONObject item = mult.getJSONObject(i);
                switch (item.getString("subtype")) {
                    case "wide":
                        wide_url = "http://www.nytimes.com/" + item.getString("url");
                        break;
                    case "xlarge":
                        large_url = "http://www.nytimes.com/" + item.getString("url");
                        break;
                    case "large":
                        large_url = "http://www.nytimes.com/" + item.getString("url");
                        break;
                    case "thumbnail":
                        thumbnail_url = "http://www.nytimes.com/" + item.getString("url");
                        break;
                    default:
                        Log.d("Article", "Article: unexpected image type " + item.getString("subtype"));
                        break;
                }
            }
            headline = raw.getJSONObject("headline").getString("main");
            word_count = raw.getInt("word_count");
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.e("Article", "Article: " + e.toString());
        }
    }

    @Override
    public String toString() {
        if (hasRaw){
            return raw.toString();
        }
        return super.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.web_url);
        dest.writeString(this.lead_paragraph);
        dest.writeString(this.thumbnail_url);
        dest.writeString(this.wide_url);
        dest.writeString(this.large_url);
        dest.writeString(this.headline);
        dest.writeInt(this.word_count);
    }

    protected Article(Parcel in) {
//        this.raw = in.readParcelable(JSONObject.class.getClassLoader());
        this.hasRaw = false;
        this.web_url = in.readString();
        this.lead_paragraph = in.readString();
        this.thumbnail_url = in.readString();
        this.wide_url = in.readString();
        this.large_url = in.readString();
        this.headline = in.readString();
        this.word_count = in.readInt();
    }
    

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    
    private Article(){
        
    }

    public static Article fromTopArticle(JSONObject topArticle) throws JSONException {
        Article article =  new Article();
        article.hasRaw = false;
        article.web_url = topArticle.getString("url");
        article.lead_paragraph = null;
        JSONArray mult = topArticle.getJSONArray("multimedia");
        for (int i = 0; i < mult.length(); i++) {
            JSONObject item = mult.getJSONObject(i);
            switch (item.getString("format")) {
                case "wide":
                    article.wide_url =item.getString("url");
                    break;
                case "xlarge":
                    article.large_url = item.getString("url");
                    break;
                case "large":
                    article.large_url = item.getString("url");
                    break;
                case "thumbnail":
                    article.thumbnail_url = item.getString("url");
                    break;
                case "thumbLarge":
                    article.thumbnail_url = item.getString("url");
                    break;
                case "Standard Thumbnail":
                    article.thumbnail_url = item.getString("url");
                    break;
                default:
                    Log.d("Article", "Article: unexpected image type " + item.getString("subtype"));
                    break;
            }
        }
        article.headline = topArticle.getString("title");
//        int word_count;
        return article;
    }
}
