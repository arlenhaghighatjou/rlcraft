package ivorius.reccomplex.gui.table.datasource;

import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.cell.TableCell;
import javax.annotation.Nonnull;

public interface TableDataSource {
   @Nonnull
   default String title() {
      return "";
   }

   int numberOfCells();

   TableCell cellForIndex(GuiTable var1, int var2);

   default boolean canVisualize() {
      return false;
   }

   default GuiHider.Visualizer visualizer() {
      return null;
   }
}
