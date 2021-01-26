package com.gg.multimediastudy.screenprojection;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import com.gg.multimediastudy.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @description: 投屏推流端
 * @author: XiaoChen
 * @date: 2021/01/17 23:25
 */

public class ScreenProjectorActivity extends AppCompatActivity {

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private ScreenProjectorSocketLive socketLive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_projector);
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            socketLive = new ScreenProjectorSocketLive(6777);
            socketLive.start(mediaProjection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketLive.close();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ScreenProjectorActivity.class);
        context.startActivity(intent);
    }

}
