package com.yuan.imageobtain.util;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.yuan.imageobtain.eventbus.BusProvider;
import com.yuan.imageobtain.eventbus.ImageEvent;

import java.io.File;

/**
 * Created by Yuan on 4/15/16.
 * <p/>
 * obtain activity
 */
public class ObtainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAPTURE_CAMERA = 0;
    // select from album request
    private static final int REQUEST_CODE_CAPTURE_ALBUM = 1;
    // clip request
    private static final int REQUEST_CODE_CAPTURE_CLIP = 2;
    // temp image path
    private String mImagePath;
    // image width
    private int mWidth;
    // image height
    private int mHeight;
    // camera or album
    private int mChannel;
    // corp
    private boolean bCorpEnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "sdcard 不可用，请检查！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Intent intent = getIntent();
        mWidth = intent.getIntExtra("width", mWidth);
        mHeight = intent.getIntExtra("height", mHeight);
        mChannel = intent.getIntExtra("channel", mChannel);
        bCorpEnable = intent.getBooleanExtra("enableCorp", true);
        mImagePath = intent.getStringExtra("path");
        mImagePath = TextUtils.isEmpty(mImagePath) ? Environment.getExternalStorageDirectory().getPath() : mImagePath;

        File file = new File(mImagePath);
        if (!file.exists()) {
            boolean mk = file.mkdir();
            if (!mk) {
                Toast.makeText(this, "sdcard 不可用，请检查！", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        handlePicture();
    }

    private void handlePicture() {

        if (mChannel == 1) {
            albumPicture();
        } else if (mChannel == 0) {
            takePicture();
        }
    }

    /**
     * camera
     */
    private void takePicture() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File pictureFile = new File(mImagePath, "obtain_temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMERA);
    }

    /**
     * album
     */
    private void albumPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_ALBUM);
    }

    /**
     * @param uri 图片URI
     */
    private void clipImage(Uri uri) {

        File pictureFile = new File(mImagePath, "obtain_corp.jpg");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("scale", "true");
        intent.putExtra("corp", "true");

        if(mWidth == mHeight) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        if(bCorpEnable) {
            intent.putExtra("width", mWidth);
            intent.putExtra("height", mHeight);
        }
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
        intent.putExtra("noFaceDetection", false);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CLIP);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_CAMERA: // 相机拍照完成回调
                if (resultCode == Activity.RESULT_OK) {
                    File file = new File(mImagePath, "obtain_temp.jpg");
                    if (bCorpEnable) {
                        clipImage(Uri.fromFile(file));
                    } else {
                        ImageEvent imageEvent = new ImageEvent(true, file.getAbsolutePath());
                        BusProvider.getInstance().post(imageEvent);
                        finish();
                    }
                } else {
                    finish();
                }
                break;
            case REQUEST_CODE_CAPTURE_ALBUM: // 图库选择完成回调
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    if (bCorpEnable) {
                        clipImage(uri);
                    } else {
                        String realPath = getRealPathFromURI(uri);
                        if (TextUtils.isEmpty(realPath)) {
                            finish();
                            ImageEvent imageEvent = new ImageEvent(false, null);
                            BusProvider.getInstance().post(imageEvent);
                        } else {
                            finish();
                            ImageEvent imageEvent = new ImageEvent(true, getRealPathFromURI(uri));
                            BusProvider.getInstance().post(imageEvent);
                        }
                    }
                } else {
                    finish();
                }
                break;
            case REQUEST_CODE_CAPTURE_CLIP: // 裁剪完成回调
                if (resultCode == Activity.RESULT_OK) {
                    File file = new File(mImagePath, "obtain_corp.jpg");
                    ImageEvent imageEvent = new ImageEvent(true, file.getAbsolutePath());
                    BusProvider.getInstance().post(imageEvent);
                } else {
                    ImageEvent imageEvent = new ImageEvent(false, null);
                    BusProvider.getInstance().post(imageEvent);
                }
                finish();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

}
