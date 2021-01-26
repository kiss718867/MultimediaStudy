package com.gg.multimediastudy.h265videocall;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * @description:
 * @author: XiaoChen
 * @date: 2021/01/21 23:59
 */

public class LocalServiceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    public static final int TYPE_SERVER = 1;
    public static final int TYPE_CLIENT = 2;

    private Camera.Size size;
    private Camera mCamera;
    private byte[] buffer;
    H265EncodePush h265EncodePush;

    public LocalServiceView(Context context) {
        this(context, null);
    }

    public LocalServiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalServiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    public void startPreview() {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Camera.Parameters parameters = mCamera.getParameters();
        size = parameters.getPreviewSize();
        try {
            mCamera.setPreviewDisplay(getHolder());
            mCamera.setDisplayOrientation(90);
            buffer = new byte[size.width * size.height * 3/2];
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // 获取到摄像头的原始数据yuv
        // 开始视频通话
        if (h265EncodePush != null) {
            h265EncodePush.encodeFrame(data);
        }
        mCamera.addCallbackBuffer(data);
    }

    public void startCapture(int type, SocketLiveCallback socketCallback) {
        h265EncodePush = new H265EncodePush(type, socketCallback, size.width, size.height);
        h265EncodePush.startLive();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
