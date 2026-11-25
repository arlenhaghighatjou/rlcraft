package ivorius.reccomplex.gui.table;

import java.util.Stack;

public interface TableNavigator {
   GuiTable popTable();

   void pushTable(GuiTable var1);

   void setTable(GuiTable var1);

   GuiTable currentTable();

   Stack<GuiTable> tableStack();
}
