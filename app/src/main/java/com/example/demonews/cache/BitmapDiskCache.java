package com.example.demonews.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.isExternalStorageRemovable;

/**
 * QuangNhe: Lớp cache bitmap trên bộ nhớ ngoài thay vì bộ nhớ trong của app
 *           Custom lại vì vốn chỉ chạy trên background thread
 * Sources: https://github.com/GabrielBB/Android-Bitmap-DiskCache
 */
public class BitmapDiskCache {

    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 20; // 20MB
    private static final String DISK_CACHE_SUBDIR = "BITMAP_CACHE";

    public BitmapDiskCache(Context context) {
        File cacheDir = getDiskCacheDir(context);
        new InitDiskCacheTask().execute(cacheDir);
    }

    private class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDiskCacheStarting = false; // Finished initialization
        }
    }

    public Bitmap getInBackgroundThread(String key) {
        final String imageKey = String.valueOf(key);
        DiskLruCache.Snapshot foundSnapshot = null;

        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException ignored) {
                }
            }

            if (mDiskLruCache != null) {
                try {
                    foundSnapshot = mDiskLruCache.get(imageKey);
                } catch (Exception e) {}
            }
        }
        if (foundSnapshot != null) try(DiskLruCache.Snapshot snapshot = foundSnapshot) {
            return BitmapFactory.decodeStream(snapshot.getInputStream(0));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addInBackgroundThread(Bitmap bitmap, String key) {
        synchronized (mDiskCacheLock) {
            DiskLruCache.Editor editor = null;

            try {
                if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                    editor = mDiskLruCache.edit(key);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 95, editor.newOutputStream(0));
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();

                if (editor != null) {
                    try {
                        editor.abort();
                    } catch (IOException ignored) {

                    }
                }
            }
        }
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    private static File getDiskCacheDir(Context context) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + DISK_CACHE_SUBDIR);
    }

    public void close() {
        synchronized (mDiskCacheLock) {
            try {
                if (!mDiskLruCache.isClosed()) {
                    mDiskLruCache.close();
                }

                mDiskLruCache.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
