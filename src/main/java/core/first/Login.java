package core.first;

import core.Board;
import core.serial.Serializer;
import core.user.Student;
import core.utils.App;
import core.utils.Globals;
import core.utils.Internet;
import core.utils.MComponent;
import proto.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// Todo: user having difficulty logging in? suggest 'Try Dashboard'
public class Login extends JDialog {
    private Component parent;
    private static KTextField emailField;
    private static JPasswordField passwordField;
    private static KButton loginButton;
    private static KButton closeButton;
    private static String initialHint;
    private static KPanel statusPanel;
    private static JRootPane rootPane;
    private static KScrollPane statusHolder;
    private static Login instance;
    private static final ActionListener CLOSE_LISTENER = e-> instance.dispose();


    public Login(Component parent){
        instance = Login.this;
        rootPane = getRootPane();
        this.parent = parent;
        setSize(720, 425);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        final KLabel bigText = new KLabel("LOGIN TO GET THE MOST OUT OF YOUR STUDENTHOOD",
                KFontFactory.createPlainFont(18), Color.WHITE);
        bigText.setBounds(15, 10, 625, 30);
        bigText.setOpaque(false);

        final KLabel utgLogo = KLabel.createIcon("UTGLogo.gif", 50, 50);
        utgLogo.setBounds(645, 0, 100, 50);
        utgLogo.setOpaque(false);

        final KLabel loginHint = new KLabel("LOGIN", KFontFactory.createBoldFont(20), Color.BLACK);
        loginHint.setBounds(205, 5, 100, 30);

        final KLabel studentLogo = KLabel.createIcon("student.png",30,30);
        studentLogo.setBounds(50, 45, 40, 30);

        final KLabel passwordLogo = KLabel.createIcon("padlock.png",40,40);
        passwordLogo.setBounds(50, 100, 40, 30);

        emailField = new KTextField();
        emailField.setBounds(105, 45, 315, 30);
        emailField.setToolTipText("Enter Email here");
        emailField.addActionListener(e-> passwordField.requestFocusInWindow());

        passwordField = new JPasswordField(){
            @Override
            public JToolTip createToolTip() {
                return MComponent.preferredTip();
            }
        };
        passwordField.setHorizontalAlignment(emailField.getHorizontalAlignment());
        passwordField.setFont(emailField.getFont());
        passwordField.setBounds(105, 100, 315, 30);
        passwordField.setToolTipText("Enter Password here");
        passwordField.addActionListener(e-> loginTriggered());

        loginButton = new KButton("LOGIN");
        loginButton.setStyle(KFontFactory.createPlainFont(15), Color.BLUE);
        loginButton.setBounds(270, 150, 100, 30);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e-> loginTriggered());

        closeButton = new KButton("CLOSE");
        closeButton.setFont(loginButton.getFont());
        closeButton.setBounds(155, 150, 100, 30);
        closeButton.addActionListener(CLOSE_LISTENER);

        final KPanel smallPanel = new KPanel();
        smallPanel.setBackground(new Color(240, 240, 240));
        smallPanel.setBorder(BorderFactory.createLineBorder(smallPanel.getBackground(),5,true));
        smallPanel.setBounds(120, 50, 500, 190);
        smallPanel.setLayout(null);
        smallPanel.addAll(loginHint, studentLogo, passwordLogo, emailField, passwordField, loginButton, closeButton);

        statusPanel = new KPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(new Color(40, 40, 40));

        statusHolder = KScrollPane.getAutoScroller(statusPanel);
        statusHolder.setBounds(1, 245, 718, 180);

        initialHint = "Enter your Email and Password in the fields provided above, respectively.";
        appendToStatus(initialHint);

        final KPanel contentPanel = new KPanel();
        contentPanel.setBackground(new Color(40, 40, 40));
        contentPanel.setLayout(null);
        contentPanel.addAll(bigText, utgLogo, smallPanel, statusHolder);
        setContentPane(contentPanel);
        setLocationRelativeTo(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (parent != null) {
            SwingUtilities.invokeLater(()-> parent.setVisible(true));
        }
    }

    public static void appendToStatus(String update){
        final KLabel newLabel = new KLabel(update, KFontFactory.createPlainFont(15), Color.WHITE);
        newLabel.setOpaque(false);
        statusPanel.add(newLabel);
        MComponent.ready(statusPanel);
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            App.silenceException(e);
        }
    }

    public static void replaceLastUpdate(String update){
        statusPanel.removeLast();
        appendToStatus(update);
    }

    public static void setInputState(boolean state){
        MComponent.clear(statusPanel);
        emailField.setEnabled(state);
        passwordField.setEnabled(state);
        loginButton.setEnabled(state);
        if (state) {
            closeButton.removeActionListener(PrePortal.CANCEL_LISTENER);
            closeButton.addActionListener(CLOSE_LISTENER);
            closeButton.setText("CLOSE");
            closeButton.setForeground(null);
            appendToStatus(initialHint);
        } else {
            closeButton.removeActionListener(CLOSE_LISTENER);
            closeButton.addActionListener(PrePortal.CANCEL_LISTENER);
            closeButton.setText("CANCEL");
            closeButton.setForeground(Color.RED);
            appendToStatus("Please hang on while you're verified");
        }
    }

    private void loginTriggered(){
        if (Globals.hasNoText(emailField.getText())) {
            App.reportError(rootPane, "No Email", "Enter your email address.");
            emailField.requestFocusInWindow();
        } else if (Globals.hasNoText(String.valueOf(passwordField.getPassword()))) {
            App.reportError(rootPane,"No Password", "Enter your password.");
            passwordField.requestFocusInWindow();
        } else {
            new Thread(()-> {
                setInputState(false);
                appendToStatus("Checking network status.......");
                if (Internet.isInternetAvailable()) {
                    replaceLastUpdate("Checking network status....... Available");
                    PrePortal.launchVerification(emailField.getText(), String.valueOf(passwordField.getPassword()));
                } else {
                    replaceLastUpdate("Checking network status....... Unavailable");
                    signalInternetError();
                    setInputState(true);
                }
            }).start();
        }
    }

    private static void signalInternetError(){
        App.reportError(rootPane, "Internet Error", "Internet connection is required to set up Dashboard.\n" +
                "Please connect to the internet and try again.");
    }

    public static void loginAction(Component clickable){
        new Thread(()-> {
            clickable.setEnabled(false);
            if (Internet.isInternetAvailable()) {
                final Board boardInstance = Board.getInstance();
                final Login login = new Login(null);
                login.setLocationRelativeTo(boardInstance);
                login.setModalityType(DEFAULT_MODALITY_TYPE);
                SwingUtilities.invokeLater(()-> login.setVisible(true));
            } else {
                signalInternetError();
            }
            clickable.setEnabled(true);
        }).start();
    }

    public static JRootPane getRoot(){
        return rootPane;
    }

    public static Login getInstance(){
        return instance;
    }

    /**
     * PrePortal should call this after it's done all its tasks.
     */
    public static void notifyCompletion(){
        appendToStatus("Now running Pre-Dashboard builds....... Please wait");
        closeButton.setEnabled(false);
        Board.setReady(false);
        Dashboard.setFirst(true);
        Student.initialize();
        final KButton enter = new KButton();
        enter.setFocusable(true);
        enter.addActionListener(e-> {
            instance.setVisible(false);
            if (Board.getInstance() != null) {
                Runtime.getRuntime().removeShutdownHook(Board.SHUT_DOWN_HOOK);
                Serializer.unMountUserData();
                Board.getInstance().dispose();
            }
            new Board().setVisible(true);
        });
        rootPane.add(enter);
        rootPane.setDefaultButton(enter);
        replaceLastUpdate("Now running Pre-Dashboard builds....... Completed");
        appendToStatus("Your Dashboard is ready : Press \"Enter\" to launch now");
        appendToStatus("------------------------------------------------------------------------------------------------------------------------------------------");
        appendToStatus("                                                <<<<------- Enter ------>>>>");
        appendToStatus("-");
        statusHolder.stopAutoScrolling();
    }

}
