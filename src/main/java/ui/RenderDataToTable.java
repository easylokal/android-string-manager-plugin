package ui;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

class RenderDataToTable extends DefaultTableModel {

    private List<HardcodedStringEntryModel> entries = new ArrayList<HardcodedStringEntryModel>();
    private String[] colNames = {"key", "Value", "Add to String.xml"};
    private Class<?>[] colClasses = {String.class, String.class, Boolean.class};

    public RenderDataToTable(List<HardcodedStringEntryModel> entries) {
        this.entries = entries;
        this.fireTableDataChanged();
    }

    public int getRowCount() {
        if (entries != null)
            return entries.size();
        else
            return 0;
    }


    public int getColumnCount() {
        return colNames.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return entries.get(rowIndex).getKey();
        }
        if (columnIndex == 1) {
            return entries.get(rowIndex).getValue();
        }
        if (columnIndex == 2) {
            return entries.get(rowIndex).isSelected();
        }
        return null;
    }

    public String getColumnName(int columnIndex) {
        return colNames[columnIndex];
    }

    public Class<?> getColumnClass(int columnIndex) {
        return colClasses[columnIndex];
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            entries.get(rowIndex).setKey((String) aValue);
        }
        if (columnIndex == 1) {
            entries.get(rowIndex).setValue((String) aValue);
        }
        if (columnIndex == 2) {
            entries.get(rowIndex).setSelected((Boolean) aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
