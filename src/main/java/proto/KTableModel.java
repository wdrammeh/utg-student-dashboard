package proto;

import javax.swing.table.DefaultTableModel;
import java.util.function.Function;

/**
 * This is an extension of javax.swing.table.DefaultTableModel.
 * These are course oriented, since, virtually, all the tables deal with courses.
 */
public class KTableModel extends DefaultTableModel implements Preference {
    private KTable table;


    public KTableModel(){
        super();
    }

    public KTableModel(Object[] columns){
        super(columns, 0);
    }

    public KTable getTable(){
        return table;
    }

    public void setTable(KTable kTable){
        this.table = kTable;
    }

    public int getSelectedRow(){
        return table.getSelectedRow();
    }

    /**
     * Checking only the first column, returns the index of the row
     * (more formally, the first row found) having the given key.
     * This call is case-insensitive.
     */
    public int getRow(String key) {
        for (int i = 0; i < getRowCount(); i++) {
            final String cell = String.valueOf(getValueAt(i, 0));
            if (cell.equalsIgnoreCase(key)) {
                return i;
            }
        }
        return -1;
    }

    public String getSelectedId() {
        final int selectedRow = getSelectedRow();
        if (selectedRow >= 0) {
            return String.valueOf(getValueAt(selectedRow, 0));
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public void setPreferences() {
    }

}
