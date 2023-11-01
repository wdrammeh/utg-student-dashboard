package utg;

public class Transition {

    public static void transit(Version from, Version to) {
        if (from.compare(to) == Version.GREATER) {
            // 1. For versions <= 1.1.2 [v1.1.1, v1.1.2], default path := ".dashboard"
            final int pathComp = from.compare(Version.parse("v1.1.2"));
            if (pathComp == Version.EQUAL || pathComp == Version.LESS) {

            }
        }
    }

}
