package proto;

import core.utils.App;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class KFrame extends JFrame implements Preference {

    public KFrame(String title){
        super(title);
        setPreferences();
    }

    /**
     * Gets the icon used by frames and dialogs.
     * Native systems might use this as a launcher icon.
     */
    public static Image getIcon() {
        final URL iPath = App.getIconURL("dashboard.png");
        return Toolkit.getDefaultToolkit().getImage(iPath);
    }

    @Override
    public void setPreferences() {
        setIconImage(getIcon());
    }

}
