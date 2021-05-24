package proto;

import javax.swing.*;
import java.awt.event.ActionListener;

public class KMenuItem extends JMenuItem implements Preference {

    public KMenuItem(String text){
        super(text);
        setPreferences();
    }

    public KMenuItem(String text, ActionListener action){
        this(text);
        addActionListener(action);
    }

    @Override
    public void setPreferences() {
        setFont(KFontFactory.createPlainFont(15));
    }

}
