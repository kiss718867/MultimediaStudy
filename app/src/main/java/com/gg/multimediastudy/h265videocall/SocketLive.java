package com.gg.multimediastudy.h265videocall;

/**
 * @description:
 * @author: XiaoChen
 * @date: 2021/01/22 01:06
 */

public interface SocketLive {

    void start();
    void sendData(byte[] bytes);

}
