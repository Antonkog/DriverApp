package com.redhotapp.driverapp.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by antonio on 19.10.17.
 */

public class CommonUtils {

    private final static String TAG = "CommonUtils";


    public static double getKbFromBytes(long bytes) {
        return bytes / 1024;
    }

    /**
     * Tell if an array contains specified value
     *
     * @param value The value
     * @param array The array
     * @return True if the specified array contains the value, false otherwise
     */
    public static <T> boolean arrayContains(T value, T[] array) {
        return arrayIndexOf(value, array) != -1;
    }

    /**
     * Searches an array for the specified object and returns the index of the first occurrence.
     *
     * @param value The value
     * @param array The array
     * @return Index of value in array
     */
    public static <T> int arrayIndexOf(T value, T[] array) {
        int i = array.length;
        for (; i-- > 0; ) {
            if (array[i].equals(value)) {
                break;
            }
        }
        return i;
    }

    public static String loadFileAsString(String filePath) {
        StringBuffer data = new StringBuffer(1000);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                data.append(readData);
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return data.toString();
    }

    public static void loadData(String html, WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webView.loadData(html, "text/html; charset=UTF-8", "utf-8");
    }


    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(int px, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}