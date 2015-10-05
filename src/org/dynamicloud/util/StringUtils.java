package org.dynamicloud.util;

/**
 * Utilities to manipulate strings
 *
 * @author Eleazar Gomez
 * @version 1.0.0
 * @since 8/27/15
 **/
public class StringUtils {
    public static final String EMPTY = "";

    /**
     * Will return a capitalized string
     *
     * @param string string to capitalize
     *
     * @return capitalized string
     */
    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Will return a uncapitalized string
     *
     * @param string string to unCapitalize
     *
     * @return uncapitalized string
     */
    public static String unCapitalize(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }
}