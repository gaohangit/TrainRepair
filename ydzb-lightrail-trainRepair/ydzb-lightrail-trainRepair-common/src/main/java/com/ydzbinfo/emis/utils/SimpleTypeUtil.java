package com.ydzbinfo.emis.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 简单类型、基本类型、复杂类型共同构成了所有类型
 * <p>
 * 参考 org.apache.ibatis.type.SimpleTypeRegistry
 *
 * @author 张天可
 */
public class SimpleTypeUtil {
    public static final String[] JAVA8_DATE_TIME = {
        "java.time.Instant",
        "java.time.LocalDateTime",
        "java.time.LocalDate",
        "java.time.LocalTime",
        "java.time.OffsetDateTime",
        "java.time.OffsetTime",
        "java.time.ZonedDateTime",
        "java.time.Year",
        "java.time.Month",
        "java.time.YearMonth"
    };
    private static final Set<Class<?>> SIMPLE_TYPE_SET = new HashSet<>();
    private static final Set<Class<?>> PRIMITIVE_TYPE_SET = new HashSet<>();

    /**
     * 特别注意：由于基本类型有默认值，因此在实体类中不建议使用基本类型作为数据库字段类型
     */
    static {
        SIMPLE_TYPE_SET.add(byte[].class);
        SIMPLE_TYPE_SET.add(String.class);
        SIMPLE_TYPE_SET.add(Byte.class);
        SIMPLE_TYPE_SET.add(Short.class);
        SIMPLE_TYPE_SET.add(Character.class);
        SIMPLE_TYPE_SET.add(Integer.class);
        SIMPLE_TYPE_SET.add(Long.class);
        SIMPLE_TYPE_SET.add(Float.class);
        SIMPLE_TYPE_SET.add(Double.class);
        SIMPLE_TYPE_SET.add(Boolean.class);
        SIMPLE_TYPE_SET.add(Date.class);
        SIMPLE_TYPE_SET.add(Timestamp.class);
        SIMPLE_TYPE_SET.add(Class.class);
        SIMPLE_TYPE_SET.add(BigInteger.class);
        SIMPLE_TYPE_SET.add(BigDecimal.class);
        //反射方式设置 java8 中的日期类型
        for (String time : JAVA8_DATE_TIME) {
            registerSimpleTypeSilence(time);
        }
        PRIMITIVE_TYPE_SET.add(boolean.class);
        PRIMITIVE_TYPE_SET.add(byte.class);
        PRIMITIVE_TYPE_SET.add(short.class);
        PRIMITIVE_TYPE_SET.add(int.class);
        PRIMITIVE_TYPE_SET.add(long.class);
        PRIMITIVE_TYPE_SET.add(char.class);
        PRIMITIVE_TYPE_SET.add(float.class);
        PRIMITIVE_TYPE_SET.add(double.class);
    }

    /**
     * 注册新的类型，不存在时不抛出异常
     *
     * @param clazz
     */
    private static void registerSimpleTypeSilence(String clazz) {
        try {
            SIMPLE_TYPE_SET.add(Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            //ignore
        }
    }

    /**
     * 注册新的类型
     *
     * @param clazz
     */
    public static void registerSimpleType(Class<?> clazz) {
        SIMPLE_TYPE_SET.add(clazz);
    }

    /**
     * 注册新的类型
     *
     * @param classes
     */
    public static void registerSimpleType(String classes) {
        if (StringUtils.isNotBlank(classes)) {
            String[] cls = classes.split(",");
            for (String c : cls) {
                try {
                    SIMPLE_TYPE_SET.add(Class.forName(c));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("注册类型出错:" + c, e);
                }
            }
        }
    }

    /**
     * 是否是简单类型
     *
     * @param clazz
     * @return
     * @author zhangtke
     * @date 2018年10月13日 上午1:51:23
     */
    public static boolean isSimpleType(Class<?> clazz) {
        return SIMPLE_TYPE_SET.contains(clazz);
    }

    /**
     * 是否是基本类型
     *
     * @param clazz
     * @return
     * @author zhangtke
     * @date 2018年10月13日 上午1:51:38
     */
    public static boolean isPrimitiveType(Class<?> clazz) {
        return PRIMITIVE_TYPE_SET.contains(clazz);
    }

    /**
     * 是否是复杂类型
     *
     * @param clazz
     * @return
     * @author zhangtke
     * @date 2018年10月13日 上午1:51:53
     */
    public static boolean isComplexType(Class<?> clazz) {
        return !(PRIMITIVE_TYPE_SET.contains(clazz) || SIMPLE_TYPE_SET.contains(clazz));
    }
}

