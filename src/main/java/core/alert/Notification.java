package core.alert;

import core.Board;
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
        layer.setCursor(MComponent.HAND_CURSOR);
        layer.add(new KPanel(new KLabel(heading.toUpperCase(), FontFactory.createBoldFont(15),
                Color.BLUE)), BorderLayout.WEST);
        innerLabel = new KLabel(summary, FontFactory.createPlainFont(16), Color.RED);
        layer.add(new KPanel(innerLabel), BorderLayout.CENTER);
        layer.add(new KPanel(new KLabel(MDate.formatDay(date), FontFactory.createPlainFont(15),
                Color.GRAY)), BorderLayout.EAST);
        layer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                innerLabel.setFont(FontFactory.createBoldFont(16));
            }

            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()-> new Exhibitor(Notification.this).setVisible(true));
                if (!isRead) {
                    setRead(true);
                    LocalAlertHandler.effectCount(-1);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                innerLabel.setFont(FontFactory.createPlainFont(16));
            }
        });
    }

    /**
     * Readily creates a new notification, adding it to the presently
     * unread notifications panel at the Activity.
     * Where the information is an HTML text.
     */
    public static void create(String heading, String summary, String information) {
        final Notification incoming = new Notification(heading, summary, information, new Date());
        LocalAlertHandler.receive(incoming);
        NOTIFICATIONS.add(incoming);
    }

    public boolean isRead(){
        return isRead;
    }

    public void setRead(boolean isRead){
        this.isRead = isRead;
        innerLabel.setForeground(isRead ? null : Color.RED);
    }

    public KPanel getLayer(){
        return layer;
    }

    public String export(){
        return Globals.joinLines(new Object[]{heading, summary, information, MDate.toSerial(date), isRead});
    }


    private static class Exhibitor extends KDialog {

        private  Exhibitor(Notification alert) {
            super(alert.heading+" - Notification");
            setModalityType(DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final String decidedText = alert.information == null ? alert.summary : alert.information;
            final KTextPane noticePane = KTextPane.htmlFormattedPane(decidedText);

            final KScrollPane textScroll = new KScrollPane(noticePane);
            textScroll.setPreferredSize(new Dimension(600,250));

            final KButton deleteButton = new KButton("Remove");
            deleteButton.addActionListener(e-> dispose());
            deleteButton.addActionListener(e-> LocalAlertHandler.delete(alert));

            final KButton disposeButton = new KButton("Close");
            disposeButton.addActionListener(e-> dispose());

            final KPanel lowerPart = new KPanel(new BorderLayout());
            lowerPart.add(new KLabel(MDate.formatDayTime(alert.date), FontFactory.createPlainFont(15)),
                    BorderLayout.WEST);
            lowerPart.add(new KPanel(new FlowLayout(FlowLayout.CENTER, 5, 10), deleteButton, disposeButton), BorderLayout.EAST);

            final KPanel contentPanel = new KPanel(new BorderLayout());
            contentPanel.add(textScroll, BorderLayout.CENTER);
            contentPanel.add(lowerPart, BorderLayout.SOUTH);
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

    public static void deserialize() {
        final Object alertsObj = Serializer.fromDisk(Serializer.inPath("alerts.ser"));
        if (alertsObj == null) {
            App.silenceException("Failed to read Notifications.");
        } else {
            final String[] alerts = (String[]) alertsObj;
            for (String data : alerts) {
                final String[] content = Globals.splitLines(data);
                final Notification alert = new Notification(content[0], content[1], content[2],
                        MDate.fromSerial(content[3]));
                alert.setRead(Boolean.parseBoolean(content[4]));
                LocalAlertHandler.receive(alert);
                NOTIFICATIONS.add(alert);
            }
        }
    }

}
