package com.hisu.zola.util.converter;

import java.time.Instant;
import java.util.Date;

public class TimeConverterUtil {
    public static Date getDateFromString(String dateStr) {
        return Date.from(Instant.parse(dateStr));
    }
}