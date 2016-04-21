package com.yuan.imageobtain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.yuan.imageobtain.util.ImageObtainInstance;
import com.yuan.imageobtaindemo.*;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.yuan.imageobtaindemo.R.layout.activity_main);

        mImageView = (ImageView) findViewById(com.yuan.imageobtaindemo.R.id.image_obtain);
    }


    public void obtainFromCamera(View view) {
        new ImageObtainInstance(this)
                .setCorpEnable(true)
                .setCorpHeight(200)
                .setCorpWidth(300)
                .setPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "obtain_test")
                .setChannel(ImageObtainInstance.ImageChannel.CHANNEL_CAMERA)
                .setObtainListener(new ImageObtainInstance.OnPictureObtainListener() {
                    @Override
                    public void obtainSuccess(String path) {
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        mImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void obtainFailure() {

                    }
                }).obtain();
    }

    public void obtainFromAlbum(View view) {
        new ImageObtainInstance(this)
                .setCorpEnable(true)
                .setCorpHeight(200)
                .setCorpWidth(200)
                .setPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "obtain_test")
                .setChannel(ImageObtainInstance.ImageChannel.CHANNEL_ALBUM)
                .setObtainListener(new ImageObtainInstance.OnPictureObtainListener() {
                    @Override
                    public void obtainSuccess(String path) {
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        mImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void obtainFailure() {

                    }
                }).obtain();
    }
}
