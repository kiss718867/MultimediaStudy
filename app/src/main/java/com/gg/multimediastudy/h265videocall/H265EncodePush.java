package com.gg.multimediastudy.h265videocall;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.gg.multimediastudy.utils.YuvUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @description:
 * @author: XiaoChen
 * @date: 2021/01/22 00:07
 */

public class H265EncodePush {

    private MediaCodec mediaCodec;
    private int width;
    private int height;
    private SocketLive socketLive;
//    private VideoCallServerSocketLive socketLive;
//    private VideoCallClientSocketLive socketLive;
    // nv21转换成nv12的数据
    byte[] nv12;
    // 旋转之后的yuv数据
    byte[] yuv;
    public static final int NAL_I = 19;
    public static final int NAL_VPS = 32;
    private byte[] vps_sps_pps_buf;
    int frameIndex;

    public H265EncodePush(int type, SocketLiveCallback socketCallback, int width, int height) {
        this.width = width;
        this.height = height;
        if (type == LocalServiceView.TYPE_SERVER) {
            this.socketLive = new VideoCallServerSocketLive(socketCallback);
        } else {
            this.socketLive = new VideoCallClientSocketLive(socketCallback);
        }
        // 建立链接
        socketLive.start();
    }

    public void startLive() {
        // 实例化编码器
        // 创建对应编码器
        try {
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, height, width);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 2500000);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 10);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5); // IDR帧刷新时间
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
            int bufferLength = width * height * 3 / 2;
            nv12 = new byte[bufferLength];
            yuv = new byte[bufferLength];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //摄像头调用
    public int encodeFrame(byte[] input) {
        // nv21-nv12
        nv12 = YuvUtils.nv21toNV12(input);
        // 旋转
        YuvUtils.portraitData2Raw(nv12, yuv, width, height);

        int inputBufferIndex = mediaCodec.dequeueInputBuffer(100000);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
            inputBuffer.clear();
            inputBuffer.put(yuv);
            long presentationTimeUs = computePresentationTime(frameIndex);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuv.length, presentationTimeUs, 0);
            frameIndex++;
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100000);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
            dealFrame(outputBuffer, bufferInfo);
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
        return 0;
    }

    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / 15;
    }

    private void dealFrame(ByteBuffer bb, MediaCodec.BufferInfo bufferInfo) {
        int offset = 4;
        if (bb.get(2) == 0x01) {
            offset = 3;
        }
        int type = (bb.get(offset) & 0x7E) >> 1;
        if (type == NAL_VPS) {
            vps_sps_pps_buf = new byte[bufferInfo.size];
            bb.get(vps_sps_pps_buf);
        } else if (type == NAL_I) {
            final byte[] bytes = new byte[bufferInfo.size];
            bb.get(bytes);
            byte[] newBuf = new byte[vps_sps_pps_buf.length + bytes.length];
            System.arraycopy(vps_sps_pps_buf, 0, newBuf, 0, vps_sps_pps_buf.length);
            System.arraycopy(bytes, 0, newBuf, vps_sps_pps_buf.length, bytes.length);
            this.socketLive.sendData(newBuf);
        } else {
            final byte[] bytes = new byte[bufferInfo.size];
            bb.get(bytes);
            this.socketLive.sendData(bytes);
        }
    }

}
