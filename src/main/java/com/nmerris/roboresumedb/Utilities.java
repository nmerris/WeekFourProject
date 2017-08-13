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


}
