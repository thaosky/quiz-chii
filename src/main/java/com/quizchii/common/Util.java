package com.quizchii.common;

import com.quizchii.Enum.SortDir;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {
    static long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat timeToTest = new SimpleDateFormat("mm:ss");
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat formatDateBeauty = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");

    public static Timestamp convertStringToTimestamp(String strDate) {
        try {
            Date date = formatter.parse(strDate);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    public static String convertTimestampToString(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return formatDateBeauty.format(timestamp);
    }

    public static Timestamp addTime(Timestamp oldTime, int minute) {
        long duration = (long) minute * 60 * 1000;
        Timestamp newTime = new Timestamp(oldTime.getTime() + duration);
        return newTime;
    }

    public static int daysBetween(long time1, long time2) {
        // Set both times to 0:00:00
        time1 -= time1 % MILLIS_PER_DAY;
        time2 -= time2 % MILLIS_PER_DAY;
        int res = (int) TimeUnit.DAYS.convert(time1 - time2, TimeUnit.MILLISECONDS);
        return Math.abs(res);
    }

    /**
     * Tính thời gian giữa time 1 và time 2
     * @param startTime thời gian bắt đầu
     * @param endTime thời gian kết thúc
     * @return string theo pattern HH:mm:ss
     */
    public static String timeBetween(Timestamp startTime, Timestamp endTime) {
        return timeToTest.format(endTime.getTime() - startTime.getTime());
    }

    public static boolean isSameDay(Timestamp t1, Timestamp t2) {
        return simpleDateFormat.format(t1).equals(simpleDateFormat.format(t2));
    }

    public static Pageable createPageable(Integer pageSize, Integer pageNo, String sortName, String sortDir) {
        // Paging
        Sort sortable = Sort.by("id").descending();
        if (sortName != null && SortDir.ASC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).ascending();
        } else if (sortName != null && SortDir.DESC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).descending();
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortable);
        return pageable;
    }
}