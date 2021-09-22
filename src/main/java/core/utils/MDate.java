package core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * MDate is a remarkable utility class.
 * To be specific, it provides a flexible, yet maintainable, framework
 * for dealing with date and time.
 */
public class MDate {
    private static final DateFormat dayFormatter =
            DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.US);
    private static final DateFormat dayTimeFormatter =
            DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);


    public static String formatDayTime(Date date){
        return date == null ? null : dayTimeFormatter.format(date);
    }

    public static String formatDay(Date date){
        return date == null ? null : dayFormatter.format(date);
    }

    /**
     * Where full is to be understood as dayTime.
     */
    public static String formatNow(boolean full){
        final Date now = new Date();
        return full ? formatDayTime(now) : formatDay(now);
    }

    public static String formatNow(){
        return formatNow(true);
    }

    public static Date parseDayTime(String string){
        if (string == null) {
            return null;
        } else {
            try {
                return dayTimeFormatter.parse(string);
            } catch (ParseException e) {
                App.silenceException(e);
            }
        }
        return null;
    }

    public static Date parseDay(String string){
        if (string == null) {
            return null;
        } else {
            try {
                return dayFormatter.parse(string);
            } catch (ParseException e) {
                App.silenceException(e);
            }
        }
        return null;
    }

    /**
     * Note that deadlines are taken to be from the beginning of a day.
     * So, this is technically different from a saying "isDatePast".
     */
    public static boolean isDeadlinePast(Date deadline){
        final Date today = date(true);
        return isSameDay(today, deadline) || today.after(deadline);
    }

    public static Date date(int day, int month, int year, boolean dayBeginning){
        month--;
        final Calendar cal = Calendar.getInstance();
        if (dayBeginning) {
            cal.set(year, month, day, 0, 0, 0);
        } else {
            cal.set(year, month, day); // the time (i.e. h:m:s) will be this point
        }
        return cal.getTime();
    }

    public static Date date(boolean dayBeginning){
        final Calendar cal = Calendar.getInstance();
        return date(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR), dayBeginning);
    }

    public static Date date(int day, int month, int year, int hour, int minute, int second){
        month--;
        final Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute, second);
        return cal.getTime();
    }

    public static long toSerial(Date date) {
        return date == null ? -1 : date.getTime();
    }

    public static Date fromSerial(String string){
        if (string == null) {
            return null;
        } else {
            final long value;
            try {
                value = Long.parseLong(string);
                return value == -1 ? null : new Date(value);
            } catch (NumberFormatException e) {
                App.silenceException("Cannot reconstruct date. '"+string+"' is not a number.");
            }
        }
        return null;
    }

    public static int getYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth(){
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * Gets the specified calendar property from the given date.
     * As for the property, please use the magic-constants defined
     * in the {@link Calendar} type.
     */
    public static int getProperty(Date date, int property){
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (property == Calendar.MONTH) {
            return cal.get(Calendar.MONTH) + 1;
        }
        return cal.get(property);
    }

    /**
     * Returns a Dashboard-formatted date by adding
     * the given interval of days to the given date.
     */
    public static String daysAfter(Date date, int days) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return formatDayTime(cal.getTime());
    }

    /**
     * Returns true if the values presented by date1 and date2 fall in the same day.
     * The point of this method is to be time-ignorant.
     * @see #formatDay(Date)
     */
    public static boolean isSameDay(Date d1, Date d2) {
        return formatDay(d1).equals(formatDay(d2));
    }

    /**
     * Calculates and returns the number of days difference between
     * the given date instances; which are ordered.
     * That means, if the d1 is before d2, then a negative value should be expected.
     */
    public static long getDifference(Date d1, Date d2){
        return ChronoUnit.DAYS.between(d1.toInstant(), d2.toInstant());
    }

    /**
     * Returns an integer representation of the given date's time.
     * This method uses only the time values; i.e the hour, minute and seconds.
     * @see #getProperty(Date, int)
     */
    public static int getTimeValue(Date d){
        return  (getProperty(d, Calendar.HOUR) + 12) * Globals.HOUR +
                getProperty(d, Calendar.MINUTE) * Globals.MINUTE +
                getProperty(d, Calendar.SECOND) * Globals.SECOND;
    }

    /**
     * Returns the corresponding month name of n.
     * Returns null if n is such that 1 < n, or n > 12.
     */
    public static String getMonthName(int n){
        switch (n) {
            case 1 -> {
                return "January";
            }
            case 2 -> {
                return "February";
            }
            case 3 -> {
                return "March";
            }
            case 4 -> {
                return "April";
            }
            case 5 -> {
                return "May";
            }
            case 6 -> {
                return "June";
            }
            case 7 -> {
                return "July";
            }
            case 8 -> {
                return "August";
            }
            case 9 -> {
                return "September";
            }
            case 10 -> {
                return "October";
            }
            case 11 -> {
                return "November";
            }
            case 12 -> {
                return "December";
            }
            default -> {
                return null;
            }
        }
    }

}
