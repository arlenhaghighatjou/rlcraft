package ivorius.reccomplex.gui.table.cell;

public interface TableCellProperty<P> extends TableCell {
   P getPropertyValue();

   void setPropertyValue(P var1);
}
