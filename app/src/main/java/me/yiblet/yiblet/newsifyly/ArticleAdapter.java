package me.yiblet.yiblet.newsifyly;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yiblet on 6/20/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleHolder> implements RecyclerView.OnItemTouchListener{

    List<Article> articles;
    SearchActivity searchActivity;

    public ArticleAdapter(List<Article> articles, SearchActivity searchActivity) {
        this.searchActivity = searchActivity;
        this.articles = articles;
    }

    public void clear(){
        articles.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public ArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View ArticleView = inflater.inflate(R.layout.item_article, parent, false);
        return new ArticleHolder(ArticleView);
    }

    @Override
    public void onBindViewHolder(ArticleHolder holder, int position) {
        Article article = articles.get(position);
        Glide.with(holder.ivThumb.getContext()).load(article.thumbnail_url).into(holder.ivThumb);
        holder.tvHeadline.setText(article.headline);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    static class ArticleHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivThumb) ImageView ivThumb;
        @BindView(R.id.tvHeadline) TextView tvHeadline;

        public ArticleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
