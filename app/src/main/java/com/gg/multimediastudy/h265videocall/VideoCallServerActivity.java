package com.gg.multimediastudy.h265videocall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.gg.multimediastudy.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @description:
 * @author: XiaoChen
 * @date: 2021/01/21 23:52
 */

public class VideoCallServerActivity extends AppCompatActivity implements SocketLiveCallback {

    SurfaceView removeSurfaceView;
    LocalServiceView localSurfaceView;
    H265DecodePlayer h265DecodePlayer;
    Surface surface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_server);
        initView();
    }

    private void initView() {
        removeSurfaceView = findViewById(R.id.removeSurfaceView);
        localSurfaceView = findViewById(R.id.localSurfaceView);
        removeSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                surface = holder.getSurface();
                h265DecodePlayer = new H265DecodePlayer();
                h265DecodePlayer.initDecoder(surface);

            }
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            }
        });
    }

    public void connect(View view) {
        localSurfaceView.startCapture(LocalServiceView.TYPE_SERVER, this);
    }

    @Override
    public void callBack(byte[] data) {
        if (h265DecodePlayer != null) {
            h265DecodePlayer.callBack(data);
        }
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, VideoCallServerActivity.class);
        context.startActivity(intent);
    }

}
