package utg;

import core.Board;
import core.first.Welcome;
import core.other.Preview;
import core.serial.Serializer;
import core.user.Student;
import core.utils.App;
import core.utils.Globals;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;

/**
 * @author Muhammed W. Drammeh <md21712494@utg.edu.gm>
 *
 * This is the actual runner type.
 * In a nutshell, it reads from a serializable state if existed,
 * or triggers a new instance if not - or otherwise found inconsistent.
 * This class defines the normal process-flow of the Dashboard.
 * Please read the logic file.
 */
public class Dashboard {
    private static final Preview PREVIEW = new Preview(null);
    public static final Version VERSION = new Version("0.0.1", Version.SNAPSHOT);
    private static boolean isFirst;


    public static void main(String[] args) {
        if (isRunning()) {
            App.silenceException("Dashboard is already running.");
        } else {
            PREVIEW.setVisible(true);
            lockAccess();
            Runtime.getRuntime().addShutdownHook(new Thread(Dashboard::unlockAccess));
            final File rootDir = new File(Serializer.ROOT_DIR);
            if (rootDir.exists()) {
                final HashMap<String, String> configs = getLastConfigs();
                if (configs.isEmpty()) {
                    App.silenceException("Bad, or missing configuration files. Launching a new instance...");
                    freshStart();
                    return;
                }
                final Version recentVersion = Version.construct(configs.get("version"));
                final int compare = VERSION.compare(recentVersion);
                if (compare == Version.LESS) {
                    App.reportError("Version Error | Downgrade detected",
                            "You're trying launch Dashboard with an older version than your configuration files.\n" +
                                    "Please use Dashboard version '"+recentVersion.getLiteral()+"', or later.");
                } else if (compare == Version.GREATER) {
                    App.silenceInfo("A version upgrade detected.");
//                Todo some transition stuff here

                } else {
                    if (configs.get("userName").equals(Globals.userName())) {
                        rebuildNow(true);
                    } else {
                        verifyUser(true);
                    }
                }
            } else {
                freshStart();
            }
        }
    }

//    Todo implement this
    private static boolean isRunning(){
//        final File statusFile = new File(Serializer.inPath("status.ser"));
//        if (statusFile.exists()) {
//            String status = (String) Serializer.fromDisk(Serializer.inPath("status.ser"));
//            return status.equals("Running");
//        }
        return false;
    }

    //    Todo implement this
    public static void lockAccess(){
//        Serializer.toDisk("Running", Serializer.inPath("status.ser"));
    }

    //    Todo implement this
    public static void unlockAccess(){
//        Serializer.toDisk("Closed", Serializer.inPath("status.ser"));
    }

    /**
     * Serializes the configurations at this point.
     */
    public static void storeConfigs(){
        final String configs = Globals.joinLines(VERSION, Globals.userName());
        Serializer.toDisk(configs, Serializer.inPath("configs.ser"));
    }

    private static HashMap<String, String> getLastConfigs(){
        final HashMap<String, String> map = new HashMap<>();
        final Object configObj = Serializer.fromDisk(Serializer.inPath("configs.ser"));
        if (configObj != null) {
            final String[] lines = Globals.splitLines((String) configObj);
            map.put("version", lines[0]);
            map.put("userName", lines[1]);

        }
        return map;
    }

    /**
     * Triggers a new Dashboard.
     * This happens, of course, if no data are found to deserialize.
     * The user may have signed out, or has actually never launched Dashboard.
     * It sets the driver ahead of time, and brings a "Welcome Dialogue" to sight.
     */
    private static void freshStart(){
        isFirst = true;
        final Welcome welcome = new Welcome();
        PREVIEW.dispose();
        welcome.setVisible(true);
        SwingUtilities.invokeLater(()-> welcome.getScrollPane().toTop());
    }

    /**
     * This is security measure, invoked if the current user-name does not matches
     * the serialized user-name.
     * The user will be asked of the previous user's matriculation number.
     * And Dashboard will not build until such a mat. number is correct.
     */
    private static void verifyUser(boolean initialize){
        if (initialize) {
            try {
                Student.initialize();
                if (Student.isTrial()) {
                    rebuildNow(false);
                }
            } catch (Exception e) {
                App.silenceException("Failed to read user data. Launching a new instance...");
                freshStart();
                return;
            }
        }

        PREVIEW.setVisible(false);  // why?
        final String matNumber = requestInput();
        if (matNumber.equals(Student.getMatNumber())) {
            PREVIEW.setVisible(true);
            rebuildNow(false);
        } else {
            final String userName = Student.getFullNamePostOrder();
            App.reportError(PREVIEW, "Error",
                    "Incorrect Matriculation Number for "+userName+". Please try again.");
            verifyUser(false);
        }
    }

    private static String requestInput(){
        final String studentName = Student.getFullNamePostOrder();
        final String input = App.requestInput(PREVIEW, "UTG-Student Dashboard",
                "This Dashboard belongs to "+studentName+".\n" +
                "Please enter your Matriculation Number to confirm:");
        if (input == null) {
            System.exit(0);
        }
        return Globals.hasText(input) ? input : requestInput();
    }

    /**
     * Builds the Dashboard from a serializable state.
     * initialize determines whether the user's data is to be loaded,
     * if it was not already.
     */
    private static void rebuildNow(boolean initialize){
        if (initialize) {
            try {
                Student.initialize();
            } catch (NullPointerException e) {
                App.silenceException("Failed to read user data. Launching a new instance...");
                freshStart();
                return;
            }
        }

        SwingUtilities.invokeLater(()-> {
            final Board lastBoard = new Board();
            PREVIEW.dispose();
            lastBoard.setVisible(true);
        });
    }

    public static void setFirst(boolean first){
        isFirst = first;
    }

    public static boolean isFirst() {
        return isFirst;
    }

}
