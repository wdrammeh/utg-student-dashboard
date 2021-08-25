package proto;

import core.utils.KComponent;

import javax.swing.*;

public class KCheckBox extends JCheckBox implements Preference {

    public KCheckBox(String text){
        super(text);
        setPreferences();
    }

    public KCheckBox(String text, boolean selected){
        super(text, selected);
        setPreferences();
    }

    @Override
    public JToolTip createToolTip(){
        return KComponent.preferredTip();
    }

    @Override
    public void setPreferences() {
    }

}
