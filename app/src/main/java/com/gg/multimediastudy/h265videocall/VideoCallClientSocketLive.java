package com.gg.multimediastudy.h265videocall;

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

public class VideoCallClientSocketLive implements SocketLive {

    private static final String TAG = "GG";

    private SocketLiveCallback socketCallback;
    private MyWebSocketClient myWebSocketClient;

    public VideoCallClientSocketLive(SocketLiveCallback callback) {
        this.socketCallback = socketCallback;
    }

    @Override
    public void start() {
        try {
            URI url = new URI("ws://192.168.0.176:40002");
//            URI url = new URI("ws://echo.websocket.org");
            myWebSocketClient = new MyWebSocketClient(url);
            myWebSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendData(byte[] bytes) {
        if (myWebSocketClient != null && (myWebSocketClient.isOpen())) {
            myWebSocketClient.send(bytes);
        }
    }

    private class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            Log.i(TAG, "打开 socket  onOpen");
        }

        @Override
        public void onMessage(String s) {
            Log.i(TAG, "onMessage: " + s);
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
            Log.i(TAG, "onClose");
        }

        @Override
        public void onError(Exception e) {
            Log.i(TAG, "onError: " + e.getMessage());
        }
    }

}
