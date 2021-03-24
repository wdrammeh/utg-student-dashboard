package proto;

import core.MComponent;

import javax.swing.*;

public class KComboBox<E> extends JComboBox<E> implements Preference {


    public KComboBox(E[] items){
        super(items);
        setPreferences();
    }

    @Override
    public JToolTip createToolTip() {
        return MComponent.preferredTip();
    }

    @Override
    public void setPreferences() {
        setFont(KFontFactory.createPlainFont(15));
        setCursor(MComponent.HAND_CURSOR);
        setFocusable(false);
    }

}
