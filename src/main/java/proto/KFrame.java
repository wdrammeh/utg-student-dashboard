package proto;

import core.utils.App;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KFrame extends JFrame implements Preference {
    public static final List<KFrame> ALL_FRAMES = new ArrayList<>();


    public KFrame(String title){
        super(title);
        setPreferences();
    }

    /**
     * Gets the icon used by frames and dialogs.
     * Native systems use this as a launcher icon.
     */
    public static Image getIcon() {
        final URL iPath = App.getIconURL("dashboard.png");
        return Toolkit.getDefaultToolkit().getImage(iPath);
    }

    @Override
    public void setPreferences() {
        setIconImage(getIcon());
        ALL_FRAMES.add(this);
    }

}
