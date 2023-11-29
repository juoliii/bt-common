package com.bitian.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author admin
 */
public class NumberUtil {

    public static float twoDecimal(Number f,int scale){
        return roundToFloat(f,scale);
    }

    private static float roundToFloat(Number f,int scale){
        return new BigDecimal(defaultZeroIfNull(f).toString()).setScale(scale, RoundingMode.HALF_UP).floatValue();
    }

    public static Number defaultZeroIfNull(Number v){
        return v == null ? 0 : v;
    }

}
