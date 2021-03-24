package utg;

import core.*;

import javax.swing.*;
import java.io.File;

/**
 * @author Muhammed W. Drammeh <wakadrammeh@gmail.com>
 *
 * This is the actual runner type.
 * In a nutshell, it reads from a serializable state if existed,
 * or triggers a new instance if not - or otherwise found inconsistent.
 * This class defines the normal process-flow of the Dashboard.
 * Please read the logic.txt file.
 */
public class Dashboard {
    private static final Preview PREVIEW = new Preview(null);
    public static final String VERSION = "0.0.1-SNAPSHOT";
    private static boolean isFirst;


    public static void main(String[] args) {
        PREVIEW.setVisible(true);
        final Object recentUser = Serializer.fromDisk("user-name.ser");
        if (recentUser == null) {
            final File coreFile = new File(Serializer.SERIALS_DIR + File.separator + "core.ser");
            if (coreFile.exists()) {
                verifyUser(true);
            } else {
                freshStart();
            }
        } else {
            final String immediateUser = System.getProperty("user.name");
            if (immediateUser.equals(recentUser)) {
                rebuildNow(true);
            } else {
                verifyUser(true);
            }
        }
    }

    /**
     * Triggers a new Dashboard.
     * This happens, of course, if no data are found.
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
            } catch (NullPointerException e) {
                App.silenceException("Error reading user data.");
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
            App.reportError(PREVIEW, "Error", "Incorrect Matriculation Number for "+userName+". Please try again.");
            verifyUser(false);
        }
    }

    private static String requestInput(){
        final String userName = Student.getFullNamePostOrder();
        final String input = App.requestInput(PREVIEW, "UTG Student Dashboard",
                "This Dashboard belongs to "+userName+".\n" +
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
                App.silenceException("Error reading user data.");
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
