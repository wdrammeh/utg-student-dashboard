package core;

public class Globals {
    public static final int SECOND = 1_000;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;
    public static final String NONE = "None";
    public static final String UNKNOWN = "Unknown";


    /**
     * Checks plurality of the given count, and assigns a compound string with text
     * based on the count.
     * This is only compatible with regular nouns.
     * And the text must be in plural format already.
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

    /**
     * Returns true if the given string has text.
     * This, instead of throwing a {@link NullPointerException},
     * will simply return false, if t null.
     * This is a negation of hasNoText(String).
     * @see #hasNoText(String)
     */
    public static boolean hasText(String t){
        return !hasNoText(t);
    }

    /**
     * Returns true if the given string is null, or otherwise blank.
     * This, instead of throwing a {@link NullPointerException},
     * will simply return true, if t null.
     */
    public static boolean hasNoText(String t){
        if (t == null) {
            return true;
        }
        for (char c : t.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

}
