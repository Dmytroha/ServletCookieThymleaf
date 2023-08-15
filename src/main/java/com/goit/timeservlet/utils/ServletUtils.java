package com.goit.timeservlet.utils;

import java.time.DateTimeException;
import java.time.ZoneId;

public final class ServletUtils {
    public static final String TIMEZONE_PARAMETER = "timezone";

    /**
     *  ex:  http://127.0.0.1:8080/ServletCookieThymleaf-0.0.1/time?timezone=UTC+5
     *  '+' replaces to ' '
     *  this method parse string and replaces gap with +
     * @param timeZoneFromURL
     * @return timeZoneFromURL without gap
     */
    public static String replaceGapWithPlusInTimeZoneParamFromURL(String timeZoneFromURL){
        return String.join("+",timeZoneFromURL.split(" "));
    }
    public static boolean isValidTimeZone(String timeZone){
        try {
            ZoneId.of(timeZone);
            return true;
        }catch(DateTimeException e){
            return false;
        }
    }
}
