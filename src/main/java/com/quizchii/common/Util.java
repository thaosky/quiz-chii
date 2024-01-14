package com.quizchii.common;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {
    public static Timestamp convertTimestampToString(String strDate) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // you can change format of date
            Date date = formatter.parse(strDate);

            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    static long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;
    public static int daysBetween(long time1, long time2) {
        // Set both times to 0:00:00
        time1 -= time1 % MILLIS_PER_DAY;
        time2 -= time2 % MILLIS_PER_DAY;
        int res = (int) TimeUnit.DAYS.convert(time1 - time2, TimeUnit.MILLISECONDS);
        return Math.abs(res);
    }
}