package utg;

import core.Board;
import core.alert.Notification;
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
    public static final Version VERSION = new Version("0.1.1", Version.RELEASE);
    private static final String RUNNING = "Running";
    private static final String CLOSED = "Closed";
    public static final Thread UNLOCK_HOOK = new Thread(()-> setStatus(CLOSED));
    private static boolean isFirst;


    public static void main(String[] args) {
        if (isRunning()) {
            App.silenceWarning("Dashboard is already running.");
        }
        PREVIEW.setVisible(true);
        final File rootDir = new File(Serializer.ROOT_DIR);
        if (rootDir.exists()) {
            setStatus(RUNNING);
            final File configFile = new File(Serializer.inPath("configs.ser"));
            if (configFile.exists()) {
                final HashMap<String, String> configs = getLastConfigs();
                if (configs.isEmpty()) {
                    App.silenceException("Bad configuration files. Launching a new instance...");
                    freshStart();
                } else {
                    final Version recentVersion = Version.construct(configs.get("version"));
                    final int comparison = VERSION.compare(recentVersion);
                    if (comparison == Version.LESS) {
                        PREVIEW.dispose();
                        App.reportError(null, "Version Error | Downgrade Detected",
                                "You're trying to launch Dashboard with an older version than your configuration files.\n" +
                                        "Please use Dashboard version '"+recentVersion.getLiteral()+"', or later.");
                        System.exit(0);
                    } else if (comparison == Version.GREATER) {
                        App.silenceInfo("A version upgrade detected.");
//                    Todo implement version upgrade stuff
                        Board.POST_PROCESSES.add(()-> {
                            Notification.create("New Update", "Dashboard has been updated.",
                                    "<p>A version upgrade was detected: from <b>"+recentVersion+"</b> to <b>"+VERSION+"</b>.</p>");
//                    Todo: Add What's new notice, or point to an external source containing such notice.
                        });
                    }
                    if (configs.get("userName").equals(Globals.userName())) {
                        rebuildNow(true);
                    } else {
                        verifyUser(true);
                    }
                }
            } else {
                App.silenceException("Missing configuration files. Launching a new instance...");
                freshStart();
            }
        } else {
            setStatus(RUNNING);
            freshStart();
        }
    }

    private static boolean isRunning(){
        final File statusFile = new File(Serializer.inPath("status.ser"));
        return statusFile.exists() &&
                RUNNING.equals(Serializer.fromDisk(statusFile.getAbsolutePath()));
    }

    private static void setStatus(String status){
        Serializer.toDisk(status, Serializer.inPath("status.ser"));
        if (RUNNING.equals(status)) {
            Runtime.getRuntime().addShutdownHook(UNLOCK_HOOK);
        }
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
     * The user might have signed out, or has actually never launched Dashboard.
     */
    private static void freshStart(){
        isFirst = true;
        final Welcome welcome = new Welcome();
        PREVIEW.dispose();
        welcome.setVisible(true);
        SwingUtilities.invokeLater(()-> welcome.getScrollPane().toTop());
    }

    /**
     * This is a security measure invoked if the current userName does not matches
     * the serialized userName.
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

        PREVIEW.setVisible(false);
        final String matNumber = requestInput();
        if (matNumber.equals(Student.getMatNumber())) {
            PREVIEW.setVisible(true);
            rebuildNow(false);
        } else {
            final String userName = Student.getFullNamePostOrder();
            App.reportError(PREVIEW, "Error",
                    "Incorrect Matriculation Number for '"+userName+"'. Try again.");
            verifyUser(false);
        }
    }

    private static String requestInput(){
        final String studentName = Student.getFullNamePostOrder();
        final String input = App.requestInput(null, "Dashboard",
                "This Dashboard belongs to '"+studentName+"'.\n" +
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

    public static boolean isRelease(){
        return VERSION.getType().equals(Version.RELEASE);
    }

}
