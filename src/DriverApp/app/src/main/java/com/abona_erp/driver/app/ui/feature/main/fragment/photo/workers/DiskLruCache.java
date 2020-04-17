package com.abona_erp.driver.app.ui.feature.main.fragment.photo.workers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StatFs;

import com.abona_erp.driver.app.logging.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiskLruCache {
  private static final String TAG = "DiskLruCache";
  private static final String CACHE_FILENAME_PREFIX = "cache_";
  private static final int MAX_REMOVALS = 4;
  private static final int INITIAL_CAPACITY = 32;
  private static final float LOAD_FACTOR = 0.75f;
  
  private final File mCacheDir;
  private static final int MAX_CACHE_SIZE_ITEM = 1000; // 64 item default
  
  private int cacheSize = 0;
  private int cacheByteSize = 0;
  private long maxCacheByteSize = 1024 * 1024 * 20; // 20MB default
  private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
  private int mCompressQuality = 70;
  
  private final Map<String, String> mLinkedHashMap =
    Collections.synchronizedMap(new LinkedHashMap<String, String>(
      INITIAL_CAPACITY, LOAD_FACTOR, true));
  
  /**
   * A filename filter to use to identify the cache filenames which have CACHE_FILENAME_PREFIX
   * prepended.
   */
  private static final FilenameFilter cacheFileFilter = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String filename) {
      return filename.startsWith(CACHE_FILENAME_PREFIX);
    }
  };
  
  public static DiskLruCache openCache(File cacheDir, long maxByteSize) {
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    
    if (cacheDir.isDirectory() && cacheDir.canWrite()
      && getUsableSpace(cacheDir) > maxByteSize) {
      return new DiskLruCache(cacheDir, maxByteSize);
    }
    
    return null;
  }
  
  /**
   * Constructor that should not be called directly, instead use
   * {@link DiskLruCache#openCache(File, long)} which runs some extra checks before
   * creating a DiskLruCache instance.
   *
   * @param cacheDir
   * @param maxByteSize
   */
  private DiskLruCache(File cacheDir, long maxByteSize) {
    mCacheDir = cacheDir;
    maxCacheByteSize = maxByteSize;
  }
  
  
  @SuppressWarnings("deprecation")
  @SuppressLint("NewApi")
  public static long getUsableSpace(File path) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      return path.getUsableSpace();
    }
    final StatFs stats = new StatFs(path.getPath());
    return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
  }
  
  /**
   * Add a bitmap to the disk cache.
   *
   * @param key  A unique identifier for the bitmap.
   * @param data The bitmap to store.
   */
  public void put(String key, Bitmap data) {
    
    if ((data == null) || (key == null))
      return;
    synchronized (mLinkedHashMap) {
      if (mLinkedHashMap.get(key) == null) {
        try {
          final String file = createFilePath(mCacheDir, key);
          if (writeBitmapToFile(data, file)) {
            put(key, file);
            flushCache();
          }
        } catch (final FileNotFoundException e) {
          Log.e(TAG, "Error in put: " + e.getMessage());
        } catch (final IOException e) {
          Log.e(TAG, "Error in put: " + e.getMessage());
        }
      }
    }
  }
  
  private void put(String key, String file) {
    mLinkedHashMap.put(key, file);
    cacheSize = mLinkedHashMap.size();
    cacheByteSize += new File(file).length();
  }
  
  /**
   * Flush the cache, removing oldest entries if the total size is over the specified cache size.
   * Note that this isn't keeping track of stale files in the cache directory that aren't in the
   * HashMap. If the images and keys in the disk cache change often then they probably won't ever
   * be removed.
   */
  private void flushCache() {
    Map.Entry<String, String> eldestEntry;
    File eldestFile;
    long eldestFileSize;
    int count = 0;
    
    while (count < MAX_REMOVALS &&
      (cacheSize > MAX_CACHE_SIZE_ITEM || cacheByteSize > maxCacheByteSize)) {
      eldestEntry = mLinkedHashMap.entrySet().iterator().next();
      eldestFile = new File(eldestEntry.getValue());
      eldestFileSize = eldestFile.length();
      mLinkedHashMap.remove(eldestEntry.getKey());
      eldestFile.delete();
      cacheSize = mLinkedHashMap.size();
      cacheByteSize -= eldestFileSize;
      count++;
      
    }
  }
  
  /**
   * Get an image from the disk cache.
   *
   * @param key The unique identifier for the bitmap
   * @return The bitmap or null if not found
   */
  public Bitmap get(String key) {
    synchronized (mLinkedHashMap) {
      final String file = mLinkedHashMap.get(key);
      if (file != null) {
        
        return BitmapFactory.decodeFile(file);
      } else {
        final String existingFile = createFilePath(mCacheDir, key);
        if (new File(existingFile).exists()) {
          put(key, existingFile);
          
          return BitmapFactory.decodeFile(existingFile);
        }
      }
      return null;
    }
  }
  
  /**
   * Checks if a specific key exist in the cache.
   *
   * @param key The unique identifier for the bitmap
   * @return true if found, false otherwise
   */
  public boolean containsKey(String key) {
    // See if the key is in our HashMap
    if (mLinkedHashMap.containsKey(key)) {
      return true;
    }
    
    // Now check if there's an actual file that exists based on the key
    final String existingFile = createFilePath(mCacheDir, key);
    if (new File(existingFile).exists()) {
      // File found, add it to the HashMap for future use
      put(key, existingFile);
      return true;
    }
    return false;
  }
  
  /**
   * Removes all disk cache entries from this instance cache dir
   */
  public void clearCache() {
    DiskLruCache.clearCache(mCacheDir);
  }
  
  /**
   * Removes all disk cache entries from the application cache directory in the uniqueName
   * sub-directory.
   *
   * @param context    The context to use
   * @param uniqueName A unique cache directory name to append to the app cache directory
   */
  public static void clearCache(Context context, String uniqueName) {
    File cacheDir = CacheManager.getDiskCacheDir(context, uniqueName);
    clearCache(cacheDir);
  }
  
  /**
   * Removes all disk cache entries from the given directory. This should not be called directly,
   * call {@link DiskLruCache#clearCache(Context, String)} or {@link DiskLruCache#clearCache()}
   * instead.
   *
   * @param cacheDir The directory to remove the cache files from
   */
  private static void clearCache(File cacheDir) {
    final File[] files = cacheDir.listFiles(cacheFileFilter);
    for (int i = 0; i < files.length; i++) {
      files[i].delete();
    }
  }
  
  
  /**
   * Creates a constant cache file mPath given a target cache directory and an image key.
   *
   * @param cacheDir
   * @param key
   * @return
   */
  public static String createFilePath(File cacheDir, String key) {
    try {
      // Use URLEncoder to ensure we have a valid filename, a tad hacky but it will do for
      // this example
      return cacheDir.getAbsolutePath() + File.separator +
        CACHE_FILENAME_PREFIX + URLEncoder.encode(key.replace("*", ""), "UTF-8");
    } catch (final UnsupportedEncodingException e) {
      Log.e(TAG, "createFilePath - " + e);
    }
    
    return null;
  }
  
  /**
   * Create a constant cache file mPath using the current cache directory and an image key.
   *
   * @param key
   * @return
   */
  public String createFilePath(String key) {
    return createFilePath(mCacheDir, key);
  }
  
  /**
   * Sets the target compression format and quality for images written to the disk cache.
   *
   * @param compressFormat
   * @param quality
   */
  public void setCompressParams(Bitmap.CompressFormat compressFormat, int quality) {
    mCompressFormat = compressFormat;
    mCompressQuality = quality;
  }
  
  /**
   * Writes a bitmap to a file. Call {@link DiskLruCache#setCompressParams(Bitmap.CompressFormat, int)}
   * first to set the target bitmap compression and format.
   *
   * @param bitmap
   * @param file
   * @return
   */
  private boolean writeBitmapToFile(Bitmap bitmap, String file)
    throws IOException, FileNotFoundException {
    
    OutputStream out = null;
    try {
      out = new BufferedOutputStream(new FileOutputStream(file), (8 * 1024));
      return bitmap.compress(mCompressFormat, mCompressQuality, out);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }
}
