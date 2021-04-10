package proto;

import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The standard Dashboard text-field. One of the proto types.
 * Note that control-fields suspend any form of pasting.
 */
public class KTextField extends JTextField implements Preference {


    public KTextField(){
        super();
        setPreferences();
    }

    /**
     * Constructs a text-field; loaded in it is the given initialText.
     */
    public KTextField(String initialText){
	    super(initialText);
	    setPreferences();
    }

    public KTextField(Dimension preferredSize){
        this();
        setPreferredSize(preferredSize);
    }

    /**
     * Provides a field that accepts all input (like any other normal instance) until the
     * given limit of values are met.
     */
    public static KTextField rangeControlField(int limit) {
        final KTextField field = new KTextField() {
            @Override
            public void paste() {
                UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
        };
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (field.getText().length() >= limit) {
                    e.consume();
                }
            }
        });
        return field;
    }

    /**
     * Provides a {@link #rangeControlField(int)} that restricts its input to only numbers.
     * @see #rangeControlField(int)
     */
    public static KTextField digitRangeControlField(int limit) {
        final KTextField field = new KTextField(){
            @Override
            public void paste() {
                UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
        };
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                final char keyChar = e.getKeyChar();
                if (field.getText().length() >= limit || !Character.isDigit(keyChar) &&
                        !(keyChar == KeyEvent.VK_DELETE || keyChar == KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });
        return field;
    }

    /**
     * Creates a field intended for holding day values.
     */
    public static KTextField dayField(){
        final KTextField dayField = digitRangeControlField(2);
        dayField.setPreferredSize(new Dimension(50, 30));
        return dayField;
    }

    /**
     * Creates a field intended for holding month values.
     * @see #dayField()
     */
    public static KTextField monthField(){
        return dayField();
    }

    /**
     * Creates a field intended for holding year values.
     * Unlike the other, loaded in this is the current year.
     */
    public static KTextField yearField(){
        final KTextField yearField = digitRangeControlField(4);
        yearField.setPreferredSize(new Dimension(75, 30));
        yearField.setText(String.valueOf(MDate.currentYear()));
        return yearField;
    }

    /**
     * Checks whether this field has any text as defined by {@link Globals#hasText(String)}.
     */
    public boolean hasText() {
        return Globals.hasText(getText());
    }

    /**
     * Checks whether this field has no text as defined by {@link Globals#hasNoText(String)}.
     */
    public boolean isBlank() {
        return Globals.hasNoText(getText());
    }

    public void setText(int n) {
        setText(Integer.toString(n));
    }

    @Override
    public JToolTip createToolTip() {
        return MComponent.preferredTip();
    }

    public void setPreferences() {
        setFont(KFontFactory.createPlainFont(15));
        setHorizontalAlignment(SwingConstants.CENTER);
        setAutoscrolls(true);
    }

}
