package core.alert;

import core.Board;
import core.serial.Serializer;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;

/**
 * Or NotificationSelf
 * The serialization approach utilized by this class may not be compatible with
 * later versions of Dashboard.
 */
public class Notification {
    private String heading;
    private String text;
    private String information; // dialog parses this same text, if the description is null.
    private boolean isRead;
    private Date time;
    private transient Exhibitor shower;
    private transient NotificationLayer layer;
    public static final ArrayList<Notification> NOTIFICATIONS = new ArrayList<>();


    private Notification(String heading, String vText, String information, Date date) {
        this.heading = heading;
        this.text = vText;
        this.information = information;
        this.time = date;
        this.shower = new Exhibitor(this);
        this.layer = new NotificationLayer(this);
    }

    /**
     * Creates a new notification.
     * This call is complete, and must remain a central point for
     * creating Dashboard specific alerts.
     * Give the information as a HTML text.
     */
    public static void create(String heading, String vText, String information) {
        final Notification incoming = new Notification(heading, vText, information, new Date());
        NotificationActivity.join(incoming);
        NOTIFICATIONS.add(incoming);
    }

    public String getHeading(){
        return heading;
    }

    public String getText(){
        return text;
    }

    public String getInformation(){
        return information;
    }

    public KDialog getShower(){
        return shower;
    }

    //This is an enquiry
    public boolean isRead(){
        return isRead;
    }

    //This is an update
    private void justRead(){
        isRead = true;
        layer.innerLabel.setForeground(null);
    }

    public KPanel getLayer(){
        return layer;
    }

    private String export(){
        return Globals.joinLines(heading,
                text,
                information,
                MDate.format(time),
                isRead);
    }


    private static class Exhibitor extends KDialog {

        private  Exhibitor(Notification notification){
            super(notification.getHeading()+" - Notification");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final String decidedText = notification.getInformation() == null ? notification.getText() :
                    notification.getInformation();
            final KTextPane noticePane = KTextPane.htmlFormattedPane(decidedText);

            final KScrollPane textScroll = new KScrollPane(noticePane);
            textScroll.setPreferredSize(new Dimension(550,235));

            final KButton deleteButton = new KButton("Remove");
            deleteButton.addActionListener(NotificationActivity.deleteAction(notification));

            final KButton disposeButton = new KButton("Close");
            disposeButton.setFocusable(true);
            disposeButton.addActionListener(closeListener());

            final KPanel lowerPart = new KPanel(new BorderLayout());
            lowerPart.add(new KPanel(new KLabel(MDate.format(notification.time), KFontFactory.createPlainFont(16))),
                    BorderLayout.WEST);
            lowerPart.add(new KPanel(deleteButton, disposeButton), BorderLayout.EAST);

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(textScroll, MComponent.contentBottomGap(), lowerPart);
            setContentPane(contentPanel);
            getRootPane().setDefaultButton(disposeButton);
            pack();
            setLocationRelativeTo(Board.getRoot());
        }

        private ActionListener closeListener(){
            return e-> dispose();
        }

    }


    private static class NotificationLayer extends KPanel {
        private Notification notification;
        private KLabel innerLabel;

        private NotificationLayer(Notification alert) {
            this.notification = alert;
            setPreferredSize(new Dimension(975, 35));
            setCursor(MComponent.HAND_CURSOR);
            addMouseListener(forgeListener(this));
            setLayout(new BorderLayout());
            add(new KPanel(new KLabel(alert.heading.toUpperCase(), KFontFactory.createBoldFont(16),
                    Color.BLUE)), BorderLayout.WEST);
            innerLabel = new KLabel(alert.text, KFontFactory.createPlainFont(16), alert.isRead ? null : Color.RED);
            add(new KPanel(innerLabel), BorderLayout.CENTER);
            add(new KPanel(new KLabel(MDate.formatDateOnly(alert.time), KFontFactory.createPlainFont(16), Color.GRAY)),
                    BorderLayout.EAST);
        }

        private static MouseListener forgeListener(NotificationLayer layer){
            return new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    layer.innerLabel.setFont(KFontFactory.createBoldFont(16));
                }

                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    SwingUtilities.invokeLater(()-> layer.notification.shower.setVisible(true));
                    if (!layer.notification.isRead()) {
                        layer.notification.justRead();
                        NotificationActivity.effectCount(-1);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    layer.innerLabel.setFont(KFontFactory.createPlainFont(16));
                }
            };
        }
    }


    public static void serialize() {
        final String[] alerts = new String[NOTIFICATIONS.size()];
        for(int i = 0; i < NOTIFICATIONS.size(); i++){
            alerts[i] = NOTIFICATIONS.get(i).export();
        }
        Serializer.toDisk(alerts, Serializer.inPath("alerts.ser"));
    }

    public static void deSerialize(){
        final Object alertsObj = Serializer.fromDisk(Serializer.inPath("alerts.ser"));
        if (alertsObj == null) {
            App.silenceException("Failed to read Notifications.");
        } else {
            final String[] alerts = (String[]) alertsObj;
            for (String data : alerts) {
                final String[] content = Globals.splitLines(data);
                final Notification alert = new Notification(content[0], content[1], content[2],
                        MDate.parse(content[3]));
                alert.isRead = Boolean.parseBoolean(content[4]);
                alert.shower = new Exhibitor(alert);
                alert.layer = new NotificationLayer(alert);
                NotificationActivity.join(alert);
                NOTIFICATIONS.add(alert);
            }
        }
    }

}
