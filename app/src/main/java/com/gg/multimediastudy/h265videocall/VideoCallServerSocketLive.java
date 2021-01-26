package com.gg.multimediastudy.h265videocall;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @description:
 * @author: XiaoChen
 * @date: 2021/01/17 23:54
 */

public class VideoCallServerSocketLive implements SocketLive {

    private static final String TAG = "GG";

    private WebSocket webSocket;
    private SocketLiveCallback socketCallback;

    public VideoCallServerSocketLive(SocketLiveCallback socketCallback) {
        this.socketCallback = socketCallback;
    }

    @Override
    public void start() {
        webSocketServer.start();
    }

    private WebSocketServer webSocketServer = new WebSocketServer(new InetSocketAddress(40002)) {
        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
            VideoCallServerSocketLive.this.webSocket = webSocket;
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
        public void onMessage(WebSocket conn, ByteBuffer bytes) {
            Log.i(TAG, "onMessage: 消息长度 : " + bytes.remaining());
            byte[] buf = new byte[bytes.remaining()];
            bytes.get(buf);
            socketCallback.callBack(buf);
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

    @Override
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
