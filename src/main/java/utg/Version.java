package utg;

import core.utils.App;
import core.utils.Globals;

import java.util.Date;

/**
 * Adheres to semantic versioning 2 convention
 * <p>
 * Given a version number MAJOR.MINOR.PATCH, increment the:
 * <p>
 * MAJOR version when you make incompatible API changes, MINOR version when you
 * add functionality in a backwards compatible manner, and PATCH version when
 * you make backwards compatible bug fixes.
 * <p>
 * Additional labels for pre-release and build metadata are available as
 * extensions to the MAJOR.MINOR.PATCH format as MAJOR.MINOR.PATCH-label.
 * <p>
 * See https://semver.org/
 */
public class Version {
    private final int major;
    private final int minor;
    private final int patch;
    private Date deprecateTime;
    /**
     * The maximum period, in days, a user is allowed
     * to use a deprecated version of Dashboard.
     */
    public static final int MAX_DEPRECATE_TIME = 14;
    public static final int LESS = -1;
    public static final int EQUAL = 0;
    public static final int GREATER = 1;


    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Literal is a string representation of a version such that the
     * version is period separated with a leading letter 'v'.
     * <p>
     * E.g: For a version 1.2.3, its literal will be v1.2.3
     *
     * @see #toLiteral()
     */
    public static Version parse(String literal) {
        try {
            final String[] a = literal.strip().replace("v", "").split("[.]");
            return new Version(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
        } catch (Exception e) {
            App.silenceInfo(String.format("Parse failed. Invalid version literal '%s'", literal));
            return null;
        }
    }

    public Date getDeprecateTime() {
        return deprecateTime;
    }

    public void setDeprecateTime(Date deprecateTime) {
        this.deprecateTime = deprecateTime;
    }

    public String toLiteral() {
        return "v" + Globals.join(".", new Object[]{major, minor, patch});
    }

    @Override
    public String toString() {
        return toLiteral();
    }

    public boolean equals(String literal) {
        return compare(parse(literal)) == EQUAL;
    }

    public int compare(Version v) {
        if (v == null) {
            return GREATER;
        }
        if (toString().equals(v.toString())) {
            return EQUAL;
        } else {
            if (major > v.major) {
                return GREATER;
            } else if (major < v.major) {
                return LESS;
            } else if (minor > v.minor) {
                return GREATER;
            } else if (minor < v.minor) {
                return LESS;
            } else {
                return patch > v.patch ? GREATER : LESS;
            }
        }
    }

}
