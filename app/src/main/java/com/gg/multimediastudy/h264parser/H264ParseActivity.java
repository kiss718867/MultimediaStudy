package com.gg.multimediastudy.h264parser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.gg.multimediastudy.R;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @description: 解析h264
 * @author: XiaoChen
 * @date: 2021/01/12 23:26
 */

public class H264ParseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h264parser);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    public void start() {
        H264Parser mediaCodec = new H264Parser(new File(Environment.getExternalStorageDirectory(),
                "out2.h264").getAbsolutePath());
        mediaCodec.startParser();
    }


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, H264ParseActivity.class);
        context.startActivity(intent);
    }

}
