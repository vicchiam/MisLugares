package com.example.mislugares;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by vicch on 08/02/2018.
 */

public class VolleySingleton {

    private static VolleySingleton INSTANCE = null;

    private static RequestQueue colaPeticiones;
    private static ImageLoader lectorImagenes;

    private VolleySingleton(Context context){
        colaPeticiones = Volley.newRequestQueue(context);

        lectorImagenes = new ImageLoader(colaPeticiones, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

        });
    };

    private synchronized static void createInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new VolleySingleton(context);
        }
    }

    public static VolleySingleton getInstance(Context context){
        if(INSTANCE == null) createInstance(context);
        return INSTANCE;
    }

    public static RequestQueue getColaPeticiones(){
        return colaPeticiones;
    }

    public static ImageLoader getLectorImagenes(Context context){
        if(INSTANCE == null) createInstance(context);
        return lectorImagenes;
    }

}
