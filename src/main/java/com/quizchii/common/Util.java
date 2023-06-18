package com.quizchii.common;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static Timestamp convertStringToTimestamp(String strDate) {
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


//    public static String convertStringToTimestamp(Timestamp timestamp) {
//        try {
//            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//            // you can change format of date
//            Date date = formatter.parse(strDate);
//
//            return new Timestamp(date.getTime());
//        } catch (ParseException e) {
//            System.out.println("Exception :" + e);
//            return null;
//        }
//    }
}