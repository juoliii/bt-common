package com.bitian.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/**
 * 通用验证工具类
 * <p>
 * 提供常用格式的验证方法，包括手机号、邮箱、身份证、URL、IP 地址等。
 * 所有正则均预编译为静态常量以提升性能，所有方法均为 null 安全。
 * </p>
 *
 * @author admin
 */
public class ValidateUtil {

    private ValidateUtil() {
        // 工具类禁止实例化
    }

    // ========================= 预编译正则 =========================

    /** 中国大陆手机号：1开头，第二位3-9，共11位 */
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^1[3-9]\\d{9}$");

    /** 邮箱地址 */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    /** URL（http/https/ftp） */
    private static final Pattern URL_PATTERN =
            Pattern.compile("^(https?|ftp)://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?$",
                    Pattern.CASE_INSENSITIVE);

    /** 18位身份证号（粗校验） */
    private static final Pattern ID_CARD_PATTERN =
            Pattern.compile("^\\d{17}[\\dXx]$");

    /** 整数（含负数） */
    private static final Pattern INTEGER_PATTERN =
            Pattern.compile("^-?\\d+$");

    /** 数字（含小数和负数） */
    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("^-?\\d+(\\.\\d+)?$");

    /** 中文字符 */
    private static final Pattern CHINESE_PATTERN =
            Pattern.compile("[\\u4e00-\\u9fa5]");

    /** 纯中文字符串 */
    private static final Pattern ALL_CHINESE_PATTERN =
            Pattern.compile("^[\\u4e00-\\u9fa5]+$");

    /** IPv4 地址 */
    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    /** IPv6 地址（简化版） */
    private static final Pattern IPV6_PATTERN =
            Pattern.compile("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    /** MAC 地址（冒号或横线分隔） */
    private static final Pattern MAC_PATTERN =
            Pattern.compile("^([0-9a-fA-F]{2}[:\\-]){5}[0-9a-fA-F]{2}$");

    /** 中国大陆车牌号（含新能源） */
    private static final Pattern LICENSE_PLATE_PATTERN =
            Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤川青藏琼宁][A-HJ-NP-Z]" +
                    "[A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$");

    /** 用户名：字母开头，允许字母数字下划线，4-20位 */
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{3,19}$");

    /**
     * 强密码：至少8位，包含大写字母、小写字母、数字和特殊字符
     */
    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");

    /** 十六进制颜色值 */
    private static final Pattern HEX_COLOR_PATTERN =
            Pattern.compile("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");

    /** UUID */
    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    /** 中文姓名（2-6个汉字，支持中间的·号，如少数民族姓名） */
    private static final Pattern CHINESE_NAME_PATTERN =
            Pattern.compile("^[\\u4e00-\\u9fa5]{1,}(·[\\u4e00-\\u9fa5]{1,})*$");

    /** 中国大陆邮政编码 */
    private static final Pattern POSTAL_CODE_PATTERN =
            Pattern.compile("^[1-9]\\d{5}$");

    /** 统一社会信用代码（18位） */
    private static final Pattern CREDIT_CODE_PATTERN =
            Pattern.compile("^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$");

    // ========================= 基础类型验证 =========================

    /**
     * 判断单个字符是否为中文
     *
     * @param c 待检测字符
     * @return 是否为中文字符
     */
    public static boolean isChinese(char c) {
        return CHINESE_PATTERN.matcher(String.valueOf(c)).matches();
    }

    /**
     * 判断字符串是否包含中文字符
     *
     * @param value 待检测字符串
     * @return 是否包含中文
     */
    public static boolean containsChinese(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return CHINESE_PATTERN.matcher(value).find();
    }

    /**
     * 判断字符串是否全部由中文组成
     *
     * @param value 待检测字符串
     * @return 是否全部为中文
     */
    public static boolean isAllChinese(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return ALL_CHINESE_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是数字（含整数和小数，支持负数）
     *
     * @param value 待检测字符串
     * @return 是否为数字
     */
    public static boolean isNumber(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return NUMBER_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是整数（支持负数）
     *
     * @param value 待检测字符串
     * @return 是否为整数
     */
    public static boolean isDigit(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return INTEGER_PATTERN.matcher(value).matches();
    }

    // ========================= 通信与网络验证 =========================

    /**
     * 是否是有效的中国大陆手机号
     *
     * @param value 待检测字符串
     * @return 是否为有效手机号
     */
    public static boolean isPhone(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是有效的邮箱地址
     *
     * @param value 待检测字符串
     * @return 是否为有效邮箱
     */
    public static boolean isEmail(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是有效的 URL（支持 http、https、ftp）
     *
     * @param value 待检测字符串
     * @return 是否为有效 URL
     */
    public static boolean isUrl(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是有效的 IPv4 地址
     *
     * @param value 待检测字符串
     * @return 是否为有效 IPv4
     */
    public static boolean isIpv4(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是有效的 IPv6 地址
     *
     * @param value 待检测字符串
     * @return 是否为有效 IPv6
     */
    public static boolean isIpv6(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return IPV6_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是有效的 IP 地址（IPv4 或 IPv6）
     *
     * @param value 待检测字符串
     * @return 是否为有效 IP
     */
    public static boolean isIp(String value) {
        return isIpv4(value) || isIpv6(value);
    }

    /**
     * 是否是有效的 MAC 地址
     *
     * @param value 待检测字符串（冒号或横线分隔，如 AA:BB:CC:DD:EE:FF）
     * @return 是否为有效 MAC 地址
     */
    public static boolean isMac(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return MAC_PATTERN.matcher(value).matches();
    }

    // ========================= 身份与证件验证 =========================

    /**
     * 是否是有效的18位身份证号码（含校验位验证）
     * <p>
     * 校验规则：
     * <ol>
     *     <li>长度必须为18位</li>
     *     <li>前17位必须为数字</li>
     *     <li>最后一位为校验码（0-9 或 X）</li>
     *     <li>加权求和取模验证校验码</li>
     * </ol>
     * </p>
     *
     * @param value 待检测字符串
     * @return 是否为有效身份证号
     */
    public static boolean isIdCardNumber(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        if (!ID_CARD_PATTERN.matcher(value).matches()) {
            return false;
        }
        // 加权因子
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        // 校验码映射
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (value.charAt(i) - '0') * weights[i];
        }
        char expectedCheck = checkCodes[sum % 11];
        char actualCheck = Character.toUpperCase(value.charAt(17));
        return expectedCheck == actualCheck;
    }

    /**
     * 是否是有效的中文姓名（2-6个汉字，支持少数民族的·号）
     *
     * @param value 待检测字符串
     * @return 是否为有效中文姓名
     */
    public static boolean isChineseName(String value) {
        if (value == null || value.length() < 2 || value.length() > 20) {
            return false;
        }
        return CHINESE_NAME_PATTERN.matcher(value).matches();
    }

    // ========================= 银行卡验证 =========================

    /**
     * 是否是有效的银行卡号（Luhn 算法校验）
     * <p>
     * 银行卡号通常为16-19位数字，使用 Luhn 算法进行校验。
     * </p>
     *
     * @param value 待检测字符串
     * @return 是否为有效银行卡号
     */
    public static boolean isBankCard(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        // 去除空格
        value = value.replaceAll("\\s+", "");
        // 银行卡号长度一般为16-19位
        if (value.length() < 16 || value.length() > 19) {
            return false;
        }
        // 必须全部为数字
        for (char c : value.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return luhnCheck(value);
    }

    /**
     * Luhn 算法校验
     *
     * @param number 数字字符串
     * @return 是否通过校验
     */
    private static boolean luhnCheck(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = number.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    // ========================= 车辆与编码验证 =========================

    /**
     * 是否是有效的中国大陆车牌号（含新能源车牌）
     *
     * @param value 待检测字符串
     * @return 是否为有效车牌号
     */
    public static boolean isLicensePlate(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return LICENSE_PLATE_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是有效的中国大陆邮政编码
     *
     * @param value 待检测字符串
     * @return 是否为有效邮政编码
     */
    public static boolean isPostalCode(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return POSTAL_CODE_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是有效的统一社会信用代码（18位）
     *
     * @param value 待检测字符串
     * @return 是否为有效统一社会信用代码
     */
    public static boolean isCreditCode(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return CREDIT_CODE_PATTERN.matcher(value).matches();
    }

    // ========================= 格式验证 =========================

    /**
     * 是否是有效的日期字符串
     * <p>
     * 支持格式：yyyy-MM-dd，并验证日期的合法性（如闰年2月29日）。
     * </p>
     *
     * @param value 待检测字符串
     * @return 是否为有效日期
     */
    public static boolean isDate(String value) {
        return isDate(value, "uuuu-MM-dd");
    }

    /**
     * 是否是有效的日期字符串（自定义格式）
     *
     * @param value   待检测字符串
     * @param pattern 日期格式，如 "uuuu-MM-dd"、"uuuu/MM/dd"
     * @return 是否为有效日期
     */
    public static boolean isDate(String value, String pattern) {
        if (value == null || value.isEmpty() || pattern == null) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
                    .withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(value, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 是否是有效的用户名（字母开头，允许字母数字下划线，4-20位）
     *
     * @param value 待检测字符串
     * @return 是否为有效用户名
     */
    public static boolean isUsername(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是强密码（至少8位，包含大小写字母、数字和特殊字符）
     *
     * @param value 待检测字符串
     * @return 是否为强密码
     */
    public static boolean isStrongPassword(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是有效的十六进制颜色值（如 #FFF、#FFFFFF、#FFFFFFFF）
     *
     * @param value 待检测字符串
     * @return 是否为有效十六进制颜色值
     */
    public static boolean isHexColor(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return HEX_COLOR_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是有效的 UUID
     *
     * @param value 待检测字符串
     * @return 是否为有效 UUID
     */
    public static boolean isUuid(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return UUID_PATTERN.matcher(value).matches();
    }

    // ========================= 字符串基础验证 =========================

    /**
     * 判断字符串是否为空（null 或空字符串）
     *
     * @param value 待检测字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * 判断字符串是否为空白（null、空字符串或仅包含空白字符）
     *
     * @param value 待检测字符串
     * @return 是否为空白
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 判断字符串长度是否在指定范围内
     *
     * @param value 待检测字符串
     * @param min   最小长度（含）
     * @param max   最大长度（含）
     * @return 长度是否在范围内
     */
    public static boolean isLengthBetween(String value, int min, int max) {
        if (value == null) {
            return false;
        }
        int length = value.length();
        return length >= min && length <= max;
    }

    /**
     * 判断字符串是否匹配指定的正则表达式
     *
     * @param value 待检测字符串
     * @param regex 正则表达式
     * @return 是否匹配
     */
    public static boolean matches(String value, String regex) {
        if (value == null || regex == null) {
            return false;
        }
        return Pattern.matches(regex, value);
    }
}
