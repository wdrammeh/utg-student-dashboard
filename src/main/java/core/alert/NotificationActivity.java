package core.alert;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.SwingConstants;

import core.Activity;
import core.Board;
import core.utils.FontFactory;
import proto.KComboBox;
import proto.KLabel;
import proto.KPanel;
import proto.KScrollPane;
import utg.Dashboard;

public class NotificationActivity implements Activity {

    public NotificationActivity() {
        LocalAlertHandler.initHandle();
        final KScrollPane localPane = new KScrollPane(new KPanel(LocalAlertHandler.getComponent()));

        RemoteAlertHandler.initHandle();
        final Component remotePane = RemoteAlertHandler.getComponent();

        final CardLayout cardLayout = new CardLayout();
        final KPanel centerPanel = new KPanel(cardLayout);
        cardLayout.addLayoutComponent(centerPanel.add(localPane), "Dashboard");
        cardLayout.addLayoutComponent(centerPanel.add(remotePane), "Portal");

        final KLabel hintLabel = KLabel.getPredefinedLabel("Notifications ", SwingConstants.LEFT);
        hintLabel.setFont(FontFactory.BODY_HEAD_FONT);
        hintLabel.setText("(Showing Local Alerts)");

        final KComboBox<String> alertBox = new KComboBox<>(new String[] {"Dashboard", "Portal"});
        alertBox.addActionListener(e-> {
            final int selectedIndex = alertBox.getSelectedIndex();
            if (selectedIndex == 0) {
                cardLayout.show(centerPanel,"Dashboard");
                hintLabel.setText("(Showing Local Dashboard Alerts)");
            } else if (selectedIndex == 1) {
                cardLayout.show(centerPanel, "Portal");
                hintLabel.setText("(Showing Portal Alerts)");
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

}
