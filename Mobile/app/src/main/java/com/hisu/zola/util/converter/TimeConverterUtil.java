package com.hisu.zola.util.converter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeConverterUtil {
    public static Date getDateFromString(String dateStr) {
        return Date.from(Instant.parse(dateStr));
    }

    public static String getDateAsString(String dateStr) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        outputFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        SimpleDateFormat compareFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        compareFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        Date date = Date.from(Instant.parse(dateStr));

        String first = compareFormat.format(new Date());
        String sec = compareFormat.format(date);

        boolean isToday = first.equalsIgnoreCase(sec);

        String formattedDate = outputFormat.format(date);

        if (isToday) {
            formattedDate = formattedDate.substring(0, 6) + " Hôm nay";
        }

        return formattedDate;
    }
}