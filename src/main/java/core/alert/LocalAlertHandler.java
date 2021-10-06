package core.alert;

import static core.alert.Notification.NOTIFICATIONS;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;

import core.Board;
import core.utils.App;
import core.utils.FontFactory;
import core.utils.Globals;
import core.utils.MComponent;
import proto.KButton;
import proto.KPanel;
import proto.KSeparator;

public class LocalAlertHandler {
    private static KPanel localPanel;
    private static int unreadCount;
    

    public static void initHandle() {
        final KButton clearButton = new KButton("Clear Alerts");
        clearButton.setFont(FontFactory.createPlainFont(15));
        clearButton.addActionListener(e-> {
            if (!NOTIFICATIONS.isEmpty()) {
                if (App.showOkCancelDialog("Confirm", "This action will remove all the notifications.\n" +
                        (unreadCount == 0 ? "" : "You currently have "+ Globals.checkPlurality(unreadCount,
                                "notifications")+" that are not read."))) {
                    for (Notification notification : NOTIFICATIONS) {
                        localPanel.remove(notification.getLayer());
                        MComponent.ready(localPanel);
                    }
                    NOTIFICATIONS.clear();
                    effectCount(-unreadCount);
                }
            }
        });

        localPanel = new KPanel();
        localPanel.setLayout(new BoxLayout(localPanel, BoxLayout.Y_AXIS));
        localPanel.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), clearButton),
            new KPanel(new KSeparator(new Dimension(975, 1))));
    }

    public static Component getComponent() {
        return localPanel;
    }

    /**
     * Receives the given alert into the notifications' container.
     * This alert is expected to have undergone all core settings
     * prior to this; that is why you must never call this method directly!
     * Call {@link Notification#create(String, String, String)} instead.
     */
    public static void receive(Notification alert) {
        localPanel.add(alert.getLayer(), 2);
        localPanel.add(Box.createVerticalStrut(5), 3);
        MComponent.ready(localPanel);
        if (!alert.isRead()) {
            effectCount(1);
        }
    }

    public static void delete(Notification alert) {
        localPanel.remove(alert.getLayer());
        MComponent.ready(localPanel);
        NOTIFICATIONS.remove(alert);
    }

    /**
     * For all incoming or read alerts, this should be called eventually.
     * If notification is coming (new) pass 1, else if it's being read, pass -1
     * This function will also renew the toolTipText of the outline-button.
     */
    public static void effectCount(int value){
        unreadCount += value;
        final String tipText = unreadCount == 0 ? null :
            Globals.checkPlurality(unreadCount, "unread notifications");
        Board.effectNotificationToolTip(tipText);
    }

}
