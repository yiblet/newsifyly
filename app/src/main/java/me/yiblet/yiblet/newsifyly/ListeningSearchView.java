package me.yiblet.yiblet.newsifyly;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yiblet on 6/22/16.
 */
public class ListeningSearchView extends SearchView {


    private ActionViewListener actionViewListener;

    public ListeningSearchView(Context context) {
        super(context);
    }

    public ListeningSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListeningSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onActionViewCollapsed() {
        if (actionViewListener != null) {
            actionViewListener.onActionViewCollapsed(this);
        }
        super.onActionViewCollapsed();
    }

    public ActionViewListener getActionViewListener() {
        return actionViewListener;
    }

    public void setActionViewListener(ActionViewListener actionViewListener) {
        this.actionViewListener = actionViewListener;
    }

    @Override
    public void onActionViewExpanded() {
        if (actionViewListener != null) {
            actionViewListener.onActionViewExpanded(this);
        }
        super.onActionViewExpanded();
    }

    public interface ActionViewListener{

        void onActionViewCollapsed(View v);

        void onActionViewExpanded(View v);
    }
}
