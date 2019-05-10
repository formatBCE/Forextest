package ua.pp.formatbce.forextest.view.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import ua.pp.formatbce.forextest.misc.SubscriptionType;
import ua.pp.formatbce.forextest.model.Subscription;
import ua.pp.formatbce.forextest.service.MainService;

@InjectViewState
public class MainPresenter extends MvpPresenter<IMainView> {

    @NonNull
    private final Context appContext;
    private boolean isWatchingInBackground;
    private MainAdapter adapter;
    @Nullable
    private MainService service;
    @NonNull
    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainPresenter.this.service = ((MainService.Binder) service).getService();
            final List<SubscriptionType> activeSubscriptions = MainPresenter.this.service.getActiveSubscriptions();
            if (!activeSubscriptions.isEmpty()) {
                watchInBackground();
                for (SubscriptionType type : activeSubscriptions) {
                    addSubscription(type);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MainPresenter.this.service = null;
        }
    };

    public MainPresenter(@NonNull Context appContext) {
        this.appContext = appContext;
        adapter = new MainAdapter(this::onSubscriptionClicked, this::onSubscriptionCancelled);
        final Intent service = new Intent(appContext, MainService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(service);
        }
        appContext.bindService(service, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void attachView(IMainView view) {
        super.attachView(view);
        view.setAdapter(adapter);
    }

    private void onSubscriptionClicked(Subscription subscription) {
        getViewState().openDetailsFor(subscription);
    }

    private void onSubscriptionCancelled(Subscription subscription) {
        if (service != null) {
            service.cancelSubscription(subscription.getType());
        }
        adapter.removeItem(subscription.getType());
    }


    void attemptToAddSubscription() {
        final SubscriptionType type;
        switch (adapter.getItemCount()) {
            case 0:
                type = SubscriptionType.BTCEUR;
                break;
            case 1:
                type = adapter.getItem(0).getType() == SubscriptionType.BTCEUR
                        ? SubscriptionType.BTCUSD
                        : SubscriptionType.BTCEUR;
                break;
            default:
                // we have just 2 subscriptions
                return;
        }
        addSubscription(type);
    }

    private void addSubscription(@NonNull SubscriptionType type) {
        if (service != null) {
            adapter.addItem(type);
            service.addSubscription(type);
        }
    }

    boolean isWatchingInBackground() {
        return isWatchingInBackground;
    }

    void watchInBackground() {
        isWatchingInBackground = true;
        getViewState().onWatchStatusChanged();
    }

    void unwatchInBackground() {
        isWatchingInBackground = false;
        getViewState().onWatchStatusChanged();
    }

    @Override
    public void onDestroy() {
        if (!isWatchingInBackground && service != null) {
            service.stop();
        }
        appContext.unbindService(connection);
        super.onDestroy();
    }
}
