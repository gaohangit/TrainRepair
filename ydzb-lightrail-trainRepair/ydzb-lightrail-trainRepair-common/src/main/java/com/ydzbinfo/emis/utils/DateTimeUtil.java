package com.ydzbinfo.emis.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * java8时间转换工具类(带缓存)
 * 功能
 * 1、java8时间类与基本时间类的转换
 * 2、日期、时间对象与字符串的互相转换
 *
 * @author 张天可
 */
public class DateTimeUtil {
    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();
    private static final int PATTERN_CACHE_SIZE = 500;

    private static final String DEFAULT_DATE_FORMAT = Constants.DEFAULT_DATE_FORMAT;
    private static final String DEFAULT_TIME_FORMAT = Constants.DEFAULT_TIME_FORMAT;
    private static final String DEFAULT_DATE_TIME_FORMAT = Constants.DEFAULT_DATE_TIME_FORMAT;


    /**
     * Date转换为格式化时间
     *
     * @param date    date
     * @param pattern 格式
     * @return
     */
    public static String format(Date date, String pattern) {
        return formatLocalDateTime(LocalDateTime.ofInstant(date.toInstant(),
            ZoneId.systemDefault()), pattern);
    }

    public static String format(Date date) {
        return format(date, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * LocalDateTime转换为格式化时间
     *
     * @param localDateTime localDateTime
     * @param pattern       格式
     * @return
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return localDateTime.format(formatter);
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * LocalDate转换为格式化时间
     *
     * @param localDate localDate
     * @param pattern   格式
     * @return
     */
    public static String formatLocalDate(LocalDate localDate, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return localDate.format(formatter);
    }

    public static String formatLocalDate(LocalDate localDate) {
        return formatLocalDate(localDate, DEFAULT_DATE_FORMAT);
    }

    /**
     * LocalTime转换为格式化时间
     *
     * @param localTime localTime
     * @param pattern   格式
     * @return
     */
    public static String formatLocalTime(LocalTime localTime, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return localTime.format(formatter);
    }

    public static String formatLocalTime(LocalTime localTime) {
        return formatLocalTime(localTime, DEFAULT_TIME_FORMAT);
    }

    /**
     * 格式化字符串转为Date
     *
     * @param dateTimeString 格式化时间
     * @param pattern        格式
     * @return
     */
    public static Date parse(String dateTimeString, String pattern) {
        return Date.from(parseLocalDateTime(dateTimeString, pattern)
            .atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date parse(String dateTimeString) {
        return parse(dateTimeString, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 格式化字符串转为LocalDateTime
     *
     * @param dateTimeString 格式化时间
     * @param pattern        格式
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeString, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    /**
     * 格式化字符串转为LocalDateTime
     * 使用当前日期补全未提供的部分
     *
     * @param dateTimeString
     * @param pattern
     * @return
     */
    public static LocalDateTime parseLocalDateTimeToday(String dateTimeString, String pattern) {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .append(createCacheFormatter(pattern))
            .parseDefaulting(ChronoField.YEAR_OF_ERA, localDate.getYear())
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, localDate.getMonthValue())
            .parseDefaulting(ChronoField.DAY_OF_MONTH, localDate.getDayOfMonth())
            .toFormatter();
        return LocalDateTime.parse(dateTimeString, dateTimeFormatter);
    }

    public static LocalDateTime parseLocalDateTime(String dateTimeString) {
        return parseLocalDateTime(dateTimeString, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 格式化字符串转为LocalDate
     *
     * @param dateString
     * @param pattern
     * @return
     */
    public static LocalDate parseLocalDate(String dateString, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return LocalDate.parse(dateString, formatter);
    }


    public static LocalDate parseLocalDate(String dateString) {
        return parseLocalDate(dateString, DEFAULT_DATE_FORMAT);
    }


    /**
     * 格式化字符串转为LocalTime
     *
     * @param timeString
     * @param pattern
     * @return
     */
    public static LocalTime parseLocalTime(String timeString, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return LocalTime.parse(timeString, formatter);
    }

    public static LocalTime parseLocalTime(String timeString) {
        return parseLocalTime(timeString, DEFAULT_TIME_FORMAT);
    }

    /**
     * 在缓存中创建DateTimeFormatter
     *
     * @param pattern 格式
     * @return
     */
    private static DateTimeFormatter createCacheFormatter(String pattern) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Invalid pattern specification");
        }
        DateTimeFormatter formatter = FORMATTER_CACHE.get(pattern);
        if (formatter == null) {
            formatter = new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();
            if (FORMATTER_CACHE.size() < PATTERN_CACHE_SIZE) {
                DateTimeFormatter oldFormatter = FORMATTER_CACHE
                    .putIfAbsent(pattern, formatter);
                if (oldFormatter != null) {
                    formatter = oldFormatter;
                }
            }
        }
        return formatter;
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalTime localTime) {
        return Date.from(localTime.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime asLocalTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
