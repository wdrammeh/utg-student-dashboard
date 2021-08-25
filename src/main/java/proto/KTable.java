package proto;

import core.utils.App;
import core.utils.KComponent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * This type works hand-in-hand with the KTableModel type, and share their
 * mutual-functionality.
 */
public class KTable extends JTable implements Preference {

    public KTable(KTableModel kTableModel){
        super();
        setModel(kTableModel);
        kTableModel.setTable(this);
        setPreferences();
    }

    /**
     * Returns a scroll-pane that fits itself to the size of this instance
     * by calculating its size.
     * And, as rows are added and removed from this table's model,
     * it adjusts its size accordingly.
     * @see #getProperSize()
     */
    public KScrollPane sizeMatchingScrollPane() {
        final KScrollPane scrollPane = new KScrollPane(this);
        scrollPane.setPreferredSize(getProperSize());
        getModel().addTableModelListener(tableModelEvent-> {
            scrollPane.setPreferredSize(getProperSize());
            KComponent.ready(scrollPane.getParent());
        });
        return scrollPane;
    }

    /**
     * Gets the approximate size of this table, including its header.
     */
    public Dimension getProperSize() {
        final int width = getPreferredSize().width;
        int height = getPreferredSize().height + 3;
        height += getTableHeader().getPreferredSize().height;
        return new Dimension(width, height);
    }

    /**
     * Sets the height of the header of this instance.
     */
    public void setHeaderHeight(int height) {
        getTableHeader().setPreferredSize(new Dimension(getPreferredSize().width, height));
    }

    /**
     * Where columns is a list of the columns in this table to be center-aligned.
     */
    public void centerAlignColumns(int... columns){
        final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        for (int c : columns) {
            try {
                getColumnModel().getColumn(c).setCellRenderer(centerRenderer);
            } catch (Exception e) {
                App.silenceException(e);
            }
        }
    }

    /**
     * Center-align all the columns of this table.
     */
    public void centerAlignAllColumns(){
        for (int i = 0; i < getColumnCount(); i++) {
            final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            try {
                getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            } catch (Exception e) {
                App.silenceException(e);
            }
        }
    }

    @Override
    public void setPreferences(){
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

}
