package me.yiblet.yiblet.newsifyly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

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

    final int GREEN = Color.rgb(0x33, 0x69, 0x1E);
    final int ORANGE = Color.rgb(0xE6, 0x51, 0x00);
    final int BROWN = Color.rgb(0x4E, 0x34, 0x2E);
    final int GREY = Color.rgb(0x42, 0x42, 0x42);
    final int BLUE = Color.rgb(0x37, 0x47, 0x4F);
    final int[] list = {GREEN, ORANGE, BROWN, GREY, BLUE};

    private int pickRandomColor() {
        Random rn = new Random();
        return list[rn.nextInt(list.length)];
    }

    @Override
    public void onBindViewHolder(ArticleHolder holder, int position) {
        Article article = articles.get(position);
        Bitmap image = Bitmap.createBitmap(300, 192, Bitmap.Config.ARGB_8888);
        View v  = holder.ivThumb;
        image.eraseColor(pickRandomColor());

        if (article.thumbnail_url != null && article.thumbnail_url != "") {
            Glide.with(holder.ivThumb.getContext()).load(article.thumbnail_url)
                    .centerCrop()
                    .into(holder.ivThumb);
        } else
        if (article.large_url != null && article.thumbnail_url != ""){
            Glide.with(holder.ivThumb.getContext()).load(article.large_url)
                    .centerCrop()
                    .into(holder.ivThumb);
        } else if (article.wide_url != null && article.wide_url != "") {
            Glide.with(holder.ivThumb.getContext()).load(article.wide_url)
                    .centerCrop()
                    .into(holder.ivThumb);
        } else {
            holder.ivThumb.setImageBitmap(image);
        }
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
