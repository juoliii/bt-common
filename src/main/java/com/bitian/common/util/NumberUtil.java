package com.bitian.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public double percentToDouble(String str,String ...ignores){
        try{
            if(StringUtils.isBlank(str)){
                return 0d;
            }
            for (String ignore : ignores) {
                if(StringUtils.contains(str,ignore)){
                    return 0d;
                }
            }
            if (str.contains("%")){
                str = str.replaceAll("%","");
                double value= Double.parseDouble(str);
                return value*0.01;
            }else if(NumberUtils.isCreatable(str)){
                return Double.parseDouble(str);
            } else{
                return Double.parseDouble(str);
            }
        }catch (Exception e){
            return 0d;
        }
    }

    public static boolean isNumber(String value,String ...ignores){
        if(StringUtils.isBlank(value)){
            return false;
        }
        for (String ignore : ignores) {
            if(value.contains(ignore)){
                return true;
            }
        }
        if(value.trim().endsWith("%")){
            String tmp=StringUtils.replace(value,"%","");
            return NumberUtils.isCreatable(tmp);
        }else{
            return NumberUtils.isCreatable(value);
        }
    }

}
