package ua.pp.formatbce.forextest.websocket;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import ua.pp.formatbce.forextest.misc.IItemListener;

public class SocketConnection {

    private static final String WS_SERVER = "wss://api.bitfinex.com/ws";
    @NonNull
    private final IItemListener<String> messageListener;

    @Nullable
    private ClientWebSocket clientWebSocket;
    @NonNull
    private final Handler socketConnectionHandler = new Handler();

    private Runnable reopenConnectionRunnable = () -> {
        if (clientWebSocket == null || !clientWebSocket.isConnected()) {
            open();
        }
        startCheckConnection();
    };

    public SocketConnection(@NonNull IItemListener<String> messageListener) {
        this.messageListener = messageListener;
    }

    private void startCheckConnection() {
        socketConnectionHandler.postDelayed(reopenConnectionRunnable, 5000);
    }

    private void stopCheckConnection() {
        socketConnectionHandler.removeCallbacks(reopenConnectionRunnable);
    }

    public void open() {
        if (clientWebSocket != null) {
            clientWebSocket.close();
        }
        try {
            clientWebSocket = new ClientWebSocket(messageListener, WS_SERVER);
            clientWebSocket.connect();
            Log.i("Websocket", "Socket connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
        startCheckConnection();
    }

    public void send(@NonNull String message) {
        if (clientWebSocket != null && clientWebSocket.isConnected()) {
            clientWebSocket.sendMessage(message);
        }
    }

    public void close() {
        if (clientWebSocket != null) {
            clientWebSocket.close();
            clientWebSocket = null;
        }
        stopCheckConnection();
    }
}
