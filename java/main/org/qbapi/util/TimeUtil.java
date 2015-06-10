package org.qbapi.util;

import java.util.Date;

/**
 * Created by chazz on 6/9/2015.
 */
public class TimeUtil {

    public static long getUnixTime() {
        return new Date().getTime() /1000;
    }

}
