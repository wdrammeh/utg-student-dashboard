package utg;

import java.util.Date;

public class Version {
    /**
     * The literal of a version is what defines the version
     * relative to the time (academic-semester) of release as `year.month`
     * 
     * Todo: replace field with year (int) and month (int), redefining usage, comparison of this type
     */
    private final String literal;
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


    public Version(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    public Date getDeprecateTime() {
        return deprecateTime;
    }

    public void setDeprecateTime(Date deprecateTime) {
        this.deprecateTime = deprecateTime;
    }

    @Override
    public String toString() {
        return literal;
    }

    public int compare(Version other) {
        if (toString().equals(other.toString())) {
            return EQUAL;
        } else {
            final String[] parts = literal.split("[.]");
            final String[] otherParts = other.literal.split("[.]");
            final int a1 = Integer.parseInt(parts[0]);
            final int a2 = Integer.parseInt(parts[1]);
            final int b1 = Integer.parseInt(otherParts[0]);
            final int b2 = Integer.parseInt(otherParts[1]);
            if (a1 > b1) {
                return GREATER;
            } else if (b1 > a1) {
                return LESS;
            } else { // a1 == b1
                return a2 > b2 ? GREATER : LESS;
            }
        }
    }

}
