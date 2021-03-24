package proto;

import javax.swing.table.DefaultTableModel;

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
     * Gets the row-index of the row (more formally, the first row found) holding this string
     * in its first column.
     * A return of -1 signals absence of such a row in this model.
     * This call is case-insensitive.
     */
    public int getRowOf(String key) {
        for (int i = 0; i < getRowCount(); i++) {
            if (key.equalsIgnoreCase(String.valueOf(getValueAt(i, 0)))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public void setPreferences() {
    }

}
