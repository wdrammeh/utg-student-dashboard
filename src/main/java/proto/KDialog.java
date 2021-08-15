package proto;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * The standard Dashboard Dialog all dialogs must inherit.
 * By default, dialogs are not resizable.
 * @see #setPreferences()
 */
public class KDialog extends JDialog implements Preference {
    public static final List<KDialog> ALL_DIALOGS = new ArrayList<>();


    public KDialog(){
        super();
        setPreferences();
    }

    public KDialog(String title){
        this();
        setTitle(title);
    }

    public void setPreferences(){
        setIconImage(KFrame.getIcon());
        setResizable(false);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        ALL_DIALOGS.add(this);
    }

}
