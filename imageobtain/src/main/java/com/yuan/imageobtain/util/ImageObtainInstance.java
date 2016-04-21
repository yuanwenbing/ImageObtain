package com.yuan.imageobtain.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;
import com.yuan.imageobtain.eventbus.BusProvider;
import com.yuan.imageobtain.eventbus.ImageEvent;

/**
 * Created by Yuan on 4/15/16.
 * <p/>
 * Obtain instance
 */
public class ImageObtainInstance {

    private Context mContext;

    private OnPictureObtainListener mListener;
    // image width
    private int mWidth;

    // image Height
    private int mHeight;

    // camera or album
    private ImageChannel mChannel;

    // enable corp
    private boolean bEnableCorp;

    // 图片存储的地方
    private String mPath;

    public ImageObtainInstance(Context context) {
        this.mContext = context;
        BusProvider.getInstance().register(this);
    }

    /**
     * @param height 设置裁剪的高度 一旦setCorpEnable()为false 此值将不起作用
     * @return this
     */
    public ImageObtainInstance setCorpHeight(int height) {
        mHeight = height;
        return this;
    }

    /**
     * @param width 设置裁剪的宽度 一旦setCorpEnable()为false 此值将不起作用
     * @return this
     */
    public ImageObtainInstance setCorpWidth(int width) {
        mWidth = width;
        return this;
    }

    /**
     * @param path 设置临时存储path
     * @return this
     */
    public ImageObtainInstance setPath(String path) {
        mPath = path;
        return this;
    }

    /**
     * @param channel 设置图片来源
     * @return this
     */
    public ImageObtainInstance setChannel(ImageChannel channel) {
        mChannel = channel;
        return this;
    }

    /**
     * @param enable 是否进行裁剪
     * @return this
     */
    public ImageObtainInstance setCorpEnable(boolean enable) {
        bEnableCorp = enable;
        return this;
    }

    /**
     * @param listener 设置回调
     * @return this
     */
    public ImageObtainInstance setObtainListener(OnPictureObtainListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 获取图片
     */
    public void obtain() {

        if (mListener == null) {
            throw new NullPointerException("You must setPermissionListener() on ObtainPicture");
        }
        Intent intent = new Intent(mContext, ObtainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int channel = mChannel == ImageChannel.CHANNEL_CAMERA ? 0 : 1;
        intent.putExtra("channel", channel);
        intent.putExtra("width", mWidth);
        intent.putExtra("height", mHeight);
        intent.putExtra("enableCorp", bEnableCorp);
        intent.putExtra("path", mPath);
        mContext.startActivity(intent);
    }

    @Subscribe
    public void ObtainPictureResult(ImageEvent event) {
        if (event.isSuccess()) {
            String imagePath = event.getPath();
            if (TextUtils.isEmpty(imagePath)) {
                mListener.obtainFailure();
            }
            mListener.obtainSuccess(event.getPath());
        } else {
            mListener.obtainFailure();
        }

        BusProvider.getInstance().unregister(this);

    }

    public enum ImageChannel {
        CHANNEL_CAMERA, CHANNEL_ALBUM
    }

    public interface OnPictureObtainListener {
        void obtainSuccess(String path);

        void obtainFailure();
    }


}
