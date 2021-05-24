package proto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentListener;

/**
 * The standard Dashboard ScrollPane. Remarkable container for swing components.
 * This class is unique in its constructors and other useful static methods embedded.
 */
public class KScrollPane extends JScrollPane implements Preference {

    public KScrollPane(Component insider){
        super(insider);
        setPreferences();
    }

    public KScrollPane(Component insider, Dimension size){
        this(insider);
        setPreferredSize(size);
    }

    /**
     * Creates a scrollPane to which is assigned the job of scrolling itself
     * to the bottom anytime changes is made to the adjustable.
     * Such changes may even be user-triggered, so any attempt to physically
     * scroll the vertical bar results in it pushing itself to the bottom.
     * Therefore, it's recommended to stop such spontaneous behaviour once work
     * is done by calling {@link #stopAutoScrolling()}
     * @see #stopAutoScrolling()
     */
    public static KScrollPane getAutoScroller(JComponent c){
        final KScrollPane scrollPane = new KScrollPane(c);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e-> {
            final Adjustable adjustable = e.getAdjustable();
            adjustable.setValue(adjustable.getMaximum());
        });
        return scrollPane;
    }

    /**
     * Removes the AdjustmentListener on the scrollPane.
     * So it no longer pushes itself to the bottom on change events.
     */
    public void stopAutoScrolling(){
        final JScrollBar verticalBar = getVerticalScrollBar();
        final AdjustmentListener[] adjustmentListeners = verticalBar.getAdjustmentListeners();
        verticalBar.removeAdjustmentListener(adjustmentListeners[0]);
    }

    /**
     * Pushes the vertical bar to the top-most.
     * This method has no effect if the component is not visible.
     * @see #toBottom()
     */
    public void toTop(){
        getVerticalScrollBar().setValue(0);
    }

    /**
     * Pushes the vertical bar to the bottom.
     * This method has no effect if the component is not visible.
     * @see #toTop()
     */
    public void toBottom(){
        getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
    }

    public void setPreferences(){
    }

}
