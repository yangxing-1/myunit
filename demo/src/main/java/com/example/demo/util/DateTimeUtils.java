package com.example.demo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Date;

import com.example.demo.exception.ConversionFailedException;


public class DateTimeUtils {
    private static final String DEFAULT_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    // formatter to format java.util.Date to string
    public static final ThreadLocal<DateFormat> TL_DATETIME_FORMATTER = getThreadLocalDateFormat(DEFAULT_FORMAT_PATTERN);
    public static final ThreadLocal<DateFormat> TL_DATE_FORMATTER = getThreadLocalDateFormat("yyyy-MM-dd");
    public static final ThreadLocal<DateFormat> TL_TIME_FORMATTER = getThreadLocalDateFormat("HH:mm:ss");
    
    // formatter to format string to java.time.LocalDateTime or java.time.LocalDate or java.time.LocalTime, vice versa
    public static final ThreadLocal<DateTimeFormatter> TL_LOCAL_DATETIME_FORMATTER = getThreadLocalDateTimeFormatter(DEFAULT_FORMAT_PATTERN);
    public static final ThreadLocal<DateTimeFormatter> TL_LOCAL_DATE_FORMATTER = getThreadLocalDateTimeFormatter("yyyy-MM-dd");
    public static final ThreadLocal<DateTimeFormatter> TL_LOCAL_TIME_FORMATTER = getThreadLocalDateTimeFormatter("HH:mm:ss");
    public static final ThreadLocal<DateTimeFormatter> TL_LOCAL_HOUR_MINUTE_FORMATTER = getThreadLocalDateTimeFormatter("HH:mm");

    // formatter to format java.time.LocalDateTime or java.time.LocalDate or java.time.LocalTime to serial number
    public static final ThreadLocal<DateTimeFormatter> TL_DATE_SERIAL_NUMBER_FORMATTER = getThreadLocalDateTimeFormatter("yyyyMMdd");
    public static final ThreadLocal<DateTimeFormatter> TL_DATETIME_SERIAL_NUMBER_FORMATTER = getThreadLocalDateTimeFormatter("yyyyMMddHHmmss");
    
    public static final String getDateTimeSerialNumber(LocalDateTime dateTime) {
        return dateTime.format(TL_DATETIME_SERIAL_NUMBER_FORMATTER.get());
    }
    
    public static final String getDateSerialNumber(LocalDate date) {
        return date.format(TL_DATE_SERIAL_NUMBER_FORMATTER.get());
    }
    
    public static final String getDateSerialNumber(LocalDateTime dateTime) {
        return dateTime.format(TL_DATE_SERIAL_NUMBER_FORMATTER.get());
    }
    
    public static final String formatLocalDateTime(final LocalDateTime dateTime, final DateTimeFormatter formatter) {
        if (dateTime == null) {
            return null;
        }
        
        return dateTime.format(formatter);
    }
    
    public static final String formatLocalDateTime(final LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        
        return dateTime.format(TL_LOCAL_DATETIME_FORMATTER.get());
    }
    
    public static final String formatDateTime(final Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        
        return TL_DATETIME_FORMATTER.get().format(dateTime);
    }
    
    public static final String formatTime(final Date Time) {
        if (Time == null) {
            return null;
        }
        
        return TL_TIME_FORMATTER.get().format(Time);
    }
    
    public static final String formatLocalDate(final LocalDate date, final DateTimeFormatter formatter) {
        if (date == null) {
            return null;
        }
        
        return date.format(formatter);
    }
    
    public static final String formatLocalDate(final LocalDate date) {
        if (date == null) {
            return null;
        }
        
        return date.format(TL_LOCAL_DATE_FORMATTER.get());
    }
    
    @Deprecated
    public static final String formatDate(final Date date) {
        if (date == null)
            return null;
        
        return TL_DATE_FORMATTER.get().format(date);
    }
    
    public static final String formatLocalTime(final LocalTime time) {
        if (time == null) 
            return null;
        
        return TL_LOCAL_TIME_FORMATTER.get().format(time);
    }
    
    @Deprecated
    public static final Date parseDate(final String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        
        String tempSource = getStandardDateText(source);
        
        try {
            return TL_DATETIME_FORMATTER.get().parse(tempSource);
        } catch (ParseException pe) {
            /* ignore */
        }
        
        // 存在毫秒或纳秒
        if (StringUtils.contains(tempSource, ".")) {
            try {
                int qty = tempSource.length() - (tempSource.lastIndexOf(".") + 1);
                
                if (qty > 0) {
                    return new SimpleDateFormat(DEFAULT_FORMAT_PATTERN + "." + StringUtils.repeat("S", qty)).parse(tempSource);
                }
            } catch (ParseException pe) {
                /* ignore */
            }
        }
        
        try {
            return TL_DATE_FORMATTER.get().parse(tempSource);
        } catch (ParseException pe) {
            /* ignore */
        }
        
        throw new ConversionFailedException(String.class, Date.class, source);
    }
    
    public static final LocalDate parseLocalDate(final String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        
        String tempSource = getStandardDateText(source);
        
        try {
            return LocalDate.parse(tempSource, TL_LOCAL_DATE_FORMATTER.get());
        } catch(DateTimeParseException dtpe) {
            /* ignore */
        }
        
        try {
            return LocalDateTime.parse(tempSource, TL_LOCAL_DATETIME_FORMATTER.get()).toLocalDate();
        } catch(DateTimeParseException dtpe) {
            /* ignore */
        }
        
        // 存在毫秒或纳秒
        if (StringUtils.contains(tempSource, ".")) {
            try {
                int qty = tempSource.length() - (tempSource.lastIndexOf(".") + 1);
                
                if (qty > 0) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT_PATTERN + "." + StringUtils.repeat("S", qty));
                    
                    return LocalDateTime.parse(tempSource, formatter).toLocalDate();
                }
            } catch (Exception pe) {
                /* ignore */
            }
        }
        
        throw new ConversionFailedException(String.class, LocalDate.class, source);
    }
    
    public static final LocalDateTime parseLocalDateTime(final String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        
        String tempSource = getStandardDateText(source);
        
        try {
            return LocalDateTime.parse(tempSource, TL_LOCAL_DATETIME_FORMATTER.get());
        } catch(DateTimeParseException dtpe) {
            /* ignore */
        }
        
        try {
            return LocalDate.parse(tempSource, TL_LOCAL_DATE_FORMATTER.get()).atStartOfDay();
        } catch(DateTimeParseException dtpe) {
            /* ignore */
        }
        
        // 存在毫秒或纳秒
        if (StringUtils.contains(tempSource, ".")) {
            try {
                int qty = tempSource.length() - (tempSource.lastIndexOf(".") + 1);
                
                if (qty > 0) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT_PATTERN + "." + StringUtils.repeat("S", qty));
                    
                    return LocalDateTime.parse(tempSource, formatter);
                }
            } catch (Exception pe) {
                /* ignore */
            }
        }
        
        throw new ConversionFailedException(String.class, LocalDateTime.class, source);
    }
    
    public static final LocalTime parseLocalTime(final String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        
        try {
            return LocalTime.parse(source, TL_LOCAL_TIME_FORMATTER.get());
        } catch(DateTimeParseException dtpe) {
            /* ignore */
        }
        
        try {
            return LocalTime.parse(source, TL_LOCAL_HOUR_MINUTE_FORMATTER.get());
        } catch(DateTimeParseException dtpe) {
            /* ignore */
        }
        
        
        try {
            return LocalDateTime.parse(source, TL_LOCAL_DATETIME_FORMATTER.get()).toLocalTime();
        } catch(DateTimeParseException dtpe) {
            /* ignore */
        }
        
        // 存在毫秒或纳秒
        if (StringUtils.contains(source, ".")) {
            try {
                int qty = source.length() - (source.lastIndexOf(".") + 1);
                
                if (qty > 0) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT_PATTERN + "." + StringUtils.repeat("S", qty));
                    
                    return LocalDateTime.parse(source, formatter).toLocalTime();
                }
            } catch (DateTimeParseException pe) {
                /* ignore */
            }
        }
        
        throw new ConversionFailedException(String.class, LocalTime.class, source);
    }
    
//    public static final long getDurationInMilliseconds(Temporal start, Temporal end) {
//        return ConvertUtils.convert(end, Long.class) - ConvertUtils.convert(start, Long.class);
//    }
//    
//    @SuppressWarnings("unchecked")
//    public static final <T> T addDay(T time, int day) {
//        return (T) ConvertUtils.convert(new Date(ConvertUtils.convert(time, Date.class).getTime() + 1000 * 60 * 60 * 24 * day), time.getClass());
//    }
    
    public static final LocalDateTime plusYears(LocalDateTime time, Long years) {
        if (null == time || null == years) {
            return null;
        }
        return time.plusYears(years);
    }
    
    public static final LocalDateTime plusMonths(LocalDateTime time, Long months) {
        if (null == time || null == months) {
            return null;
        }
        return time.plusMonths(months);
    }

    public static final LocalDateTime plusWeeks(LocalDateTime time, Long weeks) {
        if (null == time || null == weeks) {
            return null;
        }
        return time.plusWeeks(weeks);
    }

    public static final LocalDateTime plusDays(LocalDateTime time, Long days) {
        if (null == time || null == days) {
            return null;
        }
        return time.plusDays(days);
    }

    public static final LocalDateTime plusHours(LocalDateTime time, Long hours) {
        if (null == time || null == hours) {
            return null;
        }
        return time.plusHours(hours);
    }

    public static final LocalDateTime plusMinutes(LocalDateTime time, Long minutes) {
        if (null == time || null == minutes) {
            return null;
        }
        return time.plusMinutes(minutes);
    }
    
    public static final LocalDate plusYears(LocalDate date, Long years) {
        if (null == date || null == years) {
            return null;
        }
        return date.plusYears(years);
    }
    
    public static final LocalDate plusMonths(LocalDate date, Long months) {
        if (null == date || null == months) {
            return null;
        }
        return date.plusMonths(months);
    }

    public static final LocalDate plusWeeks(LocalDate date, Long weeks) {
        if (null == date || null == weeks) {
            return null;
        }
        return date.plusWeeks(weeks);
    }

    public static final LocalDate plusDays(LocalDate date, Long days) {
        if (null == date || null == days) {
            return null;
        }
        return date.plusDays(days);
    }
    
    public static final boolean isBefore(LocalDate d1, LocalDate d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        
        return d1.isBefore(d2);
    }
    
    public static final boolean isAfter(LocalDate d1, LocalDate d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        
        return d1.isAfter(d2);
    }
    
    public static final boolean isEqual(LocalDate d1, LocalDate d2) {
        if (d1 == null || d2 == null) {
            return d1 == null && d2 == null;
        }
        
        return d1.isEqual(d2);
    }
    
    public static final boolean isBefore(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        
        return t1.isBefore(t2);
    }
    
    public static final boolean isAfter(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        
        return t1.isAfter(t2);
    }
    
    public static final boolean isEqual(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null || t2 == null) {
            return t1 == null && t2 == null;
        }
        
        return t1.isEqual(t2);
    }
    
    public static final LocalDateTime max(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null) {
            return t2;
        }
        
        if (t2 == null) {
            return t1;
        }
        
        if (t1.isAfter(t2)) {
            return t1;
        }
        
        return t2;
    }
    
    public static final LocalDateTime min(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null) {
            return t2;
        }
        
        if (t2 == null) {
            return t1;
        }
        
        if (t1.isBefore(t2)) {
            return t1;
        }
        
        return t2;
    }
    
    public static final boolean isLocalDate(String text) {
        try {
            parseLocalDate(text);
            
            return true;
        } catch (ConversionFailedException cfe) {
            return false;
        }
    }
    
    public static final boolean isLocalDateTime(String text) {
        try {
            parseLocalDateTime(text);
            
            return true;
        } catch (ConversionFailedException cfe) {
            return false;
        }
    }
    
    //-------------------------------------------------------------------------------
    // 私有方法
    //-------------------------------------------------------------------------------
    private static final String getStandardDateText(String dateText) {
        StringBuilder sb = new StringBuilder();
        
        int zoneIndex = Math.max(dateText.lastIndexOf("-"), dateText.lastIndexOf("+"));
        if (zoneIndex >= "yyyy-M-d H:m:s".length()) { // 有时区
            dateText = dateText.substring(0, zoneIndex).trim();
        }
        
        String[] values = dateText.split("(-|\\/|年|月|日|:|T|t|\\.|\\s)");
        
        for (int i = 0, j = values.length; i < j; i++) {
            String value = values[i];
            
            if (i == 0) { // 年
                sb.append(value);
            } else if (i <= 2) { // 月、日
                sb.append("-").append(StringUtils.leftPad(value, 2));
            } else if (i == 3) { // 时
                sb.append(" ").append(StringUtils.leftPad(value, 2));
            } else if (i <= 5) { // 分、秒
                sb.append(":").append(StringUtils.leftPad(value, 2));
            } else if (i == 6) { // 毫秒
                sb.append(".").append(value);
            }
        }
        
        // 没有秒时，自动补上秒
        if (sb.length() == "yyyy-MM-dd HH:mm".length()) {
            sb.append(":00");
        }
        
        return sb.toString();
    }
    
    private static ThreadLocal<DateFormat> getThreadLocalDateFormat(String pattern) {
        return new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(pattern);
            }
        };
    }
    
    private static ThreadLocal<DateTimeFormatter> getThreadLocalDateTimeFormatter(String pattern) {
        return new ThreadLocal<DateTimeFormatter>() {
            @Override
            protected DateTimeFormatter initialValue() {
                return DateTimeFormatter.ofPattern(pattern);
            }
        };
    }
}