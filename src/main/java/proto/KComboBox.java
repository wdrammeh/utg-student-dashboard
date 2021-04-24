package proto;

import core.utils.MComponent;

import javax.swing.*;
import java.util.Objects;

public class KComboBox<E> extends JComboBox<E> implements Preference {
    /**
     * Refers to an element in the model,
     * such that {@link #getSelectionText()}
     * should not return its string value.
     * Precisely, this is usually a descriptively default class property
     * used by this instance, and selection of such property signifies
     * no selection, hence the {@link #value} should be returned
     * as the property in-place.
     * For e.g. some fields of {@link core.module.Course}: day, time, etc.
     * This allows such fields to be presented indirectly.
     */
    private E mask;
    private E value;


    public KComboBox(E[] items){
        super(items);
        setPreferences();
    }

    @Override
    public JToolTip createToolTip() {
        return MComponent.preferredTip();
    }

    /**
     * Sets the exception with its alternative for this model.
     * This instance will now set selection to this exception element.
     */
    public void setMask(E except, E alt) {
        this.mask = except;
        this.value = alt;
        this.setSelectedItem(mask);
    }

    /**
     * Returns a string representation of whatever element
     * is currently selected from this comboBox.
     * If this instance has specifies a mask,
     * and the selected item is such, then the alternative
     * (actual) value ({@link #value}) will be returned instead.
     */
    public String getSelectionText() {
        final Object selection = getSelectedItem();
        if (Objects.equals(selection, mask)) {
            return String.valueOf(value);
        } else {
            return String.valueOf(selection);
        }
    }

    @Override
    public void setPreferences() {
        setFont(KFontFactory.createPlainFont(15));
        setCursor(MComponent.HAND_CURSOR);
    }

}
