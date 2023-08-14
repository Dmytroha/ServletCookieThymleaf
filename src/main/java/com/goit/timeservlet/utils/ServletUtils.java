package com.goit.timeservlet.utils;

import java.time.DateTimeException;
import java.time.ZoneId;

public final class ServletUtils {
    public static void saveTimeZoneToCookie(){
        //TODO save time zone to cookie

    }

    public static String getTimeZoneFromCookie() {
//TODO read time zone form cookie
        return "UTC";
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
