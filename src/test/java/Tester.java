import core.ModuleHandler;
import core.Student;

import java.util.Date;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * This type is intended for testing a specific component / functionality of the project.
 * This class becomes useful when testing: for instance, how accurate are some computations?
 * Or how exactly are some components rendered before loading them up with the entire project.
 * E.g., to see how the Preview window looks like, use something like:
 *  SwingUtilities.invokeLater(()-> new Preview(null).setVisible(true));
 *
 * In whatever case, developer is assumed to be working on that particular side of the project.
 */
public class Tester {

    public static void main(String[] args) {
        System.out.println(new Date());

    }

    public static void loadContent(){
        Student.initialize();
        new ModuleHandler();
    }

}
