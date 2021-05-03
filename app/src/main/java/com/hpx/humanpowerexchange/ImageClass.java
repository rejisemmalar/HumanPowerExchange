package com.hpx.humanpowerexchange;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.android.volley.toolbox.ImageLoader;

public class ImageClass implements ImageLoader.ImageCache {
    @Nullable
    @Override
    public Bitmap getBitmap(String url) {
        return null;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {

    }
}
