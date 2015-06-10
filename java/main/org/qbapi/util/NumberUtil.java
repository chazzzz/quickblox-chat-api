package org.qbapi.util;

/**
 * Created by chazz on 6/9/2015.
 */
public class NumberUtil {

    public static double randomNonce() {
        return Math.floor(Math.random() * 10000);
    }

}
