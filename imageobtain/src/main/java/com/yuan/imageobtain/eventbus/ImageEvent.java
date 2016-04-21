package com.yuan.imageobtain.eventbus;

/**
 * Created by Yuan on 4/15/16.
 * <p/>
 * image bus event
 */
public class ImageEvent {
    public boolean bSuccess;
    public String mPath;

    public ImageEvent(boolean success, String path) {
        this.bSuccess = success;
        this.mPath = path;
    }
    public boolean isSuccess() {
        return bSuccess;
    }

    public String getPath() {
        return mPath;
    }

}