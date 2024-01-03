package com.job_portal.admin_portal.utils;

import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

@Configuration
public class DateUtils {
    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date convertToDateUsingInstant(LocalDate date) {
        return java.util.Date.from(date.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public String formattedDate(String startDate, SimpleDateFormat format){
        String interviewDate = null;
        try {
            Date formattedInterviewDate = format.parse(startDate);
            LocalDate localDate = convertToLocalDateViaInstant(formattedInterviewDate);
            interviewDate = localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));

        } catch (Exception exception){
            exception.printStackTrace();
        }
        return interviewDate;
    }

}
