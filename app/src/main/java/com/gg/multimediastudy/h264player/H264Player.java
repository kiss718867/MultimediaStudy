package com.gg.multimediastudy.h264player;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @description: 解码播放h264
 * @author: XiaoChen
 * @date: 2020/12/24 01:58
 */

public class H264Player implements Runnable {

    private Context context;
    private String path;
    private MediaCodec mediaCodec;
    private Surface surface;

    public H264Player(Context context, String path, Surface surface) {
        this.surface = surface;
        this.path = path;
        this.context = context;

        try {
            mediaCodec = MediaCodec.createDecoderByType("video/avc");
            MediaFormat format = MediaFormat.createVideoFormat("video/avc",853, 480);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            mediaCodec.configure(format, surface, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        mediaCodec.start();
        new Thread(this).start();
    }

    @Override
    public void run() {
        decodeH264();
    }

    private void decodeH264() {
        byte[] bytes = null;
        try {
            // 一次全加载了
            bytes = getBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bytes == null) {
            return;
        }
        // 内部队列，不是都可以用
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        int startIndex = 0;
        int totalSize = bytes.length;
        while (true) {
            if (totalSize == 0 || startIndex >= totalSize) {
                break;
            }
            int nextFrameStart = findByFrame(bytes, startIndex + 2, totalSize);
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int inIndex = mediaCodec.dequeueInputBuffer(10000);
            if (inIndex >= 0) {
                // 获取可以用的ByteBuffer
                ByteBuffer byteBuffer = inputBuffers[inIndex];
                byteBuffer.clear();
                byteBuffer.put(bytes, startIndex, nextFrameStart - startIndex);
                // 通知dsp芯片解码
                mediaCodec.queueInputBuffer(inIndex, 0, nextFrameStart - startIndex, 0, 0);
                startIndex = nextFrameStart;
            } else {
                continue;
            }

            // 得到数据
            int outIndex = mediaCodec.dequeueOutputBuffer(info, 10000);
            if (outIndex >= 0) {
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaCodec.releaseOutputBuffer(outIndex, true);
            }
        }
    }

    private int findByFrame(byte[] bytes, int start, int totalSize) {
        int j = 0;
        for (int i = start; i < totalSize - 4; i++) {
            if ((bytes[i] == 0x00 && bytes[i + 1] == 0x00 && bytes[i + 2] == 0x00 && bytes[i + 3] == 0x01)
                    || (bytes[i] == 0x00 && bytes[i + 1] == 0x00 && bytes[i + 2] == 0x01)) {
                return i;
            }
        }
        return -1;
    }

    public byte[] getBytes(String path) throws IOException {
        InputStream is = new DataInputStream(new FileInputStream(new File(path)));
        int len;
        int size = 1024;
        byte[] buf;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = is.read(buf, 0, size)) != -1) {
            bos.write(buf, 0, len);
        }
        buf = bos.toByteArray();
        return buf;
    }

}
