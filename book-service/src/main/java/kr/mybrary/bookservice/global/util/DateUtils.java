package kr.mybrary.bookservice.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String toHyphenFormatYYYMMddHHmm(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "0000-00-00 00:00";
        }

        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static String toDotFormatYYYYMMDD(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "0000.00.00";
        }

        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    public static String toHyphenYYYYMMDD(LocalDate localDate) {
        if (localDate == null) {
            return "0000-00-00";
        }

        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
