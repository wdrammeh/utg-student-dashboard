package proto;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;


public class KPopupMenu extends JPopupMenu implements Preference {
    public static final List<KPopupMenu> POPUP_MENUS = new ArrayList<>();


    public KPopupMenu() {
        super();
        this.setPreferences();
    }

    @Override
    public void setPreferences() {
        POPUP_MENUS.add(this);
    }
    
}
