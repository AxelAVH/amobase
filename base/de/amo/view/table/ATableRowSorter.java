package de.amo.view.table;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;

/**
 * Created by private on 19.01.2016.
 */
public class ATableRowSorter extends TableRowSorter {

    ATableModel aTableModel;

//    public ATableRowSorter() {
//    }

//    public ATableRowSorter(ATableModel model) {
//        super(model);
//        aTableModel = model;
//    }

    @Override
    public void setModel(TableModel model) {
        super.setModel(model);
        aTableModel = (ATableModel) model;
    }

    @Override
    public Comparator<?> getComparator(int column) {
        Class columnClass = aTableModel.getColumnClass(column);

        if (columnClass == Integer.class) {
            return new IntegerComperator();
        }

        if (columnClass == Double.class) {
            return new DoubleComperator();
        }

        if (columnClass == String.class) {
            return new StringComperator();
        }
        return null;
    }

    class IntegerComperator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {

            Integer i1 = (int) o1;
            Integer i2 = (int) o2;

            return i1.compareTo(i2);
        }
    }

    class DoubleComperator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {

            Double d1 = (double) o1;
            Double d2 = (double) o2;

            return d1.compareTo(d2);
        }
    }

    class StringComperator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {

            String s1 = (String) o1;
            String s2 = (String) o2;

            return s1.compareTo(s2);
        }
    }
}
