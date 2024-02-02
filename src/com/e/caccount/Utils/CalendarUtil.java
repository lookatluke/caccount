/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 *
 * @author trito
 */
public class CalendarUtil {

    Calendar calendar = Calendar.getInstance();

    public CalendarUtil() {
    }

    public int getCurrentWeek() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("w");
        return Integer.parseInt(date.format(dtf));
    }

    public String getFileName() {
        String file = "" + getYear() + getMonthInString() + getDateInString();
        return file;
    }

    public String getToday() {
        String today = " (" + getYear() + "." + getMonth() + "." + getDate() + ")";
        return today;
    }

    public int getYear() {
        return calendar.get(1);
    }

    public int getMonth() {
        return calendar.get(2) + 1;
    }

    public int getDate() {

        return calendar.get(5);
    }

    public String getMonthInString() {
        String Smonth = "";
        int month = calendar.get(2) + 1;
        if (month < 10 && month > 0) {
            Smonth = "0" + String.valueOf(month);
        } else {
            Smonth = String.valueOf(month);
        }
        return Smonth;
    }

    public String getDateInString() {
        String date = "";
        int day = calendar.get(5);
        if (day < 10 && day > 0) {
            date = "0" + String.valueOf(day);
        } else {
            date = String.valueOf(day);
        }
        return date;
    }

}
