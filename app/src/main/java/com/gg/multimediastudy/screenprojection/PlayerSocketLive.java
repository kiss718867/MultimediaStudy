package com.gg.multimediastudy.screenprojection;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @description:
 * @author: XiaoChen
 * @date: 2021/01/18 00:32
 */

public class PlayerSocketLive {

    private static final String TAG = "GG";

    private SocketCallback socketCallback;
    private int port;
    private MyWebSocketClient myWebSocketClient;

    public PlayerSocketLive(SocketCallback callback, int port) {
        this.socketCallback = socketCallback;
        this.port = port;
    }

    public void start() {
        try {
            URI url = new URI("ws://192.168.0.176:" + port);
            myWebSocketClient = new MyWebSocketClient(url);
            myWebSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            Log.i(TAG, "打开 socket  onOpen: ");
        }

        @Override
        public void onMessage(String s) {
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            Log.i(TAG, "消息长度  : " + bytes.remaining());
            byte[] buf = new byte[bytes.remaining()];
            bytes.get(buf);
            socketCallback.callBack(buf);
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            Log.i(TAG, "onClose: ");
        }

        @Override
        public void onError(Exception e) {
            Log.i(TAG, "onError: " + e.getMessage());
        }
    }

    public interface SocketCallback{
        void callBack(byte[] data);
    }

}
