package ua.pp.formatbce.forextest.websocket;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import ua.pp.formatbce.forextest.misc.IItemListener;

class ClientWebSocket {
    private static final String TAG = "Websocket";
    @NonNull
    private final IItemListener<String> listener;
    @NonNull
    private final String host;
    @Nullable
    private WebSocket ws;


    ClientWebSocket(@NonNull IItemListener<String> listener, @NonNull String host) {
        this.listener = listener;
        this.host = host;
    }

    void connect() {
        new Thread(() -> {
            if (ws != null) {
                reconnect(ws);
            } else {
                try {
                    WebSocketFactory factory = new WebSocketFactory();
                    SSLContext context = NaiveSSLContext.getInstance("TLS");
                    factory.setSSLContext(context);
                    ws = factory.createSocket(host);
                    ws.addListener(new SocketListener());
                    ws.connect();
                } catch (WebSocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void reconnect(@NonNull WebSocket template) {
        try {
            ws = template.recreate().connect();
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    WebSocket getSocket() {
        return ws;
    }

    void sendMessage(@NonNull String message) {
        if (ws != null) {
            Log.i(TAG, "Sending: " + message);
            ws.sendText(message);
        }
    }

    void close() {
        if (ws != null) {
            ws.disconnect();
            ws = null;
        }
    }

    boolean isConnected() {
        return ws != null && ws.isOpen();
    }


    public class SocketListener extends WebSocketAdapter {

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            Log.i(TAG, "onConnected");
        }

        public void onTextMessage(WebSocket websocket, String message) {
            listener.call(message);
            Log.i(TAG, "Message --> " + message);
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) {
            Log.i(TAG, "Error -->" + cause.getMessage());
            if (ws != null) {
                reconnect(ws);
            }
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                                   boolean closedByServer) {
            Log.i(TAG, "onDisconnected");
            if (closedByServer && ws != null) {
                reconnect(ws);
            }
        }

        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
            Log.i(TAG, "Error -->" + cause.getMessage());
            if (ws != null) {
                reconnect(ws);
            }
        }

        @Override
        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onPongFrame(websocket, frame);
            websocket.sendPing("Are you there?");
        }
    }
}
