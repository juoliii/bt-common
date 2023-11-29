package com.bitian.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author admin
 */
public class ValidateUtil {

    public static boolean isChinese(char c) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(c + "");
        if (m.find())
            return true;
        return false;
    }

    public static boolean isPhone(String value){
        if(null != value && value.length() == 11){
            String regex = "^1[0-9][0-9]\\d{8}$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(value);
            return m.matches();
        }
        return false;
    }

    public static boolean isUrl(String str) {
        if(null == str){
            return false;
        }
        return str.matches("[http|https]+[://]+[0-9A-Za-z:/[-]_#[?][=][.][&]]*");
    }

    public static boolean isEmail(String value){
        if(null != value){
            String regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(value);
            return m.matches();
        }
        return false;
    }

    public static boolean isIdCardNumber(String input) {
        // 身份证号正则表达式
        String regex = "\\d{17}[\\d|x|X]";
        return input.matches(regex);
    }

    /**
     * 是否是数字
     * @param value
     * @return
     */
    public static boolean isNumber(String value){
        if(null != value){
            Pattern pattern = Pattern.compile("^-?[0-9]+.?([0-9]+)?$");
            Matcher isNum = pattern.matcher(value);
            return isNum.matches();
        }
        return false;
    }

    /**
     * 是否是整数
     * @param value
     * @return
     */
    public static boolean isDigit(String value){
        if(null != value){
            Pattern pattern = Pattern.compile("^[0-9]+$");
            Matcher isNum = pattern.matcher(value);
            return isNum.matches();
        }
        return false;
    }
}
