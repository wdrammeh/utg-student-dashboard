package core.alert;

import core.Activity;
import core.Board;
import core.Portal;
import core.driver.MDriver;
import core.module.SemesterActivity;
import core.user.Student;
import core.utils.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import proto.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static core.alert.Notification.NOTIFICATIONS;

public class NotificationActivity implements Activity {
    private static KPanel dashboardAlertsPanel;
    private static KPanel portalAlertsPanel;
    private static KButton refreshButton;
    private static FirefoxDriver noticeDriver;
    private static KLabel admissionLabel;
    private static KLabel registrationLabel;
    private static int unreadCount;


    public NotificationActivity() {
        final CardLayout cardLayout = new CardLayout();
        final KPanel centerPanel = new KPanel(cardLayout);
        cardLayout.addLayoutComponent(centerPanel.add(getDashboardComponent()), "Dashboard");
        cardLayout.addLayoutComponent(centerPanel.add(getPortalComponent()), "Portal");

        final KLabel hintLabel = KLabel.getPredefinedLabel("Notifications ", SwingConstants.LEFT);
        hintLabel.setFont(KFontFactory.BODY_HEAD_FONT);
        hintLabel.setText("[Showing Local Dashboard Alerts]");

        final KComboBox<String> alertBox = new KComboBox<>(new String[] {"Dashboard", "Portal"});
        alertBox.addActionListener(e-> {
            final int selectedIndex = alertBox.getSelectedIndex();
            if (selectedIndex == 0) {
                cardLayout.show(centerPanel,"Dashboard");
                hintLabel.setText("[Showing Local Dashboard Alerts]");
            } else if (selectedIndex == 1) {
                cardLayout.show(centerPanel, "Portal");
                hintLabel.setText("[Showing Portal Alerts]");
            }
        });

        final KPanel northPanel = new KPanel(new BorderLayout());
        northPanel.add(new KPanel(hintLabel), BorderLayout.WEST);
        northPanel.add(new KPanel(alertBox), BorderLayout.EAST);

        final KPanel activityPanel = new KPanel(new BorderLayout());
        activityPanel.add(northPanel, BorderLayout.NORTH);
        activityPanel.add(centerPanel, BorderLayout.CENTER);
        if (!Dashboard.isFirst()) {
            Notification.deserialize();
        }
        Board.addCard(activityPanel, "Notifications");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Notifications");
    }

    private Component getDashboardComponent() {
        final KButton clearButton = new KButton("Clear Alerts");
        clearButton.setFont(KFontFactory.createPlainFont(15));
        clearButton.addActionListener(e-> {
            if (!NOTIFICATIONS.isEmpty()) {
                if (App.showOkCancelDialog("Confirm", "This action will remove all the notifications.\n" +
                        (unreadCount == 0 ? "" : "You currently have "+ Globals.checkPlurality(unreadCount,
                                "notifications")+" that are not read."))) {
                    for (Notification notification : NOTIFICATIONS) {
                        dashboardAlertsPanel.remove(notification.getLayer());
                        KComponent.ready(dashboardAlertsPanel);
                    }
                    NOTIFICATIONS.clear();
                    effectCount(-unreadCount);
                }
            }
        });

        dashboardAlertsPanel = new KPanel();
        dashboardAlertsPanel.setLayout(new BoxLayout(dashboardAlertsPanel, BoxLayout.Y_AXIS));
        dashboardAlertsPanel.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), clearButton),
                new KPanel(new KSeparator(new Dimension(975, 1))));
        return new KScrollPane(new KPanel(dashboardAlertsPanel));
    }

    /**
     * Receives the given alert into the notifications' container.
     * This alert is expected to have undergone all core settings
     * prior to this; that is why you must never call this method directly!
     * Call {@link Notification#create(String, String, String)} instead.
     */
    public static void receive(Notification alert) {
        dashboardAlertsPanel.add(alert.getLayer(), 2);
        dashboardAlertsPanel.add(Box.createVerticalStrut(5), 3);
        KComponent.ready(dashboardAlertsPanel);
        if (!alert.isRead()) {
            effectCount(1);
        }
    }

    public static void delete(Notification alert) {
        dashboardAlertsPanel.remove(alert.getLayer());
        KComponent.ready(dashboardAlertsPanel);
        NOTIFICATIONS.remove(alert);
    }

    /**
     * For all incoming or read alerts, this should be called eventually.
     * If notification is coming (new) pass 1, else if it's being read, pass -1
     * This function will also renew the toolTipText of the outline-button.
     */
    public static void effectCount(int value){
        unreadCount += value;
        final String tipText = unreadCount == 0 ? null : Globals.checkPlurality(unreadCount,
                "unread notifications");
        Board.effectNotificationToolTip(tipText);
    }

    private Component getPortalComponent() {
        admissionLabel = new KLabel(Student.isGuest() ? "Not available" : Portal.getAdmissionNotice(),
                KFontFactory.createPlainFont(16));
        final KPanel admissionPanel = new KPanel(new BorderLayout());
        admissionPanel.setPreferredSize(new Dimension(1_000, 35));
        admissionPanel.setCursor(KComponent.HAND_CURSOR);
        admissionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                admissionLabel.setFont(KFontFactory.createBoldFont(16));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()->
                        new NoticeExhibition(NoticeExhibition.ADMISSION_NOTICE).setVisible(true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                admissionLabel.setFont(KFontFactory.createPlainFont(16));
            }
        });
        admissionPanel.add(new KPanel(new KLabel("ADMISSION ALERT:", KFontFactory.createBoldFont(15),
                Color.BLUE)), BorderLayout.WEST);
        admissionPanel.add(new KPanel(admissionLabel), BorderLayout.CENTER);

        registrationLabel = new KLabel(Student.isGuest() ? "Not available" : Portal.getRegistrationNotice(),
                KFontFactory.createPlainFont(16));
        final KPanel registrationPanel = new KPanel(new BorderLayout());
        registrationPanel.setPreferredSize(new Dimension(1_000, 35));
        registrationPanel.setCursor(KComponent.HAND_CURSOR);
        registrationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registrationLabel.setFont(KFontFactory.createBoldFont(16));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()->
                        new NoticeExhibition(NoticeExhibition.REGISTRATION_NOTICE).setVisible(true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registrationLabel.setFont(KFontFactory.createPlainFont(16));
            }
        });
        registrationPanel.add(new KPanel(new KLabel("REGISTRATION ALERT:", KFontFactory.createBoldFont(15),
                Color.BLUE)), BorderLayout.WEST);
        registrationPanel.add(new KPanel(registrationLabel), BorderLayout.CENTER);

        refreshButton = new KButton("Update Alerts");
        refreshButton.setFont(KFontFactory.createPlainFont(15));
        refreshButton.addActionListener(e-> {
            if (Student.isGuest()) {
                App.reportInfo("Unavailable",
                        "Sorry, we cannot currently access the Portal for notices, because you're not logged in.");
            } else {
                updateNotices(true);
            }
        });

        portalAlertsPanel = new KPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        portalAlertsPanel.addAll(refreshButton, new KPanel(new KSeparator(new Dimension(975, 1))),
                admissionPanel, registrationPanel);
        return portalAlertsPanel;
    }

    private static synchronized void setupDriver() {
        if (noticeDriver == null) {
            noticeDriver = MDriver.forgeNew(true);
        }
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


    private static class NoticeExhibition extends KDialog {
        private static final String ADMISSION_NOTICE = "Admission Notice";
        private static final String REGISTRATION_NOTICE = "Registration Notice";

        private NoticeExhibition(String type){
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
                lowerPart.add(new KPanel(new KLabel("Last updated: ", KFontFactory.createBoldFont(15)),
                        new KLabel(type.equals(ADMISSION_NOTICE) ? Portal.getLastAdmissionNoticeUpdate() :
                                Portal.getLastRegistrationNoticeUpdate(), KFontFactory.createPlainFont(15))));
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
