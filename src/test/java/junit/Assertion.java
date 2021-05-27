package junit;

import core.serial.Serializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utg.Version;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

// https://www.tutorialspoint.com/junit/junit_using_assertion.htm
public class Assertion {

    @Test
    void checkRootDir(){
        final String root = String.join(File.separator, System.getProperty("user.home"),
                ".dashboard");
        assertEquals(root, Serializer.ROOT_DIR, () -> "Root directories do not match.");

        Assertions.assertEquals(Version.GREATER, new Version("2021.2").compare(
                new Version("2020.9")));
    }

}
