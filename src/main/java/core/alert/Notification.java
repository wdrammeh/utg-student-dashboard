package core.alert;

import core.Board;
import core.serial.Serializer;
import core.utils.*;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;

public class Notification {
    private String heading;
    private String summary;
    private String information;
    private boolean isRead;
    private Date date;
    private KPanel layer;
    private KLabel innerLabel;
    public static final ArrayList<Notification> NOTIFICATIONS = new ArrayList<>();


    private Notification(String heading, String summary, String information, Date date) {
        this.heading = heading;
        this.summary = summary;
        this.information = information;
        this.date = date;
        this.layer = new KPanel(new BorderLayout(), new Dimension(975, 35));
        layer.setCursor(KComponent.HAND_CURSOR);
        layer.add(new KPanel(new KLabel(heading.toUpperCase(), KFontFactory.createBoldFont(15),
                Color.BLUE)), BorderLayout.WEST);
        innerLabel = new KLabel(summary, KFontFactory.createPlainFont(16), Color.RED);
        layer.add(new KPanel(innerLabel), BorderLayout.CENTER);
        layer.add(new KPanel(new KLabel(KDate.formatDay(date), KFontFactory.createPlainFont(15),
                Color.GRAY)), BorderLayout.EAST);
        layer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                innerLabel.setFont(KFontFactory.createBoldFont(16));
            }

            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()-> new Exhibitor(Notification.this).setVisible(true));
                if (!isRead) {
                    setRead(true);
                    NotificationActivity.effectCount(-1);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                innerLabel.setFont(KFontFactory.createPlainFont(16));
            }
        });
    }

    /**
     * Readily creates a new notification, adding it to the presently
     * unread notifications panel.
     * Where the information is an HTML text.
     */
    public static void create(String heading, String summary, String information) {
        final Notification incoming = new Notification(heading, summary, information, new Date());
        NotificationActivity.join(incoming);
        NOTIFICATIONS.add(incoming);
    }

    public boolean isRead(){
        return isRead;
    }

    private void setRead(boolean isRead){
        if (isRead) {
            this.isRead = true;
            innerLabel.setForeground(null);
        } else {
            this.isRead = false;
            innerLabel.setForeground(Color.RED);
        }
    }

    public KPanel getLayer(){
        return layer;
    }

    private String export(){
        return Globals.joinLines(new Object[]{heading, summary, information,
                KDate.toSerial(date), isRead});
    }

    private static class Exhibitor extends KDialog {

        private  Exhibitor(Notification alert) {
            super(alert.heading+" - Notification");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final String decidedText = alert.information == null ? alert.summary :
                    alert.information;
            final KTextPane noticePane = KTextPane.htmlFormattedPane(decidedText);

            final KScrollPane textScroll = new KScrollPane(noticePane);
            textScroll.setPreferredSize(new Dimension(575,225));

            final KButton deleteButton = new KButton("Remove");
            deleteButton.addActionListener(e-> dispose());
            deleteButton.addActionListener(NotificationActivity.deleteAction(alert));

            final KButton disposeButton = new KButton("Close");
            disposeButton.addActionListener(e-> dispose());

            final KPanel lowerPart = new KPanel(new BorderLayout());
            lowerPart.add(new KPanel(new KLabel(KDate.formatDayTime(alert.date),
                    KFontFactory.createPlainFont(15))), BorderLayout.WEST);
            lowerPart.add(new KPanel(deleteButton, disposeButton), BorderLayout.EAST);

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(textScroll, KComponent.contentBottomGap(), lowerPart);
            setContentPane(contentPanel);
            getRootPane().setDefaultButton(disposeButton);
            pack();
            setLocationRelativeTo(Board.getRoot());
        }
    }

    public static void serialize() {
        final String[] alerts = new String[NOTIFICATIONS.size()];
        for(int i = 0; i < NOTIFICATIONS.size(); i++){
            alerts[i] = NOTIFICATIONS.get(i).export();
        }
        Serializer.toDisk(alerts, Serializer.inPath("alerts.ser"));
    }

    public static void deserialize(){
        final Object alertsObj = Serializer.fromDisk(Serializer.inPath("alerts.ser"));
        if (alertsObj == null) {
            App.silenceException("Failed to read Notifications.");
        } else {
            final String[] alerts = (String[]) alertsObj;
            for (String data : alerts) {
                final String[] content = Globals.splitLines(data);
                final Notification alert = new Notification(content[0], content[1], content[2],
                        KDate.fromSerial(content[3]));
                alert.setRead(Boolean.parseBoolean(content[4]));
                NotificationActivity.join(alert);
                NOTIFICATIONS.add(alert);
            }
        }
    }

}
