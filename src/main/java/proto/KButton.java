package proto;

import core.utils.App;
import core.utils.MComponent;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * The standard Dashboard Button.
 * It is a convention that buttons modify their toolTips based on their states
 * (on or off) as appropriate.
 * In a future release, buttons that are assign long-tasks will trigger
 * progress icons, while carrying out such tasks.
 */
public class KButton extends JButton implements Preference {
    private String initialTip;


    /**
     * Constructs a new button with no text, or icon.
     * All Dashboard Buttons are not focusable by default.
     * @see #setPreferences()
     */
    public KButton(){
        super();
        setPreferences();
    }

    /**
     * Constructs a button with the specified text.
     */
    public KButton(String text){
        super(text);
        setPreferences();
    }

    /**
     * Constructs an iconified button; which, by default or under most UIs, is "dressed".
     * Since most Dashboard icons are scaled and undressed, this is not the preferred Dashboard call.
     * Use {@link #createIconifiedButton(String, int, int)} instead.
     */
    public KButton(Icon icon){
        super(icon);
        setPreferences();
    }

    /**
     * Creates an iconified button scaled to the given width and height.
     * The given name must be simple, and an existing file in the icons dir.
     * Instances created through this call are undressed-set before returning.
     * @see MComponent#scaleIcon(URL, int, int)
     * @see App#getIconURL(String)
     */
    public static KButton createIconifiedButton(String name, int width, int height){
        final KButton button = new KButton(MComponent.scaleIcon(App.getIconURL(name), width, height));
        button.undress();
        return button;
    }

    public void setStyle(Font font, Color foreground){
        setFont(font);
        setForeground(foreground);
    }

    /**
     * Invoked to force "undressing" on buttons that may not have been constructed with
     * the {@link #createIconifiedButton(String, int, int)}
     * @see #redress()
     */
    public void undress(){
        setBorderPainted(false);
        setContentAreaFilled(false);
    }

    /**
     * Redresses this instance.
     * A button is said to be dressed if it paints its border and fill its content-area.
     * @see #undress()
     */
    public void redress(){
        setBorderPainted(true);
        setContentAreaFilled(true);
    }

    public void setText(int n){
        setText(Integer.toString(n));
    }

    public void setToolTipText(int n){
        setToolTipText(Integer.toString(n));
    }

    /**
     * Sets the state of this button.
     * If tool-tip will be assigned accordingly.
     */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        setToolTipText(b ? initialTip : null);
    }

    /**
     * Sets the tool-tip of this button.
     * This will also update the initialTip if its not null.
     */
    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        if (text != null) {
            initialTip = text;
        }
    }

    @Override
    public JToolTip createToolTip(){
        return MComponent.preferredTip();
    }

    @Override
    public void setPreferences(){
        setFocusable(false);
    }

}
