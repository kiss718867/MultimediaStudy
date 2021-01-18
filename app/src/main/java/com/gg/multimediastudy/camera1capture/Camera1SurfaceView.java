package com.gg.multimediastudy.camera1capture;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.gg.multimediastudy.utils.YuvUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Camera1SurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Camera mCamera;
    private Camera.Size size;
    private byte[] buffer;
    private byte[] yuv;
    private volatile boolean isCapture;

    public Camera1SurfaceView(Context context) {
        this(context, null);
    }

    public Camera1SurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Camera1SurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
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

    private void startPreview() {
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            Camera.Parameters parameters = mCamera.getParameters();

            size = parameters.getPreviewSize();

            mCamera.setPreviewDisplay(getHolder());
            mCamera.setDisplayOrientation(90);

            // 8个y对应2个u2个v，1y + 1/4u + 1/4v = 3/2
            buffer = new byte[size.width * size.height * 3 / 2];
            yuv = new byte[size.width * size.height * 3 / 2];
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);

            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startCapture() {
        isCapture = true;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (isCapture) {
            YuvUtils.portraitData2Raw(data, yuv, size.width, size.height);
            isCapture = false;
            capture(yuv);
        }
        mCamera.addCallbackBuffer(data);
    }

    private void capture(byte[] buffer) {
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";  //jpeg文件名定义
        File sdRoot = Environment.getExternalStorageDirectory();    //系统路径
        File pictureFile = new File(sdRoot, fileName);

        try {
            if (!pictureFile.exists()) {
                pictureFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(pictureFile);
            YuvImage image = new YuvImage(buffer, ImageFormat.NV21, size.height, size.width, null);
            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, fos);
            Toast.makeText(getContext(), pictureFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
