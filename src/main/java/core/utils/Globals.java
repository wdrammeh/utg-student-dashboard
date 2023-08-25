package core.utils;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringJoiner;

public class Globals {
    public static final String PROJECT_TITLE = "UTG Student Dashboard";
    public static final String PROJECT_NAME = PROJECT_TITLE.replace(" ", "-").toLowerCase();
    // 
    public static final int SECOND = 1_000;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;
    // 
    public static final String UNKNOWN = "Unknown";
    public static final String NONE = "None";
    public static final String NEVER = "Never";
    public static final String OTHER = "Other";


    /**
     * Checks plurality of the given count,
     * and assigns a compound string with text based on the count.
     * This is only compatible with Regular Nouns, and the given text
     * must be in the plural format already => must be ending in letter "s".
     */
    public static String checkPlurality(int count, String text) {
        if (count == 0) {
            return "No "+text;
        } else if (count == 1) {
            return "1 " + text.substring(0, text.length() - 1);
        } else {
            return count+" "+text;
        }
    }

    public static double round(double d, int places) {
        BigDecimal bd = BigDecimal.valueOf(d);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String joinPaths(String... paths) {
        return String.join(File.separator, paths);
    }
    
    public static String join(String sep, String nullVal, Object[] objs) {
        final StringJoiner joiner = new StringJoiner(sep);
        for (Object obj : objs) {
            joiner.add(obj == null ? nullVal : String.valueOf(obj));
        }
        return joiner.toString();
    }

    public static String join(String sep, Object[] objs) {
        return join(sep, null, objs);
    }

    public static String joinLines(String nullVal, Object[] objs) {
        return join(App.LINE_SEPARATOR, nullVal, objs);
    }

    public static String joinLines(Object[] objs) {
        return joinLines("null", objs);
    }

    public static String[] split(String regex, String text) {
        if (text == null) {
            return null;
        }
        return text.split(regex);
    }

    public static String[] splitLines(String text) {
        return split(App.LINE_SEPARATOR, text);
    }

    public static String userName(){
        return System.getProperty("user.name");
    }

    public static String userHome(){
        return System.getProperty("user.home");
    }

    /**
     * Returns true if the given string has text.
     * This, instead of throwing a {@link NullPointerException},
     * will simply return false, if t null.
     * This is direct a negation of {@link #hasNoText(String)}.
     * @see #hasNoText(String)
     */
    public static boolean hasText(String t){
        return !hasNoText(t);
    }

    /**
     * Returns true if the given string is null, or blank.
     */
    public static boolean hasNoText(String t){
        return t == null || t.isBlank();
    }

    public static String reference(String... parts) {
        return "'"+String.join(" > ", parts)+"'";
    }

}
