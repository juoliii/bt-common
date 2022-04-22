package com.bitian.common.util;

import java.util.Arrays;
import java.util.List;

/**
 * 容器工具类
 * @author admin
 */
public class CollectionUtil {
    public static <T> T[] ListToArray(List<T> list){
        Object[] t=new Object[list.size()];
        return (T[])list.toArray(t);
    }

    public static <T> List<T>  ArrayToList(T ...t){
        return Arrays.asList(t);
    }
}
