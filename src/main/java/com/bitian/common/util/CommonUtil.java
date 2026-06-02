package com.bitian.common.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * 通用工具类
 * <p>
 * 提供 Web 请求处理、字符串处理、数据脱敏、URL 解析、环境检测等常用方法。
 * 所有方法均为 null 安全设计，不会抛出 NullPointerException。
 * </p>
 *
 * @author admin
 */
public class CommonUtil {

    private static final Logger LOGGER = Logger.getLogger(CommonUtil.class.getName());

    /** 用于检测 unknown IP 占位符的常量 */
    private static final String UNKNOWN = "unknown";

    /** 用于 stripHTML 的预编译正则 */
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("</?[^>]+>");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private CommonUtil() {
        // 工具类禁止实例化
    }

    // ========================= Web 请求相关 =========================

    /**
     * 判断请求是否为 AJAX 请求
     *
     * @param request HTTP 请求对象
     * @return 如果是 AJAX 请求返回 true
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * 判断请求是否为 JSON 内容类型
     *
     * @param request HTTP 请求对象
     * @return 如果是 JSON 请求返回 true
     */
    public static boolean isJsonRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }

    /**
     * 判断请求是否为 multipart（文件上传）请求
     *
     * @param request HTTP 请求对象
     * @return 如果是 multipart 请求返回 true
     */
    public static boolean isMultipartRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("multipart/");
    }

    /**
     * 获取客户端真实 IP 地址
     * <p>
     * 按优先级依次检查以下请求头：X-Forwarded-For、Proxy-Client-IP、
     * WL-Proxy-Client-IP、HTTP_CLIENT_IP、HTTP_X_FORWARDED_FOR，
     * 最后使用 {@code request.getRemoteAddr()} 作为兜底。
     * 对于 X-Forwarded-For 包含多个 IP 的情况，取第一个非 unknown 的 IP。
     * </p>
     *
     * @param request HTTP 请求对象
     * @return 客户端真实 IP 地址
     */
    public static String getRequestRemoteIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR",
                "X-Real-IP"
        };

        String ip = null;
        for (String header : headers) {
            ip = request.getHeader(header);
            if (isValidIp(ip)) {
                break;
            }
        }

        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        // X-Forwarded-For 可能包含多个 IP，取第一个非 unknown 的
        if (ip != null && ip.contains(",")) {
            for (String segment : ip.split(",")) {
                String trimmed = segment.trim();
                if (!UNKNOWN.equalsIgnoreCase(trimmed)) {
                    return trimmed;
                }
            }
        }
        return ip == null ? "" : ip.trim();
    }

    /**
     * 获取请求的完整基础路径（scheme://host:port/contextPath）
     * <p>
     * 优先使用 X-Forwarded-Scheme 头来确定协议，支持反向代理场景。
     * 当端口为 80（HTTP）或 443（HTTPS）时自动省略端口号。
     * </p>
     *
     * @param request HTTP 请求对象
     * @return 基础路径，不以 / 结尾
     */
    public static String getBasePathFromRequest(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String scheme = request.getHeader("X-Forwarded-Scheme");
        if (StringUtils.isEmpty(scheme)) {
            scheme = request.getScheme();
        }
        int port = request.getServerPort();
        boolean omitPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                || ("https".equalsIgnoreCase(scheme) && port == 443);

        String basePath = scheme + "://" + request.getServerName()
                + (omitPort ? "" : ":" + port)
                + request.getContextPath();
        return basePath.replaceAll("/+$", "");
    }

    /**
     * 读取请求体内容（适用于 POST 等有 body 的请求）
     *
     * @param request HTTP 请求对象
     * @return 请求体内容字符串，读取失败返回空字符串
     */
    public static String readContentFromRequest(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        try {
            BufferedReader br = request.getReader();
            StringBuilder buffer = new StringBuilder(256);
            String str;
            while ((str = br.readLine()) != null) {
                buffer.append(str);
            }
            return buffer.toString();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "读取请求体内容失败", e);
        }
        return "";
    }

    /**
     * 获取请求的所有参数并以 Map 形式返回
     *
     * @param request HTTP 请求对象
     * @return 参数键值对 Map（值为数组中的第一个值），不会返回 null
     */
    public static Map<String, String> getRequestParams(HttpServletRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> result = new LinkedHashMap<>(parameterMap.size());
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            result.put(entry.getKey(), (values != null && values.length > 0) ? values[0] : "");
        }
        return result;
    }

    /**
     * 获取请求的 User-Agent 字符串
     *
     * @param request HTTP 请求对象
     * @return User-Agent 字符串，不存在返回空字符串
     */
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "";
    }

    // ========================= URL 解析 =========================

    /**
     * 从 URL 中提取指定参数的值
     * <p>
     * 如果 url 不以 http 开头，则认为整个字符串就是参数值，直接返回。
     * </p>
     *
     * @param url URL 字符串
     * @param id  参数名
     * @return 参数值，未找到返回空字符串
     */
    public static String getParamFromUrl(String url, String id) {
        if (url == null || id == null) {
            return "";
        }
        if (!url.startsWith("http")) {
            return url;
        }
        int queryIdx = url.indexOf('?');
        if (queryIdx < 0 || queryIdx == url.length() - 1) {
            return "";
        }
        String query = url.substring(queryIdx + 1);
        for (String pair : query.split("&")) {
            int eqIdx = pair.indexOf('=');
            if (eqIdx > 0) {
                String key = pair.substring(0, eqIdx);
                if (id.equals(key)) {
                    return pair.substring(eqIdx + 1);
                }
            }
        }
        return "";
    }

    /**
     * 解析 URL 查询字符串为 Map
     * <p>
     * 提取 URL 中 ? 后面的所有键值对。
     * </p>
     *
     * @param url URL 字符串
     * @return 查询参数 Map，不会返回 null
     */
    public static Map<String, String> parseUrlParams(String url) {
        if (url == null || url.isEmpty()) {
            return Collections.emptyMap();
        }
        int queryIdx = url.indexOf('?');
        if (queryIdx < 0 || queryIdx == url.length() - 1) {
            return Collections.emptyMap();
        }
        String query = url.substring(queryIdx + 1);
        // 去除片段标识符
        int hashIdx = query.indexOf('#');
        if (hashIdx >= 0) {
            query = query.substring(0, hashIdx);
        }
        Map<String, String> params = new LinkedHashMap<>();
        for (String pair : query.split("&")) {
            int eqIdx = pair.indexOf('=');
            if (eqIdx > 0) {
                params.put(pair.substring(0, eqIdx), pair.substring(eqIdx + 1));
            } else if (!pair.isEmpty()) {
                params.put(pair, "");
            }
        }
        return params;
    }

    /**
     * URL 编码
     *
     * @param value 待编码字符串
     * @return 编码后的字符串，输入为 null 时返回空字符串
     */
    public static String urlEncode(String value) {
        if (value == null) {
            return "";
        }
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // UTF-8 always supported, this should never happen
            return value;
        }
    }

    /**
     * URL 解码
     *
     * @param value 待解码字符串
     * @return 解码后的字符串，输入为 null 时返回空字符串
     */
    public static String urlDecode(String value) {
        if (value == null) {
            return "";
        }
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // UTF-8 always supported, this should never happen
            return value;
        }
    }

    // ========================= HTML 处理 =========================

    /**
     * 剔除字符串中的 HTML 标签
     *
     * @param strHtml 包含 HTML 的字符串
     * @return 去除 HTML 标签后的纯文本，输入为 null 返回空字符串
     */
    public static String stripHTML(String strHtml) {
        if (strHtml == null || strHtml.isEmpty()) {
            return "";
        }
        // 移除 HTML 标签
        String text = HTML_TAG_PATTERN.matcher(strHtml).replaceAll("");
        // 压缩连续空白为单个空格
        text = WHITESPACE_PATTERN.matcher(text).replaceAll(" ");
        return text.trim();
    }

    /**
     * 转义 HTML 特殊字符，防止 XSS 攻击
     *
     * @param value 待转义字符串
     * @return 转义后的安全字符串
     */
    public static String escapeHtml(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    // ========================= 字符串处理 =========================

    /**
     * 截取字符串，超出部分以省略号替代
     *
     * @param value     原始字符串
     * @param maxLength 最大保留长度（不含省略号）
     * @return 截取后的字符串
     */
    public static String truncate(String value, int maxLength) {
        return truncate(value, maxLength, "...");
    }

    /**
     * 截取字符串，超出部分以指定后缀替代
     *
     * @param value     原始字符串
     * @param maxLength 最大保留长度（不含后缀）
     * @param suffix    省略后缀
     * @return 截取后的字符串
     */
    public static String truncate(String value, int maxLength, String suffix) {
        if (value == null) {
            return "";
        }
        if (maxLength < 0) {
            maxLength = 0;
        }
        if (value.length() <= maxLength) {
            return value;
        }
        String safeSuffix = (suffix != null) ? suffix : "";
        return value.substring(0, maxLength) + safeSuffix;
    }

    /**
     * 将驼峰命名转换为下划线命名
     * <p>
     * 例：{@code camelToUnderscore("helloWorld")} → {@code "hello_world"}
     * </p>
     *
     * @param value 驼峰命名字符串
     * @return 下划线命名字符串
     */
    public static String camelToUnderscore(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length() + 8);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 将下划线命名转换为驼峰命名
     * <p>
     * 例：{@code underscoreToCamel("hello_world")} → {@code "helloWorld"}
     * </p>
     *
     * @param value 下划线命名字符串
     * @return 驼峰命名字符串
     */
    public static String underscoreToCamel(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        boolean nextUpper = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else {
                sb.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return sb.toString();
    }

    /**
     * 首字母大写
     *
     * @param value 原始字符串
     * @return 首字母大写后的字符串
     */
    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        if (value.length() == 1) {
            return value.toUpperCase();
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param value 原始字符串
     * @return 首字母小写后的字符串
     */
    public static String uncapitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        if (value.length() == 1) {
            return value.toLowerCase();
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * 重复字符串指定次数
     *
     * @param value 原始字符串
     * @param times 重复次数
     * @return 重复拼接后的字符串
     */
    public static String repeat(String value, int times) {
        if (value == null || value.isEmpty() || times <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(value);
        }
        return sb.toString();
    }

    // ========================= 数据脱敏 =========================

    /**
     * 手机号脱敏：保留前3位和后4位，中间以 **** 替代
     * <p>
     * 例：{@code maskPhone("13812345678")} → {@code "138****5678"}
     * </p>
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone == null ? "" : phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 邮箱脱敏：保留首字符和 @ 后面的域名，中间以 **** 替代
     * <p>
     * 例：{@code maskEmail("test@example.com")} → {@code "t****@example.com"}
     * </p>
     *
     * @param email 邮箱地址
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }
        int atIdx = email.indexOf('@');
        if (atIdx <= 0) {
            return email;
        }
        return email.charAt(0) + "****" + email.substring(atIdx);
    }

    /**
     * 身份证号脱敏：保留前6位和后4位，中间以 * 替代
     * <p>
     * 例：{@code maskIdCard("110101199001011234")} → {@code "110101********1234"}
     * </p>
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 10) {
            return idCard == null ? "" : idCard;
        }
        int prefixLen = 6;
        int suffixLen = 4;
        int maskLen = idCard.length() - prefixLen - suffixLen;
        return idCard.substring(0, prefixLen) + repeat("*", maskLen)
                + idCard.substring(idCard.length() - suffixLen);
    }

    /**
     * 银行卡号脱敏：保留前4位和后4位，中间以 * 替代
     * <p>
     * 例：{@code maskBankCard("6222021234567890123")} → {@code "6222***********0123"}
     * </p>
     *
     * @param bankCard 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard == null ? "" : bankCard;
        }
        int prefixLen = 4;
        int suffixLen = 4;
        int maskLen = bankCard.length() - prefixLen - suffixLen;
        return bankCard.substring(0, prefixLen) + repeat("*", maskLen)
                + bankCard.substring(bankCard.length() - suffixLen);
    }

    /**
     * 通用脱敏：保留指定前缀和后缀长度，中间以 * 替代
     *
     * @param value     原始字符串
     * @param prefixLen 前缀保留长度
     * @param suffixLen 后缀保留长度
     * @return 脱敏后的字符串
     */
    public static String mask(String value, int prefixLen, int suffixLen) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        int len = value.length();
        if (prefixLen + suffixLen >= len) {
            return value;
        }
        int maskLen = len - prefixLen - suffixLen;
        return value.substring(0, prefixLen) + repeat("*", maskLen)
                + value.substring(len - suffixLen);
    }

    // ========================= 默认值 =========================

    /**
     * 如果值为 null，则返回默认值
     *
     * @param value        原始值
     * @param defaultValue 默认值
     * @param <T>          值类型
     * @return 非 null 的值
     */
    public static <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果字符串为空（null 或空字符串），则返回默认值
     *
     * @param value        原始字符串
     * @param defaultValue 默认值
     * @return 非空字符串
     */
    public static String defaultIfEmpty(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    /**
     * 如果字符串为空白（null、空字符串或仅空白字符），则返回默认值
     *
     * @param value        原始字符串
     * @param defaultValue 默认值
     * @return 非空白字符串
     */
    public static String defaultIfBlank(String value, String defaultValue) {
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }

    // ========================= 环境与运行时 =========================

    /**
     * 判断当前操作系统是否为 Windows
     *
     * @return 是否为 Windows 系统
     */
    public static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    /**
     * 判断当前操作系统是否为 Linux
     *
     * @return 是否为 Linux 系统
     */
    public static boolean isLinux() {
        return System.getProperty("os.name", "").toLowerCase().contains("linux");
    }

    /**
     * 判断当前操作系统是否为 macOS
     *
     * @return 是否为 macOS 系统
     */
    public static boolean isMac() {
        return System.getProperty("os.name", "").toLowerCase().contains("mac");
    }

    /**
     * 获取系统环境变量，不存在时返回默认值
     *
     * @param name         环境变量名
     * @param defaultValue 默认值
     * @return 环境变量值
     */
    public static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取系统属性，不存在时返回默认值
     *
     * @param key          属性名
     * @param defaultValue 默认值
     * @return 系统属性值
     */
    public static String getSystemProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    // ========================= 杂项工具 =========================

    /**
     * 安全地将对象转为字符串
     *
     * @param obj 对象
     * @return 字符串表示，null 返回空字符串
     */
    public static String toStr(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    /**
     * 安全地将对象转为字符串，null 时返回指定默认值
     *
     * @param obj          对象
     * @param defaultValue 默认值
     * @return 字符串表示
     */
    public static String toStr(Object obj, String defaultValue) {
        return obj != null ? obj.toString() : defaultValue;
    }

    /**
     * 安全地将字符串解析为 int，解析失败返回默认值
     *
     * @param value        字符串
     * @param defaultValue 默认值
     * @return 解析后的整数值
     */
    public static int toInt(String value, int defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 安全地将字符串解析为 long，解析失败返回默认值
     *
     * @param value        字符串
     * @param defaultValue 默认值
     * @return 解析后的长整型值
     */
    public static long toLong(String value, long defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 安全地将字符串解析为 boolean
     * <p>
     * "true"、"1"、"yes"、"on" 返回 true（不区分大小写），其余返回 false。
     * </p>
     *
     * @param value 字符串
     * @return 布尔值
     */
    public static boolean toBoolean(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        String v = value.trim().toLowerCase();
        return "true".equals(v) || "1".equals(v) || "yes".equals(v) || "on".equals(v);
    }

    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 当前时间戳
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 简单计算字符串的哈希值（使用 Java 默认 hashCode）
     * <p>
     * 返回绝对值的十六进制字符串表示。
     * </p>
     *
     * @param value 原始字符串
     * @return 十六进制哈希字符串
     */
    public static String simpleHash(String value) {
        if (value == null) {
            return "0";
        }
        return Integer.toHexString(Math.abs(value.hashCode()));
    }

    // ========================= 私有辅助方法 =========================

    /**
     * 判断 IP 是否有效（非空、非空串、非 unknown）
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
