package ivorius.reccomplex.gui.table.datasource;

import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.cell.TableCell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceSupplied implements TableDataSource {
   public final List<Supplier<TableCell>> cells = new ArrayList();

   @SafeVarargs
   public TableDataSourceSupplied(Supplier<TableCell>... cells) {
      Collections.addAll(this.cells, cells);
   }

   public TableDataSourceSupplied(List<Supplier<TableCell>> cells) {
      this.cells.addAll(cells);
   }

   public List<Supplier<TableCell>> getCells() {
      return this.cells;
   }

   public void setCells(List<Supplier<TableCell>> cells) {
      this.cells.clear();
      this.cells.addAll(cells);
   }

   public int numberOfCells() {
      return this.cells.size();
   }

   public TableCell cellForIndex(GuiTable table, int index) {
      return (TableCell)((Supplier)this.cells.get(index)).get();
   }
}
