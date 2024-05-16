package com.bitian.common.util;

import com.bitian.common.exception.CustomException;
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

    public static Double parseDouble(String str){
        if(StringUtils.isBlank(str)){
            throw new CustomException("数值转换异常");
        }
        if(NumberUtils.isCreatable(str)){
            return Double.parseDouble(str);
        }else if (str.endsWith("%")){
            str = str.replaceAll("%","");
            if(NumberUtils.isCreatable(str)){
                return Double.parseDouble(str)*0.01;
            }else {
                throw new CustomException("数值转换异常");
            }
        }else{
            throw new CustomException("数值转换异常");
        }
    }

    public static Float parseFloat(String str){
        Double value=parseDouble(str);
        return value==null?null:new BigDecimal(value).floatValue();
    }

    public static Long parseLong(String str){
        Double value=parseDouble(str);
        return value==null?null:new BigDecimal(value).longValue();
    }

    public static Integer parseInt(String str){
        Double value=parseDouble(str);
        return value==null?null:new BigDecimal(value).intValue();
    }

    public static Short parseShort(String str){
        Double value=parseDouble(str);
        return value==null?null:new BigDecimal(value).shortValue();
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
