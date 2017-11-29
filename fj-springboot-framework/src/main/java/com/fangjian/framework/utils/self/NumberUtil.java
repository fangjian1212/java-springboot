package com.fangjian.framework.utils.self;


public class NumberUtil {
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
    public static Boolean isBetweenEquals(final Long min , final Long mid  , final Long max) {
        if (max == null || max.longValue() == new Long(0).longValue()) {
            return isGreateThenEquals(mid , min);
        }
        return max.longValue() >= mid.longValue()
            && mid.longValue() >= min.longValue();
    }
    public static Boolean isGreateThenEquals(final Long max , final Long min) {
        return max.longValue() >= min.longValue();
    }
}
