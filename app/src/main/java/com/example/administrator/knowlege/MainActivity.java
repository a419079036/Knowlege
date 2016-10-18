package com.example.administrator.knowlege;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_take_photo_1)
    Button mButton;
    @BindView(R.id.image_view)
    ImageView mImageView;
    private File mTarget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.btn_take_photo_1)
    public void btnTake10Click(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,998);

    }
    @OnClick(R.id.btn_take_photo2)
    public void btnFullyClick(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        使用EXTRA_OUTPUT指定URi位置，可以把大尺寸原图保存到uri指定的位置
        String state = Environment.getExternalStorageState();
        File dir = getFilesDir();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStorageDirectory();
        }
        if (!dir.exists()) {
            dir.mkdir();
        }
        mTarget = new File(dir,"img-"+System.currentTimeMillis()+".jpg");
        Uri uri = Uri.fromFile(mTarget);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);

        startActivityForResult(intent,999);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 998) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!=null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    if (bitmap != null) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }
            }
        }else  if (requestCode == 999){
            if (resultCode== Activity.RESULT_OK) {
                Toast.makeText(this,"拍照成功",Toast.LENGTH_SHORT).show();

                if (mTarget.exists()) {
//                    加载图片显示（图片压缩）
//                    案例1：使用Options参数直接设置inSampleSize

//                    BitmapFactory.Options options =
//                            new BitmapFactory.Options();
////                    inSampleSize
//                    options.inSampleSize  = 2;
//                    options.inPreferredConfig = Bitmap.Config.RGB_565;
//                    options.inPurgeable = true;
//                    Bitmap bitmap =
//                            BitmapFactory.decodeFile(mTarget.getAbsolutePath(),options);


                    Bitmap bitmap = DecodeSampleBitmapUtil.loadBitmapWithScale(mTarget, 0, 0);
                    mImageView.setImageBitmap(bitmap);
                }
            }
        }

    }
}
