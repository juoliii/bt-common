package com.bitian.common.util;

import com.bitian.common.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数值工具类，提供数值解析、格式化、比较等常用操作。
 *
 * @author admin
 */
public class NumberUtil {

    private static final String NUMBER_PARSE_ERROR = "数值转换异常";

    private NumberUtil() {
        // 工具类禁止实例化
    }

    // ==================== 保留原有方法（向后兼容） ====================

    /**
     * 对数值进行四舍五入并保留指定小数位数，返回 float。
     *
     * @param f     数值，null 视为 0
     * @param scale 保留小数位数
     * @return 四舍五入后的 float 值
     */
    public static float twoDecimal(Number f, int scale) {
        return roundToFloat(f, scale);
    }

    /**
     * 若数值为 null 则返回 0，否则返回原值。
     *
     * @param v 数值
     * @return 非 null 数值
     */
    public static Number defaultZeroIfNull(Number v) {
        return v == null ? 0 : v;
    }

    /**
     * 将字符串解析为 Double，支持百分比格式（如 "50%"  → 0.5）。
     *
     * @param str 数值字符串
     * @return 解析后的 Double 值
     * @throws CustomException 字符串为空或格式不合法时抛出
     */
    public static Double parseDouble(String str) {
        if (StringUtils.isBlank(str)) {
            throw new CustomException(NUMBER_PARSE_ERROR);
        }
        String trimmed = str.trim();
        if (trimmed.endsWith("%")) {
            String numeric = trimmed.substring(0, trimmed.length() - 1).trim();
            if (NumberUtils.isCreatable(numeric)) {
                return Double.parseDouble(numeric) * 0.01;
            }
            throw new CustomException(NUMBER_PARSE_ERROR);
        }
        if (NumberUtils.isCreatable(trimmed)) {
            return Double.parseDouble(trimmed);
        }
        throw new CustomException(NUMBER_PARSE_ERROR);
    }

    /**
     * 将字符串解析为 Float，支持百分比格式。
     *
     * @param str 数值字符串
     * @return 解析后的 Float 值
     * @throws CustomException 格式不合法时抛出
     */
    public static Float parseFloat(String str) {
        return BigDecimal.valueOf(parseDouble(str)).floatValue();
    }

    /**
     * 将字符串解析为 Long，支持百分比格式。小数部分将被截断。
     *
     * @param str 数值字符串
     * @return 解析后的 Long 值
     * @throws CustomException 格式不合法时抛出
     */
    public static Long parseLong(String str) {
        return BigDecimal.valueOf(parseDouble(str)).longValue();
    }

    /**
     * 将字符串解析为 Integer，支持百分比格式。小数部分将被截断。
     *
     * @param str 数值字符串
     * @return 解析后的 Integer 值
     * @throws CustomException 格式不合法时抛出
     */
    public static Integer parseInt(String str) {
        return BigDecimal.valueOf(parseDouble(str)).intValue();
    }

    /**
     * 将字符串解析为 Short，支持百分比格式。小数部分将被截断。
     *
     * @param str 数值字符串
     * @return 解析后的 Short 值
     * @throws CustomException 格式不合法时抛出
     */
    public static Short parseShort(String str) {
        return BigDecimal.valueOf(parseDouble(str)).shortValue();
    }

    /**
     * 判断字符串是否为合法数字（支持百分比格式和忽略字符）。
     * <p>
     * 当字符串包含 ignores 中的任意子串时，先移除这些子串再判断是否为合法数字。
     * </p>
     *
     * @param value   待判断字符串
     * @param ignores 需要忽略的子串列表
     * @return 是否为合法数字
     */
    public static boolean isNumber(String value, String... ignores) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        String cleaned = value.trim();
        for (String ignore : ignores) {
            if (StringUtils.isNotEmpty(ignore)) {
                cleaned = cleaned.replace(ignore, "");
            }
        }
        if (cleaned.endsWith("%")) {
            String numeric = cleaned.substring(0, cleaned.length() - 1).trim();
            return NumberUtils.isCreatable(numeric);
        }
        return NumberUtils.isCreatable(cleaned);
    }

    // ==================== 新增实用方法 ====================

    /**
     * 对数值进行四舍五入并保留指定小数位数，返回 BigDecimal。
     *
     * @param value 数值，null 视为 0
     * @param scale 保留小数位数
     * @return 四舍五入后的 BigDecimal
     */
    public static BigDecimal round(Number value, int scale) {
        return toBigDecimal(value).setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 对数值进行四舍五入并保留指定小数位数，返回 double。
     *
     * @param value 数值，null 视为 0
     * @param scale 保留小数位数
     * @return 四舍五入后的 double 值
     */
    public static double roundToDouble(Number value, int scale) {
        return round(value, scale).doubleValue();
    }

    /**
     * 安全解析字符串为 Double，解析失败返回默认值而不抛异常。
     *
     * @param str          数值字符串
     * @param defaultValue 解析失败时的默认值
     * @return 解析后的 Double 值，或默认值
     */
    public static Double parseDoubleSafely(String str, Double defaultValue) {
        try {
            return parseDouble(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 安全解析字符串为 Integer，解析失败返回默认值而不抛异常。
     *
     * @param str          数值字符串
     * @param defaultValue 解析失败时的默认值
     * @return 解析后的 Integer 值，或默认值
     */
    public static Integer parseIntSafely(String str, Integer defaultValue) {
        try {
            return parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 安全解析字符串为 Long，解析失败返回默认值而不抛异常。
     *
     * @param str          数值字符串
     * @param defaultValue 解析失败时的默认值
     * @return 解析后的 Long 值，或默认值
     */
    public static Long parseLongSafely(String str, Long defaultValue) {
        try {
            return parseLong(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将数值限制在指定范围内（含边界）。
     *
     * @param value 输入值
     * @param min   最小值
     * @param max   最大值
     * @param <T>   数值类型
     * @return 限制后的值
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable<T>> T clamp(T value, T min, T max) {
        if (value == null) {
            return min;
        }
        if (value.compareTo(min) < 0) {
            return min;
        }
        if (value.compareTo(max) > 0) {
            return max;
        }
        return value;
    }

    /**
     * 判断数值是否在指定范围内（含边界）。
     *
     * @param value 输入值
     * @param min   最小值（含）
     * @param max   最大值（含）
     * @param <T>   数值类型
     * @return 是否在范围内
     */
    public static <T extends Number & Comparable<T>> boolean inRange(T value, T min, T max) {
        if (value == null) {
            return false;
        }
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    /**
     * 将 Number 转换为 BigDecimal，null 视为 BigDecimal.ZERO。
     * <p>
     * 对于 BigDecimal 类型直接返回，对于浮点类型使用 {@code BigDecimal.valueOf()}
     * 避免浮点精度问题，其他类型通过字符串转换。
     * </p>
     *
     * @param value 数值
     * @return 对应的 BigDecimal
     */
    public static BigDecimal toBigDecimal(Number value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Double || value instanceof Float) {
            return BigDecimal.valueOf(value.doubleValue());
        }
        if (value instanceof Long || value instanceof Integer
                || value instanceof Short || value instanceof Byte) {
            return BigDecimal.valueOf(value.longValue());
        }
        // 兜底：通过字符串转换
        return new BigDecimal(value.toString());
    }

    /**
     * 判断 Number 是否为 null 或等于零。
     *
     * @param value 数值
     * @return 是否为 null 或零
     */
    public static boolean isNullOrZero(Number value) {
        if (value == null) {
            return true;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).signum() == 0;
        }
        return value.doubleValue() == 0.0;
    }

    /**
     * 判断 Number 是否为正数（大于零）。
     *
     * @param value 数值
     * @return 是否为正数
     */
    public static boolean isPositive(Number value) {
        if (value == null) {
            return false;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).signum() > 0;
        }
        return value.doubleValue() > 0.0;
    }

    /**
     * 计算多个数值的合计（BigDecimal 精度）。
     *
     * @param numbers 数值列表
     * @return 合计值
     */
    public static BigDecimal sum(Number... numbers) {
        BigDecimal result = BigDecimal.ZERO;
        if (numbers == null) {
            return result;
        }
        for (Number n : numbers) {
            if (n != null) {
                result = result.add(toBigDecimal(n));
            }
        }
        return result;
    }

    /**
     * 安全除法，除数为零时返回默认值。
     *
     * @param dividend     被除数
     * @param divisor      除数
     * @param scale        结果保留小数位数
     * @param defaultValue 除数为零时的默认值
     * @return 除法结果或默认值
     */
    public static BigDecimal divideSafely(Number dividend, Number divisor, int scale, BigDecimal defaultValue) {
        if (isNullOrZero(divisor)) {
            return defaultValue;
        }
        return toBigDecimal(dividend).divide(toBigDecimal(divisor), scale, RoundingMode.HALF_UP);
    }

    // ==================== 内部方法 ====================

    private static float roundToFloat(Number f, int scale) {
        return toBigDecimal(defaultZeroIfNull(f)).setScale(scale, RoundingMode.HALF_UP).floatValue();
    }
}
