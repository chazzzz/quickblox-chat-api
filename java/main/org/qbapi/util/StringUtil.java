package org.qbapi.util;

/**
 * Created by chazz on 6/11/2015.
 */
public class StringUtil {

	public static boolean isEmpty(String obj) {
		return obj == null || obj.equalsIgnoreCase("");
	}
}
