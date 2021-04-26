package core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class MDate {
    /**
     * Dashboard's date value separator.
     */
    public static final String VAL_SEP = "/";
    /**
     * The format-pattern. This is in the full-state.
     */
    private static final String pattern = String.join(VAL_SEP, "dd", "MM", "yyyy HH:mm:ss");
    /**
     * The standard date format.
     * Changing this format has great consequences since some functions
     * of this class rely on an anticipated output format; and thus,
     * when changed, such functions must conform accordingly.
     */
    private static final SimpleDateFormat standardFormat = new SimpleDateFormat(pattern);


    /**
     * Gets a String representation of the given date
     * fully-formatted as specified by the standard format.
     * This includes both the date and time, as per the current form.
     */
    public static String format(Date d){
        return standardFormat.format(d);
    }

    /**
     * Returns only the date (and not the time) of the given date instance.
     */
    public static String formatDateOnly(Date d){
        return standardFormat.format(d).split(" ")[0];
    }

    /**
     * Returns the current date and time.
     * Convenient way of calling {@link #format(Date)} with a new Date.
     */
    public static String now(){
        return format(new Date());
    }

    /**
     * Returns the current date only, excluding the time.
     * Convenient way of calling {@link #formatDateOnly(Date)} with a new Date.
     */
    public static String today(){
        return formatDateOnly(new Date());
    }

    public static int currentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int currentMonth(){
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * Constructs a date object from the given date-string
     * which must be in accordance with the {@link #standardFormat}'s
     * implementation.
     */
    public static Date parse(String dateString){
        try {
            return standardFormat.parse(dateString);
        } catch (ParseException e) {
            App.silenceException(e);
            return null;
        }
    }

    /**
     * Gets the specified calendar property from the given date.
     * As for the property, please use the magic-constants defined
     * in the {@link Calendar} type.
     */
    public static int getPropertyFrom(Date date, int property){
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (property == Calendar.MONTH) {
            return c.get(Calendar.MONTH) + 1;
        }
        return c.get(property);
    }

    /**
     * Returns the corresponding month name of n.
     * Returns null if n is such that 1 < n, or n > 12.
     */
    public static String getMonthByName(int n){
        switch (n){
            case 1:{
                return "January";
            }
            case 2:{
                return "February";
            }
            case 3:{
                return "March";
            }
            case 4:{
                return "April";
            }
            case 5:{
                return "May";
            }
            case 6:{
                return "June";
            }
            case 7:{
                return "July";
            }
            case 8:{
                return "August";
            }
            case 9:{
                return "September";
            }
            case 10:{
                return "October";
            }
            case 11:{
                return "November";
            }
            case 12:{
                return "December";
            }
            default:{
                return null;
            }
        }
    }

    /**
     * Returns a Dashboard-formatted date by adding (or subtracting)
     * the given interval of days from the given date.
     */
    public static String daysAfter(Date date, int days) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return standardFormat.format(calendar.getTime());
    }

    /**
     * Returns true if the values presented by date1 and date2 fall in the same day.
     * This method is time-ignorant.
     * @see #formatDateOnly(Date)
     */
    public static boolean isSameDay(Date d1, Date d2) {
        return formatDateOnly(d1).equals(formatDateOnly(d2));
    }

    /**
     * Calculates and returns the number of days difference between
     * the given date instances; which are ordered.
     * That means, if the d2 is before d1, then a negative long value
     * will be returned.
     */
    public static long actualDayDifference(Date d1, Date d2){
        return ChronoUnit.DAYS.between(d1.toInstant(), d2.toInstant());
    }

    /**
     * Returns an integer representation of the given date's time.
     * This method uses only the time values; i.e the hour, minute and seconds.
     * This method is to be referred.
     * @see #getPropertyFrom(Date, int)
     */
    public static int getTimeValue(Date d){
        return  (getPropertyFrom(d, Calendar.HOUR) + 12) * Globals.HOUR +
                getPropertyFrom(d, Calendar.MINUTE) * Globals.MINUTE +
                getPropertyFrom(d, Calendar.SECOND) * Globals.SECOND;
    }

}
