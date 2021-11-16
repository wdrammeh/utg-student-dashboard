package utg;

import core.utils.App;

import java.util.Date;

public class Version {
    private int year;
    private int month;
    /**
     * The depreciation date of this version.
     * deprecateTime and upcoming goes hand-in-hand -
     * either both are available or none is.
     */
    private Date deprecateTime;
    /**
     * The maximum period, in days, a user is allowed
     * to use a deprecated version of Dashboard.
     */
    public static final int MAX_DEPRECATE_TIME = 14;
    public static final int LESS = -1;
    public static final int EQUAL = 0;
    public static final int GREATER = 1;


    public Version(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public static Version parse(String literal){
        try {
            final String[] a = literal.strip().split("[.]");
            return new Version(Integer.parseInt(a[0]), Integer.parseInt(a[1]));
        } catch (Exception e) {
            App.silenceInfo("Cannot parse version literal '%s'".formatted(literal));
            return null;
        }
    }

    public Date getDeprecateTime() {
        return deprecateTime;
    }

    public void setDeprecateTime(Date deprecateTime) {
        this.deprecateTime = deprecateTime;
    }

    @Override
    public String toString() {
        return year+"."+month;
    }

    public int compare(Version v) {
        if (v == null) {
            return GREATER;
        }

        if (toString().equals(v.toString())) {
            return EQUAL;
        } else {
            if (year > v.year) {
                return GREATER;
            } else if (year < v.year) {
                return LESS;
            } else { // year == v.year
                return month > v.month ? GREATER : LESS;
            }
        }
    }

}
