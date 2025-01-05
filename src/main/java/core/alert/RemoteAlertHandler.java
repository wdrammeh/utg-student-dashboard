package core.alert;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;

import org.openqa.selenium.firefox.FirefoxDriver;

import core.Board;
import core.Portal;
import core.driver.MDriver;
import core.module.SemesterActivity;
import core.user.Student;
import core.utils.App;
import core.utils.FontFactory;
import core.utils.Internet;
import core.utils.MComponent;
import org.openqa.selenium.remote.RemoteWebDriver;
import proto.KButton;
import proto.KDialog;
import proto.KLabel;
import proto.KPanel;
import proto.KSeparator;
import proto.KTextPane;

public class RemoteAlertHandler {
    private static KPanel portalPanel;
    private static KButton refreshButton;
    private static KLabel admissionLabel;
    private static KLabel registrationLabel;
    private static RemoteWebDriver noticeDriver;
    

    public static void initHandle() {
        admissionLabel = new KLabel(Student.isGuest() ? "Not available" : Portal.getAdmissionNotice(),
                FontFactory.createPlainFont(16));
        final KPanel admissionPanel = new KPanel(new BorderLayout());
        admissionPanel.setPreferredSize(new Dimension(1_000, 35));
        admissionPanel.setCursor(MComponent.HAND_CURSOR);
        admissionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                admissionLabel.setFont(FontFactory.createBoldFont(16));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()->
                        new NoticeExhibition(NoticeExhibition.ADMISSION_NOTICE).setVisible(true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                admissionLabel.setFont(FontFactory.createPlainFont(16));
            }
        });
        admissionPanel.add(new KPanel(new KLabel("ADMISSION ALERT:", FontFactory.createBoldFont(15),
                Color.BLUE)), BorderLayout.WEST);
        admissionPanel.add(new KPanel(admissionLabel), BorderLayout.CENTER);

        registrationLabel = new KLabel(Student.isGuest() ? "Not available" : Portal.getRegistrationNotice(),
                FontFactory.createPlainFont(16));
        final KPanel registrationPanel = new KPanel(new BorderLayout());
        registrationPanel.setPreferredSize(new Dimension(1_000, 35));
        registrationPanel.setCursor(MComponent.HAND_CURSOR);
        registrationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registrationLabel.setFont(FontFactory.createBoldFont(16));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()->
                        new NoticeExhibition(NoticeExhibition.REGISTRATION_NOTICE).setVisible(true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registrationLabel.setFont(FontFactory.createPlainFont(16));
            }
        });
        registrationPanel.add(new KPanel(new KLabel("REGISTRATION ALERT:", FontFactory.createBoldFont(15),
                Color.BLUE)), BorderLayout.WEST);
        registrationPanel.add(new KPanel(registrationLabel), BorderLayout.CENTER);

        refreshButton = new KButton("Update Alerts");
        refreshButton.setFont(FontFactory.createPlainFont(15));
        refreshButton.addActionListener(e-> {
            if (Student.isGuest()) {
                App.reportInfo("Unavailable",
                        "Sorry, we cannot currently access the Portal for notices, because you're not logged in.");
            } else {
                updateNotices(true);
            }
        });

        portalPanel = new KPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        portalPanel.addAll(refreshButton, new KPanel(new KSeparator(new Dimension(975, 1))),
                admissionPanel, registrationPanel);
    }

    private static synchronized void setupDriver() {
        if (noticeDriver == null) {
            noticeDriver = MDriver.forgeNew(true);
        }
    }

    public static Component getComponent() {
        return portalPanel;
    }

    public static void updateNotices(boolean userRequested) {
        if (!(userRequested || refreshButton.isEnabled())) {
            return;
        }

        new Thread(()-> {
            setNoticeComponents(false);
            setupDriver();
            if (noticeDriver == null) {
                if (userRequested) {
                    App.reportMissingDriver();
                }
                setNoticeComponents(true);
                return;
            }
            if (!Internet.isInternetAvailable()){
                if (userRequested) {
                    App.reportNoInternet();
                }
                setNoticeComponents(true);
                return;
            }

            final int loginTry = MDriver.attemptLogin(noticeDriver);
            if (loginTry == MDriver.ATTEMPT_SUCCEEDED) {
                final boolean renew = Portal.startRenewingNotices(noticeDriver, userRequested);
                if (renew) {
                    App.reportInfo("Portal Alerts",
                            "Admission Notice: "+Portal.getAdmissionNotice()+"\n" +
                                    "Registration Notice: "+Portal.getRegistrationNotice());
                } else {
                    App.reportError("Error", "Something went wrong while updating the Notices.\n" +
                            "Please try again.");
                }
            } else if (loginTry == MDriver.ATTEMPT_FAILED) {
                if (userRequested) {
                    App.reportLoginAttemptFailed();
                }
                setNoticeComponents(true);
                return;
            } else if (loginTry == MDriver.CONNECTION_LOST) {
                if (userRequested) {
                    App.reportConnectionLost();
                }
                setNoticeComponents(true);
                return;
            }
            setNoticeComponents(true);
        }).start();
    }

    private static void setNoticeComponents(boolean responsive){
        if (responsive) {
            registrationLabel.setText(Portal.getRegistrationNotice());
            admissionLabel.setText(Portal.getAdmissionNotice());
        } else {
            final String waitingText = "Contacting Portal... Please wait.";
            registrationLabel.setText(waitingText);
            admissionLabel.setText(waitingText);
        }
        refreshButton.setEnabled(responsive);
        SemesterActivity.noticeLabel.setText(Portal.getRegistrationNotice());
    }


    public static class NoticeExhibition extends KDialog {
        public static final String ADMISSION_NOTICE = "Admission Notice";
        public static final String REGISTRATION_NOTICE = "Registration Notice";

        public NoticeExhibition(String type) {
            super(type);
            setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final String noticeText;
            if (type.equals(ADMISSION_NOTICE)) {
                if (Student.isGuest()) {
                    noticeText = "<b>Admission Notice</b> is not available for the current user." +
                            "<p>However, you can checkout the <a href=" + Portal.ADMISSION_PAGE + ">UTG Admissions</a> Page for updates.</p>";
                } else {
                    noticeText = Portal.getAdmissionNotice();
                }
            } else if (type.equals(REGISTRATION_NOTICE)) {
                if (Student.isGuest()) {
                    noticeText = "<b>Registration Notice</b> is not available for the current user. " +
                            "If you are a UTG student, then Login to track registrations.";
                } else {
                    noticeText = Portal.getRegistrationNotice();
                }
            } else {
                return;
            }

            final KTextPane noticePane = KTextPane.htmlFormattedPane(noticeText);
            noticePane.setPreferredSize(new Dimension(500, 125));

            final KButton disposeButton = new KButton("Ok");
            disposeButton.addActionListener(e-> dispose());

            final KPanel lowerPart = new KPanel();
            lowerPart.setLayout(new BoxLayout(lowerPart, BoxLayout.Y_AXIS));
            if (!Student.isGuest()) {
                lowerPart.add(new KPanel(new KLabel("Last updated: ", FontFactory.createBoldFont(15)),
                        new KLabel(type.equals(ADMISSION_NOTICE) ? Portal.getLastAdmissionNoticeUpdate() :
                                Portal.getLastRegistrationNoticeUpdate(), FontFactory.createPlainFont(15))));
            }
            lowerPart.add(new KPanel(disposeButton));

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(noticePane, lowerPart);
            setContentPane(contentPanel);
            getRootPane().setDefaultButton(disposeButton);
            pack();
            setLocationRelativeTo(Board.getRoot());
        }
    }
    
}
