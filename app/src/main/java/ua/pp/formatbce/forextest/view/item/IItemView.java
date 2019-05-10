package ua.pp.formatbce.forextest.view.item;

import android.support.v7.widget.RecyclerView;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(SkipStrategy.class)
public interface IItemView extends MvpView {
    void setAdapter(RecyclerView.Adapter adapter);
}
