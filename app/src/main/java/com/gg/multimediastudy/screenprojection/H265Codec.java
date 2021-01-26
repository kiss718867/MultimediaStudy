package com.gg.multimediastudy.screenprojection;

import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.media.MediaFormat.KEY_BIT_RATE;
import static android.media.MediaFormat.KEY_FRAME_RATE;
import static android.media.MediaFormat.KEY_I_FRAME_INTERVAL;

/**
 * @description:
 * @author: XiaoChen
 * @date: 2021/01/17 23:57
 */

public class H265Codec extends Thread {

    private int width = 1080;
    private int height = 1920;

    private ScreenProjectorSocketLive socketLive;
    private MediaProjection mediaProjection;
    private MediaCodec mediaCodec;

    public H265Codec(ScreenProjectorSocketLive socketLive, MediaProjection mediaProjection) {
        this.socketLive = socketLive;
        this.mediaProjection = mediaProjection;
    }

    public void statLive() {
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(KEY_BIT_RATE, width * height);
        format.setInteger(KEY_FRAME_RATE, 15);
        format.setInteger(KEY_I_FRAME_INTERVAL, 1);
        try {
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface surface = mediaCodec.createInputSurface();
            mediaProjection.createVirtualDisplay("live", width, height, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        super.run();
        mediaCodec.start();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (true) {
            int outputBufferId = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
            if (outputBufferId >= 0) {
                ByteBuffer byteBuffer = mediaCodec.getOutputBuffer(outputBufferId);
                dealFrame(byteBuffer, bufferInfo);
                mediaCodec.releaseOutputBuffer(outputBufferId, false);
            }
        }
    }

    public static final int NAL_I = 19;
    public static final int NAL_VPS = 32;
    private byte[] vps_sps_pps_buf;

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
