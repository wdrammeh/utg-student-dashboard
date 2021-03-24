package proto;

import core.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The KPanel types is the number one building block of Dashboard's UI.
 * It can be constructed in a vast different ways, allowing direct addition of components.
 * It is a strict convention that top-level containers set their content-panes to a KPanel
 * instance for making theme changes easier and overall effective.
 */
public class KPanel extends JPanel implements Preference {
    /**
     * Decides whether this panel will reflect background color of the theme.
     * If not, then this panel has a permanent background color.
     */
    private boolean reflectTheme;
    public static final List<KPanel> ALL_PANELS = new ArrayList<>();


    public KPanel(){
        super();
        setPreferences();
    }

    /**
     * Constructs a panel with the given width and height as its preferred size.
     */
    public KPanel(int width, int height){
        this();
        setPreferredSize(new Dimension(width, height));
    }

    public KPanel(LayoutManager layout) {
        super(layout);
        setPreferences();
    }

    public KPanel(LayoutManager layout, Dimension dimension) {
        this(layout);
        setPreferredSize(dimension);
    }

    /**
     * Creates a panel with the specified layout, and add all the given components.
     * Notice, if it's more than one component, then this is not ideal for the border-layout.
     */
    public KPanel(LayoutManager layout, Component... components) {
        this(layout);
        addAll(components);
    }

    /**
     * Constructs a panel with the default layout, and add the given components.
     */
    public KPanel(Component... components){
        this();
        addAll(components);
    }

    /**
     * Constructs a panel with the preferred-size of the given dimension.
     * The list of components will be added, and it's layout is the default hence.
     */
    public KPanel(Dimension dimension, Component... components) {
        this(components);
        setPreferredSize(dimension);
    }

    public KPanel(LayoutManager layout, Dimension dimension, Component... components){
        this(layout, dimension);
        addAll(components);
    }

    /**
     * Directly adds a list of components to this instance.
     * Since this function pays no heed to position,
     * it cannot technically be used under certain layouts;
     * especially, the beloved Border. However, very useful under certain other
     * beloved layouts like Flow, Box, and Grid.
     */
    public void addAll(Component... list) {
        for (Component c : list) {
            add(c);
        }
    }

    /**
     * Removes all the given components from this instance.
     * This is intended for removing only specific children.
     * If you intend to cleanse this panel, then consider {@link main.MComponent#empty(Container...)}
     */
    public void removeAll(Component... list){
        for (Component c : list) {
            remove(c);
        }
    }

    /**
     * Removes the last component from this panel's children.
     * This does nothing if there are no children in its hierarchy.
     */
    public void removeLast() {
        final int count = getComponentCount();
        if (count >= 1) {
            remove(count - 1);
        }
    }

    /**
     * Adds the given component, second to last, on this panel.
     * If this panel contains no children prior to this call,
     * then this call is effectively equivalent to a add(Component)
     */
    public Component addPenultimate(Component component){
        final int count = getComponentCount();
        if (count >= 1) {
            return add(component, count - 1);
        } else {
            return add(component);
        }
    }

    /**
     * Sets whether this instance will be affected by theme changes.
     */
    public void setReflectTheme(boolean reflectTheme){
        this.reflectTheme = reflectTheme;
    }

    public boolean getReflectTheme(){
        return reflectTheme;
    }

    public void setPreferences(){
        setBackground(Settings.currentBackground());
        reflectTheme = true;
        ALL_PANELS.add(this);
    }

}
