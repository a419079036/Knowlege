package com.example.administrator.knowlege;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback, Camera.PictureCallback
{

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private int mMCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preView);
        if (mSurfaceView != null)
        {
            mSurfaceView.getHolder().addCallback(this);
        }
    }

    //按钮点击事件
    @OnClick(R.id.btn_take)
    public void btnTakeOnClick(View view)
    {
        if (mCamera != null)
        {
//            拍照完成调用huidiaojiek
//            第一个参数1：代表快门接口，
//            参数二：如果拍照格式是RAW那么调用这个接口‘
//            参数三：如果拍照的照片格式是JPEG调用这z个
            mCamera.takePicture(null, null, this);
        }
    }

    private void initCamera()
    {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++)
        {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                mCamera = Camera.open(i);
                mMCameraId = i;
                break;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (mCamera == null)
        {
            initCamera();
//            mCamera = Camera.open();//只会找后置摄像头
            if (mCamera != null)
            {
                try
                {
//                    照相机的设置
//                    1.预览设置
//                    mCamera.setDisplayOrientation(90);
//                    根据手机屏幕方向来设置预览显示方向
//                    1.2设置预览回调接口，通常视频录制，二维码扫描都是用它
                    mCamera.setPreviewCallbackWithBuffer(this);
                    setCameraDisplayOrientation(this, mMCameraId, mCamera);
//                    2.拍照设置，使用Parameters来设置
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
//                   设置闪光灯模式，需要权限
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    parameters.setPictureFormat(ImageFormat.JPEG);
                    parameters.setJpegQuality(100);
                    mCamera.setParameters(parameters);
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
//        Canvas canvas = holder.lockCanvas();
//        canvas.drawColor(Color.GREEN);
//        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera)
    {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else
        {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    //预览的图片格式，通常是YUV格式 YCbCr =>RGB
    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        // TODO: 2016/10/18 二维码扫描，直播
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size previewSize = parameters.getPreviewSize();
        int previewFormat = parameters.getPreviewFormat();
        YuvImage image=new YuvImage(data,previewFormat,previewSize.width,previewSize.height,null);

        Rect rect = new Rect();
        rect.left=0;
        rect.top=0;
        rect.right=rect.left+previewSize.width;
        rect.bottom=rect.top+previewSize.height;
       // image.compressToJpeg(rect,100);

    }

    /**
     * 接收拍照之后，实际的图像数据
     * 如果还需要继续预览，拍照的话，内部必须
     * 再一次调用Cameera的StartPreview
     *
     * @param data   图像数据，对于PictureFormat为JPEG的图像，就是一个JPEG文件
     * @param camera
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        String state = Environment.getExternalStorageState();
        File dir = getFilesDir();
        if (state.equals(Environment.MEDIA_MOUNTED))
        {
            dir = Environment.getExternalStorageDirectory();
        }
        if (!dir.exists())
        {
            dir.mkdir();
        }
        File target = new File(dir, "img-" + System.currentTimeMillis() + ".jpg");

        if (!target.exists())
        {
            try
            {
                target.createNewFile();
                FileOutputStream stream = new FileOutputStream(target);
                stream.write(data);
                stream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            //1.耗内存
/*            Bitmap bitmap= BitmapFactory.decodeFile(target.getAbsolutePath());
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] colors=new int[width*height];
            bitmap.getPixels(colors,0,width,0,0,width,height);
            bitmap.compress(Bitmap.CompressFormat.JPEG,);*/
            camera.startPreview();
        }

    }
}
