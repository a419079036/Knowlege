package com.example.administrator.knowlege;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.zxing.decoding.Intents;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ErWeiMaActivity extends AppCompatActivity implements SurfaceHolder.Callback
{
    private static final String TAG = ErWeiMaActivity.class.getSimpleName();
    @BindView(R.id.btn_begin)
    Button mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_er_wei_ma);
        ButterKnife.bind(this);
//        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.erweima_surfaceview);
//        if (surfaceView != null) {
//            surfaceView.getHolder().addCallback(this);
//        }
    }

    @OnClick(R.id.btn_begin)
    public void beginOnclick(View view)
    {
        Intent intent = new Intent(Intents.Scan.ACTION);
        startActivityForResult(intent, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000)
        {
            if (data != null)
            {
                String result = data.getStringExtra("result");
                Log.d(TAG, "onActivityResult: " + result);
                Toast.makeText(this, "result" + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {

    }
}
