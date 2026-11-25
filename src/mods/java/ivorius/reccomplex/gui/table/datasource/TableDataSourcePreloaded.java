package ivorius.reccomplex.gui.table.datasource;

import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.cell.TableCell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourcePreloaded implements TableDataSource {
   public final List<TableCell> cells = new ArrayList();

   public TableDataSourcePreloaded(TableCell... cells) {
      Collections.addAll(this.cells, cells);
   }

   public TableDataSourcePreloaded(List<TableCell> cells) {
      this.cells.addAll(cells);
   }

   public List<TableCell> getCells() {
      return this.cells;
   }

   public void setCells(List<TableCell> cells) {
      this.cells.clear();
      this.cells.addAll(cells);
   }

   public int numberOfCells() {
      return this.cells.size();
   }

   public TableCell cellForIndex(GuiTable table, int index) {
      return (TableCell)this.cells.get(index);
   }
}
