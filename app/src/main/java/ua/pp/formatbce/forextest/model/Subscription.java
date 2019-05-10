package ua.pp.formatbce.forextest.model;

import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import ua.pp.formatbce.forextest.misc.EventDataUpdate;
import ua.pp.formatbce.forextest.misc.ISimpleListener;
import ua.pp.formatbce.forextest.misc.SubscriptionType;

public class Subscription {
    private final SubscriptionType type;
    private WeakReference<ISimpleListener> listener;
    private UpdateChunk lastData;

    public Subscription(SubscriptionType type) {
        this.type = type;
    }

    public SubscriptionType getType() {
        return type;
    }

    public void setDataUpdateListener(ISimpleListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    @Nullable
    public UpdateChunk getLatestData() {
        return lastData;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Subscription)) {
            return false;
        }
        return ((Subscription) obj).type == type;
    }

    public void subscribe() {
        EventBus.getDefault().register(this);
    }

    public void unsubscribe() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDataUpdate(EventDataUpdate dataUpdate) {
        if (dataUpdate.type == type) {
            this.lastData = dataUpdate.chunk;
            if (listener != null) {
                ISimpleListener l = listener.get();
                if (l != null) {
                    l.call();
                }
            }
        }
    }
}
