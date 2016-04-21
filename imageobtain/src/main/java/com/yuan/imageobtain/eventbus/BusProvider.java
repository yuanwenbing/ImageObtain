package com.yuan.imageobtain.eventbus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Maintains a singleton instance for obtaining the bus. Ideally this would be
 * replaced with a more efficient means such as through injection directly into
 * interested classes.
 */
public final class BusProvider extends Bus {

    private static BusProvider instance;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public static BusProvider getInstance() {

        if (instance == null)
            instance = new BusProvider();

        return instance;
    }

    @Override
    public void post(final Object event) {


        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    BusProvider.getInstance().post(event);
                }
            });
        }
    }
}
