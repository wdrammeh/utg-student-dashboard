package main.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

import core.module.Course;
import core.module.ModuleHandler;
import core.module.RegisteredCourse;

public class ModuleTester {
    
    public static void main(String[] args) {
        final HashMap<String, Course> monitor = new LinkedHashMap<>();

        final Course c1 = new Course("2016/2017", "First Semester", "mthh gg002", "Precalculus",
            "lecturer", "campus", "room", "day", "time", 78, 3, "", true);

        final Course c2 = new Course("year", "semester", "code", "Precal", "lecturer", "campus", "room", "day", "time", 78, 3, "requirement", true);

        final Course c3 = new Course("year", "semester", "MTHh gg002", "Precal", "lecturer", "campus", "room", "day", "time", 78, 3, "requirement", false);

        monitor.put("a", c1);
        monitor.put("b", c2);
        System.out.println(monitor);

        System.out.println(monitor.containsKey("c"));
        System.out.println(monitor.containsValue(c3));

        System.out.println(c1.equals(c2));
        System.out.println(c3.equals(c1));
        System.out.println(c1.hashCode());
        System.out.println(c2.hashCode());
        System.out.println(c3.hashCode());
        System.out.println(c1 == c3);

        System.exit(0);
    }

    @Test
    private void hashCodeTest(){
        final Course c1 = new Course("year", "semester", "mth002", "name", "lecturer",
                "campus", "room", "day", "time", 100, 3, "requirement", false);

        final Course c2 = new Course("year", "semester", "mth002", "name", "lecturer",
                "campus", "room", "day", "time", 100, 3, "requirement", true);

        
    }

}
