package org.bibsonomy.plugin.jabref.util;

import java.nio.charset.StandardCharsets;

/**
 * @author Waldemar Biller <wbi@cs.uni-kassel.de>
 */
public class StringUtil {

    /**
     * Encodes a string to UTF8
     *
     * @param s the string which should be encoded
     * @return the encoded string or null
     */
    static String toUTF8(String s) {
        if (s != null) {
            // FIXME: what is this? why do we want to introduce platform dependency here?
            // This should only be correct if an error from somewhere else has to be corrected.
            return new String(s.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s) || "".equals(s.trim());
    }
}
