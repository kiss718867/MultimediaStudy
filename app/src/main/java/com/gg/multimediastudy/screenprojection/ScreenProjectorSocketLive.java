package com.gg.multimediastudy.screenprojection;

import android.media.projection.MediaProjection;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * @description:
 * @author: XiaoChen
 * @date: 2021/01/17 23:54
 */

public class ScreenProjectorSocketLive {

    private static final String TAG = "GG";

    private WebSocket webSocket;
    private int port;
    private H265Codec h265Codec;

    public ScreenProjectorSocketLive(int port) {
        this.port = port;
    }

    public void start(MediaProjection mediaProjection) {
        webSocketServer.start();
        h265Codec = new H265Codec(this, mediaProjection);
        h265Codec.statLive();
    }

    private WebSocketServer webSocketServer = new WebSocketServer(new InetSocketAddress(port)) {
        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
            ScreenProjectorSocketLive.this.webSocket = webSocket;
            Log.i(TAG, "onOpen");
        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b) {
            Log.i(TAG, "onClose: 关闭 socket ");
        }

        @Override
        public void onMessage(WebSocket webSocket, String s) {
            Log.i(TAG, "onMessage: " + s);
        }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            Log.i(TAG, "onError:  " + e.toString());
        }

        @Override
        public void onStart() {
            Log.i(TAG, "onStart");
        }
    };

    public void sendData(byte[] bytes) {
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.send(bytes);
        }
    }

    public void close() {
        try {
            if (webSocket != null) {
                webSocket.close();
            }
            if (webSocketServer != null) {
                webSocketServer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
