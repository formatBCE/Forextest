package ua.pp.formatbce.forextest.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.SparseArray;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.pp.formatbce.forextest.ForexApp;
import ua.pp.formatbce.forextest.R;
import ua.pp.formatbce.forextest.misc.EventDataUpdate;
import ua.pp.formatbce.forextest.misc.SubscriptionType;
import ua.pp.formatbce.forextest.model.SubscriptionMessage;
import ua.pp.formatbce.forextest.model.UpdateChunk;
import ua.pp.formatbce.forextest.view.main.MainActivity;
import ua.pp.formatbce.forextest.websocket.SocketConnection;

public class MainService extends Service {

    private static final int MAIN_SERVICE_NOTIFICATION = 1;
    private final Binder binder = new Binder();
    private SocketConnection connection;
    private final Map<SubscriptionType, List<UpdateChunk>> subscriptions = new HashMap<>();
    private final SparseArray<SubscriptionType> bindings = new SparseArray<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(MAIN_SERVICE_NOTIFICATION, getNotification());
        ensureConnection();
        return START_NOT_STICKY;
    }

    public void addSubscription(SubscriptionType type) {
        final List<UpdateChunk> data = subscriptions.get(type);
        if (data != null) {
            EventBus.getDefault().post(new EventDataUpdate(type, data.get(data.size() - 1)));
            return;
        }
        ensureConnection();
        boolean exists = false;
        for (int i = 0; i < bindings.size(); i++) {
            if (bindings.valueAt(i) == type) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            connection.send(type.getSubscriptionMessage());
        }
        subscriptions.put(type, new ArrayList<>());
    }

    public void cancelSubscription(SubscriptionType type) {
        subscriptions.remove(type);
    }

    @NonNull
    public List<SubscriptionType> getActiveSubscriptions() {
        return new ArrayList<>(subscriptions.keySet());
    }

    @NonNull
    public List<UpdateChunk> getDataFor(SubscriptionType type) {
        final List<UpdateChunk> updateChunks = subscriptions.get(type);
        return updateChunks == null ? Collections.emptyList() : new ArrayList<>(updateChunks);
    }

    public void stop() {
        stopForeground(true);
        clear();
        stopSelf();
    }

    private void ensureConnection() {
        if (connection == null) {
            connection = new SocketConnection(this::onMessageReceived);
            connection.open();
        }
    }

    private void onMessageReceived(String message) {
        if (message.startsWith("[")) {
            try {
                JSONArray array = new JSONArray(message);
                SubscriptionType type = bindings.get(array.getInt(0));
                final List<UpdateChunk> data = subscriptions.get(type);
                if (data == null) {
                    return;
                }
                final UpdateChunk chunk;
                if ("hb".equals(array.get(1))) {
                    if (data.isEmpty()) {
                        return;
                    }
                    chunk = data.get(data.size() - 1).copyWithCurrentTime();
                } else {
                    chunk = UpdateChunk.from(array);
                }
                data.add(chunk);
                EventBus.getDefault().post(new EventDataUpdate(type, chunk));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            SubscriptionMessage msg = new Gson().fromJson(message, SubscriptionMessage.class);
            final Integer channelId = msg.getChannelId();
            if (channelId != null) {
                bindings.put(channelId, SubscriptionType.valueOf(msg.getType()));
            }
        }
    }

    private void clear() {
        if (connection != null) {
            connection.close();
        }
        bindings.clear();
        subscriptions.clear();
    }

    @NonNull
    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ForexApp.SERVICE_CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle("Forex app");
        builder.setContentText("Listening for changes");
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        return builder.build();
    }

    @Override
    public void onDestroy() {
        clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class Binder extends android.os.Binder {
        public MainService getService() {
            return MainService.this;
        }
    }
}
