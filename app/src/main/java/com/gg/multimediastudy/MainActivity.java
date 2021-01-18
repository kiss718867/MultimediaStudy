package com.gg.multimediastudy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gg.multimediastudy.camera1capture.Camera1CaptureActivity;
import com.gg.multimediastudy.h264encoder.H264EncoderActivity;
import com.gg.multimediastudy.h264parser.H264ParseActivity;
import com.gg.multimediastudy.h264player.H264PlayerActivity;
import com.gg.multimediastudy.screenprojection.ScreenProjectionPlayerActivity;
import com.gg.multimediastudy.screenprojection.ScreenProjectorActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_h264player;
    private TextView tv_h264encoder;
    private TextView tv_h264parser;
    private TextView tv_camera1_capture;
    private TextView tv_screen_projector;
    private TextView tv_screen_projection_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_h264player = findViewById(R.id.tv_h264player);
        tv_h264encoder = findViewById(R.id.tv_h264encoder);
        tv_h264parser = findViewById(R.id.tv_h264parser);
        tv_camera1_capture = findViewById(R.id.tv_camera1_capture);
        tv_screen_projector = findViewById(R.id.tv_screen_projector);
        tv_screen_projection_player = findViewById(R.id.tv_screen_projection_player);
        tv_h264player.setOnClickListener(this);
        tv_h264encoder.setOnClickListener(this);
        tv_h264parser.setOnClickListener(this);
        tv_camera1_capture.setOnClickListener(this);
        tv_screen_projector.setOnClickListener(this);
        tv_screen_projection_player.setOnClickListener(this);

        checkPermission();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_h264player) {
            H264PlayerActivity.startActivity(this);
        } else if (id == R.id.tv_h264encoder) {
            H264EncoderActivity.startActivity(this);
        } else if (id == R.id.tv_h264parser) {
            H264ParseActivity.startActivity(this);
        } else if (id == R.id.tv_camera1_capture) {
            Camera1CaptureActivity.startActivity(this);
        } else if (id == R.id.tv_screen_projector) {
            ScreenProjectorActivity.startActivity(this);
        } else if (id == R.id.tv_screen_projection_player) {
            ScreenProjectionPlayerActivity.startActivity(this);
        }
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, 1);

        }
        return false;
    }


}