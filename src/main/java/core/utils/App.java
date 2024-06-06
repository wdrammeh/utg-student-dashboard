package core.utils;

import core.Board;
import core.setting.Settings;
import core.user.Student;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
/**
 * It is the center for generalized concepts.
 * Any attempt to point to an icon, a sheet, etc., must directly get this class in use.
 * As analogous to the Globals type which globalizes code operations, this class universalizes the
 * input-output operations.
 */
public class App {
    public static final int DIALOG_DISMISSED = 0;
    public static final int INPUT_BLANK = 1;
    public static final int VERIFICATION_FALSE = 2;
    public static final int VERIFICATION_TRUE = 3;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");


    /**
     * This method is stagnant. And so for uniformity only.
     * The given iconName must be a pre-existing name in the icons
     * resource dir. - the URL-pointer to which is to be returned.
     */
    public static URL getIconURL(String iconName){
        return App.class.getResource("/icons/"+iconName);
    }

    /**
     * A centralized point for user verification.
     * Note that some operations, however, do no necessarily delegate to this.
     * This function gives access if the current user is trial,
     * or {@link Settings#isVerifyNeeded} is true.
     * Operations like logging out must always ask for user-confirmation.
     * The given text will be shown to the user as a hint.
     * And the operation will be performed on the parent component.
     */
    public static int verifyUser(Component parent, String text) {
        if (!Settings.isVerifyNeeded() || Student.isGuest()) {
            return VERIFICATION_TRUE;
        } else {
            final String input = requestInput(parent, "Confirm", text);
            if (input == null) {
                return DIALOG_DISMISSED;
            } else if (Globals.hasNoText(input)) { // in which case, can only be blank.
                return INPUT_BLANK;
            } else if (input.equals(Student.getMatNumber())) {
                return VERIFICATION_TRUE;
            } else {
                return VERIFICATION_FALSE;
            }
        }
    }

    /**
     * Recalls {@link #verifyUser(Component, String)}
     * with Board.getRoot() as the component param;
     * as a result the verification will be made on the Dashboard instance.
     * @see #verifyUser(Component, String)
     */
    public static int verifyUser(String text){
        return verifyUser(Board.getRoot(), text);
    }

    /**
     * Signaled to reports an error indicating that the user has entered an incorrect
     * mat. number from a {@link #verifyUser(Component, String)} operation.
     */
    public static void reportMatError(Component parent){
        reportError(parent,"Mat Error",
                "That matriculation number does not match. Try again.");
    }

    public static void reportMatError(){
        reportMatError(Board.getRoot());
    }

    /**
     * Prompts the user with a yes-no-cancel dialog on the given parent component.
     */
    public static boolean showYesNoCancelDialog(Component parent, String title, String text) {
        final int consent = JOptionPane.showConfirmDialog(parent, dialogComponent(text), title,
                JOptionPane.YES_NO_CANCEL_OPTION);
        return consent == JOptionPane.YES_OPTION;
    }

    /**
     * @see #showYesNoCancelDialog(Component, String, String)
     */
    public static boolean showYesNoCancelDialog(String title, String text){
        return showYesNoCancelDialog(Board.getRoot(), title, text);
    }

    /**
     * Prompts the user with a ok-cancel dialog on the given parent component.
     */
    public static boolean showOkCancelDialog(Component parent, String title, String text){
        final int consent = JOptionPane.showConfirmDialog(parent, dialogComponent(text), title,
                JOptionPane.OK_CANCEL_OPTION);
        return consent == JOptionPane.OK_OPTION;
    }

    /**
     * @see #showOkCancelDialog(Component, String, String)
     */
    public static boolean showOkCancelDialog(String title, String text){
        return showOkCancelDialog(Board.getRoot(), title, text);
    }

    /**
     * Requests an input from the user on the given parent component.
     * Seemingly gives null if the dialog is dismissed.
     */
    public static String requestInput(Component parent, String title, String text){
        return JOptionPane.showInputDialog(parent, dialogComponent(text), title,
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * @see #requestInput(Component, String, String)
     */
    public static String requestInput(String title, String text){
        return requestInput(Board.getRoot(), title, text);
    }

    /**
     * Report the given error message on this parent component.
     */
    public static void reportError(Component parent, String title, String message){
        JOptionPane.showMessageDialog(parent, dialogComponent(message), title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Report the given error message on the Dashboard's instance.
     * @see #reportError(Component, String, String)
     */
    public static void reportError(String title, String message){
        reportError(Board.getRoot(), title, message);
    }

    /**
     * Makes a Dashboard-based report of the given exception on this parent component.
     * The title will be set to the className of the exception;
     * and the text will be whatever its getMessage() returns.
     */
    public static void reportError(Component parent, Exception e){
        reportError(parent, e.getClass().getName(), e.getMessage());
    }

    /**
     * Makes a Dashboard-based report of the given exception on the Dashboard's instance.
     * The title will be set to the class-name of the exception;
     * and the text will be whatever its getMessage() returns.
     */
    public static void reportError(Exception e){
        reportError(e.getClass().getName(), e.getMessage());
    }

    /**
     * Reports an information message on this parent component.
     */
    public static void reportInfo(Component parent, String title, String information) {
        JOptionPane.showMessageDialog(parent, dialogComponent(information), title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Reports an information message on the Dashboard's instance.
     */
    public static void reportInfo(String title, String information){
        reportInfo(Board.getRoot(), title, information);
    }

    /**
     * Reports a warning message on this parent component.
     */
    public static void reportWarning(Component parent, String title, String message){
        JOptionPane.showMessageDialog(parent, dialogComponent(message), title,
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Reports a warning message on the Dashboard's instance.
     */
    public static void reportWarning(String title, String message){
        reportWarning(Board.getRoot(), title, message);
    }

    /**
     * Precedes by an error in building the driver.
     * It might be that firefox/chrome is not installed.
     * The report will be made on the given parent component.
     */
    public static void reportMissingDriver(Component parent) {
        reportError(parent, "Driver Error",
                "Sorry, setting up the driver doesn't complete normally.\n" +
                "Please make sure that \"Firefox\" or \"Chrome\" is installed and try again.");
    }

    /**
     * Reports an error indicating that an attempt to build the driver was unsuccessful.
     * This report will, eventually, be made the Dashboard's instance.
     * @see #reportMissingDriver(Component)
     */
    public static void reportMissingDriver(){
        reportMissingDriver(Board.getRoot());
    }

    /**
     * This report indicates that a connection was lost with the Portal.
     * A connection might be lost due to a sudden internet problem;
     * or an error while scrapping the Portal.
     * The later case also delegate to this method as a camouflage until
     * such an issue is fixed.
     */
    public static void reportConnectionLost(Component parent) {
        reportError(parent,"Connection Lost",
                "Sorry, we are having troubles connecting to the Portal.\n" +
                "Please try again later.");
    }

    /**
     * Reports a lost in connection with the Portal on the Dashboard's instance.
     * @see #reportConnectionLost(Component)
     */
    public static void reportConnectionLost() {
        reportConnectionLost(Board.getRoot());
    }

    /**
     * Reports that Dashboard could not detect internet connection.
     * This report is made on the given parent component.
     */
    public static void reportNoInternet(Component parent) {
        reportError(parent, "No Internet",
                "Sorry, we're having troubles connecting to the internet.\n" +
                "Please try again.");
    }

    /**
     * Reports an absence of internet connection on the Dashboard's instance.
     * @see #reportNoInternet(Component)
     */
    public static void reportNoInternet() {
        reportNoInternet(Board.getRoot());
    }

    public static void reportLoginAttemptFailed() {
        reportError(Board.getRoot(), "Login Failed",
                "Dashboard has been denied access to your portal.\n" +
                "Please go to the settings and make sure the right credentials are given.");
    }

    /**
     * Convenient way of suppressing exceptions.
     * This prints the StackTrace of the exception.
     *
     * Should Dashboard implement report system, this will be useful.
     */
    public static void silenceException(Exception e){
        e.printStackTrace();
    }

    /**
     * Convenient way of suppressing exceptions.
     * This writes the passed string to the console
     * using the standard error-reporter.
     * @see #silenceException(Exception)
     */
    public static void silenceException(String message){
        System.err.println("[ERROR] "+message);
    }

    public static void silenceInfo(String message){
        System.out.println("[INFO] "+message);
    }

    public static void silenceWarning(String message){
        System.out.println("[WARNING] "+message);
    }

    /**
     * Provides the text used by the option-dialogs on a panel.
     * The text, if long, should be separated by lines,
     * and the lines are added to the panel in such a manner.
     *
     * This is a control-point should Dashboard style messages
     * using HTML, on textPanes.
     * Todo: use Globals.joinLines, and unpack them herein using splitLines
     */
    public static KPanel dialogComponent(String text){
        final KPanel panel = new KPanel();
        panel.setOpaque(false);
        panel.setReflectTheme(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (String line : text.split("\n")) {
            final KLabel label = new KLabel(line, FontFactory.createPlainFont(15));
            label.setOpaque(false);
//            label.setReflectTheme(false);
            panel.add(label);
        }
        return panel;
    }

}
