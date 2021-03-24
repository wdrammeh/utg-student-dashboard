package proto;

import javax.swing.*;
import java.awt.*;

public class KSeparator extends JSeparator implements Preference {


    public KSeparator(Dimension dimension) {
        super();
        setPreferences();
        setPreferredSize(dimension);
    }

    public KSeparator(Color foreground){
        super();
        setPreferences();
        setForeground(foreground);
    }

    public KSeparator(int orientation){
        super(orientation);
        setPreferences();
    }

    public KSeparator(Dimension size, Color foreground, int orientation){
        this(size);
        setOrientation(orientation);
        setForeground(foreground);
    }

    public KSeparator(int orientation, Color foreground){
        this(orientation);
        setForeground(foreground);
    }

    public KSeparator(Dimension dimension, Color foreground){
        this(dimension);
        setForeground(foreground);
    }

    @Override
    public void setPreferences() {
        setOpaque(false);
    }

}
