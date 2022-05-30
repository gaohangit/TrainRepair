package com.ydzbinfo.emis.utils;

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * 自定义字符串工具类
 *
 * @author L.cm
 * @modifier 张天可
 */
public class StringUtils extends org.springframework.util.StringUtils {
    /**
     * Check whether the given {@code CharSequence} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code CharSequence} is not {@code null}, its length is greater than
     * 0, and it contains at least one non-whitespace character.
     * <p><pre class="code">
     * StringUtils.isBlank(null) = true
     * StringUtils.isBlank("") = true
     * StringUtils.isBlank(" ") = true
     * StringUtils.isBlank("12345") = false
     * StringUtils.isBlank(" 12345 ") = false
     * </pre>
     *
     * @param cs the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null},
     * its length is greater than 0, and it does not contain whitespace only
     * @see Character#isWhitespace
     */
    public static boolean isBlank(final CharSequence cs) {
        return !StringUtils.hasText(cs);
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     * not empty and not null and not whitespace
     * @see Character#isWhitespace
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return StringUtils.hasText(cs);
    }

    /**
     * Convert a {@code Collection} into a delimited {@code String} (e.g. CSV).
     * <p>Useful for {@code toString()} implementations.
     *
     * @param coll  the {@code Collection} to convert
     * @param delim the delimiter to use (typically a ",")
     * @return the delimited {@code String}
     */
    public static String join(Collection<?> coll, String delim) {
        return StringUtils.collectionToDelimitedString(coll, delim);
    }

    /**
     * Convert a {@code String} array into a delimited {@code String} (e.g. CSV).
     * <p>Useful for {@code toString()} implementations.
     *
     * @param arr   the array to display
     * @param delim the delimiter to use (typically a ",")
     * @return the delimited {@code String}
     */
    public static String join(Object[] arr, String delim) {
        return StringUtils.arrayToDelimitedString(arr, delim);
    }

    // public static boolean isNotBlank(String str) {
    //     boolean b = true;
    //     if (str == null) {
    //         b = false;
    //     } else {
    //         if (str.trim().equals("")) {
    //             b = false;
    //         }
    //         if ("undefined".equals(str)) {
    //             b = false;
    //         }
    //     }
    //     return b;
    // }

    /**
     * @Author： ZhangQingFang
     * @Description: String 右对齐
     * @Date: 17:33 2019/9/27
     */
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff < 0) {
            return src;
        }
        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    /**
     * @Author： ZhangQingFang
     * @Description: String 左对齐
     * @Date: 17:33 2019/9/27
     */
    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff < 0) {
            return src;
        }
        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    /**
     * 转千位分隔符
     *
     * @param number
     * @return
     */
    public static String thousandFormat(long number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }

}
