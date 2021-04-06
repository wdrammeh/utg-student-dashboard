package core.utils;

import core.first.Login;
import proto.KFontFactory;
import proto.KLabel;
import proto.KPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

public class MComponent {
    public static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);


    /**
     * Returns an image-icon from this URL, and scales it
     * relative to the given width and height.
     * This call is self-silence.
     * @see #scaleIcon(String, int, int)
     */
    public static ImageIcon scaleIcon(URL url, int width, int height) {
        ImageIcon icon = null;
        try {
            final BufferedImage buf = ImageIO.read(url);
            icon = new ImageIcon(buf.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            App.silenceException(e);
        }
        return icon;
    }

    /**
     * Generates an url from the given name and returns an image-icon
     * as defined by {@link #scaleIcon(URL, int, int)}
     * This call is consequently self-silence.
     */
    public static ImageIcon scaleIcon(String name, int width, int height) {
        return scaleIcon(App.getIconURL(name), width, height);
    }

    /**
     * Strips these container(s) off their children, if there's any.
     */
    public static void empty(Container... components){
        for (Container c : components) {
            c.removeAll();
        }
    }

    public static void ready(Container... components){
        for (Container c : components) {
            c.repaint();
            c.revalidate();
        }
    }

    /**
     * Re-sets the state of these components, i.e on or off
     * as determined by the enable-param
     */
    public static void setEnabled(boolean enable, Component... components){
        for (Component c : components) {
            c.setEnabled(enable);
        }
    }

    /**
     * Toggles the state of each of these components.
     * If a component is on, it will be off; and vice-versa.
     */
    public static void toggleEnabled(Component... components){
        for (Component c : components) {
            c.setEnabled(!c.isEnabled());
        }
    }

    /**
     * Dashboard's standard tool-tip.
     * All supporting and targeting components must use this for uniformity.
     */
    public static JToolTip preferredTip(){
        final JToolTip toolTip = new JToolTip();
        toolTip.setFont(KFontFactory.createPlainFont(15));
        toolTip.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        return toolTip;
    }

    /**
     * This is particularly ideal under box-layouts.
     * And it's intended to be the space between the contents and the bottom.
     */
    public static Component contentBottomGap(){
        return Box.createVerticalStrut(20);
    }

    /**
     * Returns a component in place of an activity which is restricted.
     */
    public static Component createUnavailableActivity(String activityName){
        final KLabel label1 = new KLabel(activityName, KFontFactory.createBoldFont(30));
        final KLabel label2 = new KLabel("This activity is not supported for \"Trial Users\"",
                KFontFactory.createPlainFont(20), Color.DARK_GRAY);
        final KLabel label3 = new KLabel("If you are a student of The University of The Gambia, you may...",
                KFontFactory.createPlainFont(20), Color.GRAY);
        final KLabel loginLabel = new KLabel("Login now", KFontFactory.createPlainFont(20), Color.BLUE);
        loginLabel.underline(false);
        loginLabel.setCursor(HAND_CURSOR);
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Login.loginAction(loginLabel);
            }
        });

        final KPanel innerPanel = new KPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.addAll(new KPanel(label1), new KPanel(label2),
                new KPanel(new FlowLayout(FlowLayout.CENTER, 5, 25), label3, loginLabel));

        final KPanel outerPanel = new KPanel(new BorderLayout());
        outerPanel.add(Box.createVerticalStrut(100), BorderLayout.NORTH);
        outerPanel.add(innerPanel, BorderLayout.CENTER);
        outerPanel.add(Box.createVerticalStrut(150), BorderLayout.SOUTH);
        return outerPanel;
    }

    public static Component createRigidArea(int w, int h){
        return Box.createRigidArea(new Dimension(w, h));
    }

}
