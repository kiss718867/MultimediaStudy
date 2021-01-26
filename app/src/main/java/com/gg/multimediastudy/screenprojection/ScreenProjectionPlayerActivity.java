package com.gg.multimediastudy.screenprojection;

import android.content.Context;
import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gg.multimediastudy.R;

import java.io.IOException;
import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @description: 投屏播放器
 * @author: XiaoChen
 * @date: 2021/01/17 23:25
 */

public class ScreenProjectionPlayerActivity extends AppCompatActivity implements ScreenProjectionPlayerSocketLive.SocketCallback {

    private static final String TAG = "GG";

    private int width = 1080;
    private int height = 1920;

    private Surface surface;
    MediaCodec mediaCodec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_projection_player);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                surface = holder.getSurface();
                initSocket();
                initDecoder();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
    }

    private void initDecoder() {
        try {
            mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);
            final MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, width, height);
            format.setInteger(MediaFormat.KEY_BIT_RATE, width * height);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mediaCodec.configure(format, surface, null, 0);
            mediaCodec.start();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    private void initSocket() {
        ScreenProjectionPlayerSocketLive screenLive = new ScreenProjectionPlayerSocketLive(this, 12001);
        screenLive.start();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ScreenProjectionPlayerActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void callBack(byte[] data) {
        Log.i(TAG, "接收到消息: " + data.length);

        int index = mediaCodec.dequeueInputBuffer(100000);
        if (index >= 0) {
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
            inputBuffer.clear();
            inputBuffer.put(data, 0, data.length);
            mediaCodec.queueInputBuffer(index, 0, data.length, System.currentTimeMillis(), 0);
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100000);

        if (outputBufferIndex > 0) {
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
        }
    }

}
