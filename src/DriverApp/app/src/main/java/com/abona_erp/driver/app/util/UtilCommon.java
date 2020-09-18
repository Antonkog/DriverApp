package com.abona_erp.driver.app.util;

import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.logging.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilCommon {
    public static String TAG = UtilCommon.class.getCanonicalName();

    /**
     * This method will try to find
     * @param someString that string should contain value that is less {@link java.lang.Integer#MAX_VALUE})
     * @return 0 or some number 8998ffff7788sfsf will return 89987788
     * @author Anton Kogan
     */
    public static int parseInt(String someString) {
        int  result = 0;

        StringBuilder partsBuffer = new StringBuilder();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(someString);
        while(m.find()) {
            partsBuffer.append(m.group());
        }

        try {
          result =  Integer.parseInt(partsBuffer.toString());
        } catch (NumberFormatException e){
            Log.e(TAG, " wrong input in getInt(String someString)");
        }

        return result;
    }
}
