package com.quizchii.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {
    static long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    public static Timestamp convertStringToTimestamp(String strDate) {
        try {
            Date date = formatter.parse(strDate);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    public static int daysBetween(long time1, long time2) {
        // Set both times to 0:00:00
        time1 -= time1 % MILLIS_PER_DAY;
        time2 -= time2 % MILLIS_PER_DAY;
        int res = (int) TimeUnit.DAYS.convert(time1 - time2, TimeUnit.MILLISECONDS);
        return Math.abs(res);
    }

    public static boolean isSameDay(Timestamp t1, Timestamp t2) {
        return simpleDateFormat.format(t1).equals(simpleDateFormat.format(t2));
    }
}