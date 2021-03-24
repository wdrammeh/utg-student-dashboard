package core;


import proto.KFontFactory;
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


    /**
     * This method is stagnant. Ans so for uniformity only.
     * The given iconName must be a pre-existing name in the icons dir.,
     * the URL-pointer to which is to be returned.
     */
    public static URL getIconURL(String iconName){
        return App.class.getResource("/icons/"+iconName);
    }

    /**
     * A centralized point for user verification.
     * Note that some operations, however, do no necessarily delegate to this.
     * This function gives access if the current user is trial, or {@link Settings#noVerifyNeeded}
     * is true.
     * Operations like logging out should ask for user-confirmation even if {@link Settings#noVerifyNeeded}
     * is true.
     * The given text will be shown to the user as a hint. And the operation will be performed
     * on the parent, which if null, then the Dashboard instance.
     */
    public static int verifyUser(Component parent, String text){
        if (Settings.noVerifyNeeded || Student.isTrial()) {
            return VERIFICATION_TRUE;
        } else {
            final String input = requestInput(parent == null ? Board.getRoot() : parent, "Confirm", text);
            if (input == null) {
                return DIALOG_DISMISSED;
            } else if (Globals.hasNoText(input)) {//in which case, can only be blank.
                return INPUT_BLANK;
            } else if (input.equals(Student.getMatNumber())) {
                return VERIFICATION_TRUE;
            } else {
                return VERIFICATION_FALSE;
            }
        }
    }

    /**
     * Recalls {@link #verifyUser(Component, String)} with a null component param;
     * as a result the verification will be made on the Dashboard instance.
     * @see #verifyUser(Component, String)
     */
    public static int verifyUser(String text){
        return verifyUser(null, text);
    }

    /**
     * Signaled to reports an error indicating that the user has entered an incorrect
     * mat. number from a {@link #verifyUser(Component, String)} operation.
     * The report will be made on the given component, if null, the Dashboard instance.
     */
    public static void reportMatError(Component parent){
        reportError(parent == null ? Board.getRoot() : parent,"Mat Error",
                "That matriculation number does not match. Try again.");
    }

    /**
     * Recalls {@link #reportMatError(Component)} with a null component param;
     * as a result the report will be made on the Dashboard instance.
     * @see #reportMatError(Component)
     */
    public static void reportMatError(){
        reportMatError(null);
    }

//    showXXx() calls
//    Simplified methods that returns a boolean indicating that the user consents with a JOptionPane's dialog

    /**
     * Prompts the user with a yes-no-cancel dialog on the given parent component
     * [or Board.getRoot() if null].
     * Returns true if the user press the yes-option, false otherwise.
     */
    public static boolean showYesNoCancelDialog(Component parent, String title, String text){
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        final int consent = JOptionPane.showConfirmDialog(actualParent, dialogTextPanel(text), title,
                JOptionPane.YES_NO_CANCEL_OPTION);
        return consent == JOptionPane.YES_OPTION;
    }

    /**
     * Shows a yes-no-cancel dialog on the Dashboard's instance with the given title and text.
     * @see #showYesNoCancelDialog(Component, String, String)
     */
    public static boolean showYesNoCancelDialog(String title, String text){
        return showYesNoCancelDialog(null, title, text);
    }

    /**
     * Prompts the user with a ok-cancel dialog on the given parent component [or Board.getRoot() if null].
     * Returns true if the user press the ok-option, false otherwise.
     */
    public static boolean showOkCancelDialog(Component parent, String title, String text){
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        final int consent = JOptionPane.showConfirmDialog(actualParent, dialogTextPanel(text), title,
                JOptionPane.OK_CANCEL_OPTION);
        return consent == JOptionPane.OK_OPTION;
    }

    /**
     * Shows a ok-cancel dialog on the Dashboard's instance with the given title and text.
     * @see #showOkCancelDialog(Component, String, String)
     */
    public static boolean showOkCancelDialog(String title, String text){
        return showOkCancelDialog(null, title, text);
    }

    /**
     * Requests an input from the user on the parent component [or Board.getRoot() if null].
     * Seemingly gives null if the dialog is dismissed.
     */
    public static String requestInput(Component parent, String title, String text){
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        return JOptionPane.showInputDialog(actualParent, dialogTextPanel(text), title,
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Requests an input from the user on the Dashboard's instance with the given title and text.
     * @see #requestInput(Component, String, String)
     */
    public static String requestInput(String title, String text){
        return requestInput(null, title, text);
    }

//    reportXXx() calls
//    convenient ways of reporting to the user

    /**
     * Report the given error message on the parent component [or Board.getRoot() if null].
     */
    public static void reportError(Component parent, String title, String message){
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        JOptionPane.showMessageDialog(actualParent, dialogTextPanel(message), title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Report the given error message on the Dashboard's instance.
     */
    public static void reportError(String title, String message){
        reportError(null, title, message);
    }

    /**
     * Makes a Dashboard-based report of the given exception on this parent component,
     * [or Board.getRoot() if null].
     * The title will be set to the class-name of the exception;
     * and the text will be whatever its getMessage() returns.
     */
    public static void reportError(Component parent, Exception e){
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        reportError(actualParent, e.getClass().getName(), e.getMessage());
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
     * Reports an information message on this parent component [or Board.getRoot()if null].
     */
    public static void reportInfo(Component parent, String title, String information) {
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        JOptionPane.showMessageDialog(actualParent, dialogTextPanel(information), title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Reports an information message on the Dashboard's instance.
     */
    public static void reportInfo(String title, String information){
        reportInfo(null, title, information);
    }

    /**
     * Reports a warning message on this parent component [or Board.getRoot()if null].
     */
    public static void reportWarning(Component parent, String title, String message){
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        JOptionPane.showMessageDialog(actualParent, dialogTextPanel(message), title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Reports a warning message on the Dashboard's instance.
     */
    public static void reportWarning(String title, String message){
        reportWarning(null, title, message);
    }

    /**
     * Precedes by an error in building the driver.
     * It might be that firefox is not installed.
     * Please note that Dashboard is to be flexible with its driver specifications
     * in a future release.
     * The report will be made on the given parent component [or Board.getRoot() if null].
     */
    public static void reportMissingDriver(Component parent) {
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        reportError(actualParent, "Driver Error",
                "Sorry, setting up the driver doesn't complete normally.\n" +
                "Please make sure that \"Firefox\" Browser is installed and try again.");
    }

    /**
     * Reports an error indicating that an attempt to build the driver was unsuccessful.
     * This report will, eventually, be made the Dashboard's instance.
     * @see #reportMissingDriver(Component)
     */
    public static void reportMissingDriver(){
        reportMissingDriver(null);
    }

    /**
     * This report indicates that a connection was lost with the Portal.
     * A connection might be lost due to a sudden internet problem;
     * or an error while scrapping the Portal.
     * The later case also delegate to this method as a camouflage until
     * such an issue is fixed.
     */
    public static void reportConnectionLost(Component parent) {
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        reportError(actualParent,"Connection Lost",
                "Sorry, we are having troubles connecting to the Portal.\n" +
                "Please try again later.");
    }

    /**
     * Reports a lost in connection with the Portal on the Dashboard's instance.
     */
    public static void reportConnectionLost() {
        reportConnectionLost(null);
    }

    /**
     * Reports that Dashboard could not detect internet connection.
     * This report is made on the given parent component [or Board.getRoot() if null].
     */
    public static void reportNoInternet(Component parent) {
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        reportError(actualParent, "No Internet",
                "Sorry, we're having troubles connecting to the internet.\n" +
                "Please try again.");
    }

    /**
     * Reports an absence of internet connection on the Dashboard's instance.
     */
    public static void reportNoInternet() {
        reportNoInternet(null);
    }

    public static void reportLoginAttemptFailed() {
        reportError(null, "Login Failed",
                "Dashboard has been denied access to your portal.\n" +
                "Please go to the settings and make sure the right credentials are given.");
    }

    /**
     * Convenient way of suppressing exceptions.
     * This prints the StackTrace of the exception.
     * @see #silenceException(String)
     */
    public static void silenceException(Exception e){
        e.printStackTrace();
    }

    /**
     * Convenient way of suppressing exceptions.
     * This writes the passed string to the console using the standard error-reporter.
     * @see #silenceException(Exception)
     */
    public static void silenceException(String message){
        System.err.println(message);
    }

    /**
     * Provides the text used by the option-dialogs on a panel.
     * The text, if long, should be separated into lines by the \n
     * and the lines are added to the panel in such a manner.
     */
    public static KPanel dialogTextPanel(String text){
        final KPanel panel = new KPanel();
        panel.setOpaque(false);
        panel.setReflectTheme(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (String line : text.split("\n")) {
            final KLabel label = new KLabel(line, KFontFactory.createPlainFont(15));
            label.setOpaque(false);
//            label.setReflectTheme(false);
            panel.add(label);
        }
        return panel;
    }

}
