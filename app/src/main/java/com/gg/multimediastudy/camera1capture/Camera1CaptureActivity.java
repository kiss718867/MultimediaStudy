package com.gg.multimediastudy.camera1capture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gg.multimediastudy.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @description: Camera1拍照，保存图片
 * @author: XiaoChen
 * @date: 2021/01/14 00:31
 */

public class Camera1CaptureActivity extends AppCompatActivity {

    private Camera1SurfaceView surface_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1_capture);
        surface_view = findViewById(R.id.surface_view);
        findViewById(R.id.btn).setOnClickListener(v -> surface_view.startCapture());
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, Camera1CaptureActivity.class);
        context.startActivity(intent);
    }

}
