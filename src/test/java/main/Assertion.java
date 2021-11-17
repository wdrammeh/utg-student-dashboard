package main;

import core.utils.Globals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utg.Dashboard;
import utg.Version;

// https://www.tutorialspoint.com/junit/junit_using_assertion.htm
public class Assertion {

    @Test
    void checkRootDir(){
        final String root = Globals.joinPaths(Globals.userHome(), ".dashboard");

        Assertions.assertEquals(root, Dashboard.getPath(), "Root directories do not match.");

        Assertions.assertEquals(Version.GREATER, Version.parse("2021.2").compare(new Version(2020, 9)));
    }

}
