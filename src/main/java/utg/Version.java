package utg;

public class Version {
    private final String literal;
    private final String type;
    public static final String SNAPSHOT = "SNAPSHOT";
    public static final String RELEASE = "RELEASE";
    public static final int LESS = -1;
    public static final int EQUAL = 0;
    public static final int GREATER = 1;


    public Version(String literal, String type) {
        this.literal = literal;
        this.type = type;
    }

    /**
     *
     * @param fullVersion a full version string of the format literal-type
     * @return a new Version object
     */
    public static Version construct(String fullVersion) {
        final String[] v = fullVersion.split("[-]");
        return new Version(v[0], v[1]);
    }

    public String getLiteral() {
        return literal;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.join("-", literal, type);
    }

    public int compare(Version other) {
        if (toString().equals(other.toString())) {
            return EQUAL;
        } else {
            final String[] parts = literal.split("[.]");
            final String[] otherParts = other.literal.split("[.]");
            for (int i = 0; i < parts.length; i++) {
                final int a = Integer.parseInt(parts[i]);
                final int b = Integer.parseInt(otherParts[i]);
                if (a > b) {
                    return GREATER;
                } else if (a < b) {
                    return LESS;
                }
            }
            return type.equals(SNAPSHOT) ? LESS : GREATER;
        }
    }

}
