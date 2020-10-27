package com.goldsign.commu.frame.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hejj
 */
public class DateHelper {
    //

    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String HHmm = "HHmm";
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    // private static int N = 0;
    /**
     * 时间格式:yyyyMMdd
     */
    public static final SimpleDateFormat YYYYMMDD = new SimpleDateFormat(
            yyyyMMdd);
    /**
     * 时间格式:HHmm
     */
    public static final SimpleDateFormat HHMM = new SimpleDateFormat(HHmm);
    /**
     * 时间格式:yyyyMMddHHmmss
     */
    public static final SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat(
            yyyyMMddHHmmss);
    /**
     * 时间格式:yyyy-MM-dd
     */
    public static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat(
            yyyy_MM_dd);
    /**
     * 时间格式:yyyy-MM-dd HH:mm:ss
     */
    public static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
            yyyy_MM_dd_HH_mm_ss);

    public static String datetimeToStringOnlyDateF(Date d) {
        return YYYYMMDD.format(d);
    }

    public static String curentDateToHHMM() {
        return HHMM.format(new Date());
    }

    public static String curentDateToYYYYMMDDHHMMSS() {
        return YYYYMMDDHHMMSS.format(new Date());
    }

    /**
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToStr(Date date, String format) {
        String dateStr = "";
        if (yyyyMMdd.equals(format)) {
            dateStr = YYYYMMDD.format(date);
        } else if (yyyy_MM_dd.equals(format)) {
            dateStr = YYYY_MM_DD.format(date);
        } else if (yyyyMMddHHmmss.equals(format)) {
            dateStr = YYYYMMDDHHMMSS.format(date);
        } else if (yyyy_MM_dd_HH_mm_ss.equals(format)) {
            dateStr = YYYY_MM_DD_HH_MM_SS.format(date);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            dateStr = sdf.format(date);

        }
        return dateStr;
    }

    /**
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToHHMM(Date date) {
        String dateStr = HHMM.format(date);
        return dateStr;
    }

    /**
     *
     * @param curDate
     * @param beforeTime
     * @return
     */
    public static String getDateBefore(String curDate, long beforeTime) {
        Date d;
        String dateBefore = "";
        try {
            d = YYYYMMDD.parse(curDate);
            Date datePre = new Date(d.getTime() - beforeTime);
            dateBefore = YYYYMMDD.format(datePre);
        } catch (ParseException e) {
            Logger.getLogger(DateHelper.class.getName()).log(Level.SEVERE,
                    null, e);
        }
        return dateBefore;

    }

    public static String getDateAfter(String curDate, long afterTime) {

        Date d;
        String dateAfter = "";
        try {
            d = YYYYMMDD.parse(curDate);
            Date datePre = new Date(d.getTime() + afterTime);
            dateAfter = YYYYMMDD.format(datePre);
        } catch (ParseException e) {
            Logger.getLogger(DateHelper.class.getName()).log(Level.SEVERE,
                    null, e);
        }
        return dateAfter;

        // df.
    }

    public static String getDateTimeAfter(String curDate, long afterTime) {

        Date d;
        String dateAfter = "";
        try {
            d = YYYYMMDDHHMMSS.parse(curDate);
            Date datePre = new Date(d.getTime() + afterTime);
            dateAfter = YYYYMMDDHHMMSS.format(datePre);
        } catch (ParseException e) {
            Logger.getLogger(DateHelper.class.getName()).log(Level.SEVERE,
                    null, e);
        }
        return dateAfter;

    }

    public static String dateToString(Date d) {
        return YYYYMMDDHHMMSS.format(d);
    }

    public static String dateOnlyToString(Date d) {
        return YYYYMMDD.format(d);
    }

    public static String dateToStringName(Date d) {
        return YYYY_MM_DD_HH_MM_SS.format(d);
    }

    public static String datetimeToString(Date d) {
        return YYYY_MM_DD_HH_MM_SS.format(d);
    }

    public static String datetimeToStringByDate(Date d) {

        return YYYY_MM_DD.format(d);
    }

    // public static void screenPrintForEx(Map m) {
    // String date = datetimeToString(new Date());
    // String key;
    // Long value;
    // Set set = m.keySet();
    // Iterator it = set.iterator();
    // while (it.hasNext()) {
    // key = (String) it.next();
    // value = (Long) m.get(key);
    // logger.info(date + "  " + key + "=" + value);
    // }
    // }
    public static String currentTodToString() {
        Date d = new Date(System.currentTimeMillis());
        return dateToString(d);
    }

    public static Timestamp utilDateToSqlTimestamp(
            Date utilDate) {
        return new Timestamp(utilDate.getTime());
    }

    public static Timestamp getCurrentSqlTimestamp() {
        Date utilDate = new Date(System.currentTimeMillis());
        return new Timestamp(utilDate.getTime());
    }

    public static long getDiffer(String date, Date date1) throws ParseException {

        Date d = YYYY_MM_DD_HH_MM_SS.parse(date);
        long n1 = date1.getTime();
        long n = d.getTime();
        return Math.abs(n1 - n);

    }

    public static long getDiffer(String date, String date1)
            throws ParseException {

        Date d = YYYY_MM_DD_HH_MM_SS.parse(date);
        Date d1 = YYYY_MM_DD_HH_MM_SS.parse(date1);
        long n1 = d1.getTime();
        long n = d.getTime();
        return Math.abs(n1 - n);

    }

    public static long getDifferInDays(String date, String date1)
            throws ParseException {

        Date d = YYYY_MM_DD.parse(date);
        Date d1 = YYYY_MM_DD.parse(date1);
        long n1 = d1.getTime();
        long n = d.getTime();
        long days = (n1 - n) / (24 * 3600 * 1000);
        return days;

    }

    public static String convertStandFormat(String ymd) {
        return ymd.substring(0, 4) + "-" + ymd.substring(4, 6) + "-"
                + ymd.substring(6, 8);
    }

    public static boolean isGEIndicateDays(String date, Date date1, int difDays)
            throws ParseException {

        Date d = YYYY_MM_DD.parse(date);
        long n1 = date1.getTime();
        long n = d.getTime();
        long dif = difDays * 24 * 3600 * 1000;
        if (n1 - n >= dif) {
            return true;
        }
        return false;

    }

    public static boolean isLEIndicateDays(String date, Date date1, int difDays)
            throws ParseException {

        Date d = YYYY_MM_DD.parse(date);
        long n1 = date1.getTime();
        long n = d.getTime();
        long dif = difDays * 24 * 3600 * 1000;
        if (n1 - n <= dif) {
            return true;
        }
        return false;

    }

    public static Date dateStrToUtilDate(String dateStr) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        return YYYYMMDDHHMMSS.parse(dateStr, pos);
    }

    public static Timestamp dateStrToSqlTimestamp(String dateStr)
            throws ParseException {
        Date utilDate = dateStrToUtilDate(dateStr);
        return new Timestamp(utilDate.getTime());
    }

    public static Date dateStrToUtilDate1(String dateStr)
            throws ParseException {
        return YYYY_MM_DD_HH_MM_SS.parse(dateStr);
    }

    public static Timestamp dateStrToSqlTimestamp1(String dateStr)
            throws ParseException {
        Date utilDate = dateStrToUtilDate1(dateStr);
        return new Timestamp(utilDate.getTime());
    }
    
    
    /*
    日期加减（天）
    */
    public static Date addDay(Date date, int n){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, n);
        return calendar.getTime();
    }
    
}
