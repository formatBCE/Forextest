package ua.pp.formatbce.forextest.view.main;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ua.pp.formatbce.forextest.model.Subscription;

@StateStrategyType(SkipStrategy.class)
public interface IMainView extends MvpView {

    void setAdapter(MainAdapter adapter);

    void onWatchStatusChanged();

    void openDetailsFor(Subscription subscription);
}
