package com.nmerris.roboresumedb;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {

    public static String getTodaysDateString() {
        String todaysDateString = "";
        Date today = new Date();
        SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMM");
        todaysDateString += (dateFormatMonth.format(today) + " ");
        SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd");
        todaysDateString += (dateFormatDay.format(today) + ", ");
        SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        todaysDateString += (dateFormatYear.format(today));
        return todaysDateString;
    }



    public static String getMonthDayYearFromDate(Date date) {
        String dateString = "";
        SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMM");
        dateString += (dateFormatMonth.format(date) + " ");
        SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd");
        dateString += (dateFormatDay.format(date) + ", ");
        SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        dateString += (dateFormatYear.format(date));
        return dateString;
    }
    

}
