package org.recap.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by premkb on 20/8/16.
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    public static Date getDateFromString(String inputDateString,String dateFormat){
        Date outputDate=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            if(inputDateString != null) {
                outputDate = sdf.parse(inputDateString);
            }
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return outputDate;
    }

    public static Date getDateTimeFromString(String inputDateTimeString, String dateTimeFormat){
        Date outputDateTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        try{
            LocalDateTime parsedDateTime = LocalDateTime.parse(inputDateTimeString, formatter);
            outputDateTime = Date.from(parsedDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return outputDateTime;
    }
}
