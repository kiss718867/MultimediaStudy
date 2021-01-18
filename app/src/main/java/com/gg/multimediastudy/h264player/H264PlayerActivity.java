package com.gg.multimediastudy.h264player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gg.multimediastudy.R;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @description: 解码h264
 * @author: XiaoChen
 * @date: 2020/12/24 01:51
 */

public class H264PlayerActivity extends AppCompatActivity {

    private SurfaceView surface_view;
    private H264Player h264Player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h264player);
        surface_view = findViewById(R.id.surface_view);
        initSurface();
    }

    private void initSurface() {
        surface_view.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                h264Player = new H264Player(H264PlayerActivity.this,
                        new File(Environment.getExternalStorageDirectory(), "fitness.264").getAbsolutePath(),
                        surfaceHolder.getSurface());
                h264Player.play();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, H264PlayerActivity.class);
        context.startActivity(intent);
    }

}
