package proto;

import core.utils.MComponent;

import javax.swing.*;
import java.util.HashMap;

public class KComboBox<E> extends JComboBox<E> implements Preference {
    private HashMap<String, Object> masks;


    public KComboBox(E[] items){
        super(items);
        masks = new HashMap<>();
        setPreferences();
    }

    public KComboBox(E[] items, int i){
        this(items);
        if (i == -1) {
            setSelectedIndex(getItemCount() - 1);
        } else {
            setSelectedIndex(i);
        }
    }

    public void addMask(E mask, Object value) {
        masks.put(String.valueOf(mask), value);
    }

    /**
     * Returns a string representation of whatever element
     * is currently selected from this comboBox.
     * If this instance has specified a mask for the item,
     * and the selected item is such, then the alternative
     * (actual) value will be returned instead.
     */
    public String getSelectionText() {
        final Object selection = getSelectedItem();
        final String selectionText = String.valueOf(selection);
        if (masks.containsKey(selectionText)) {
            return String.valueOf(masks.get(selectionText));
        } else {
            return selectionText;
        }
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
