package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedConnector;
import java.util.function.Consumer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceConnector implements TableDataSource {
   public SavedConnector connector;
   public String title;

   public TableDataSourceConnector(SavedConnector connector, String title) {
      this.connector = connector;
      this.title = title;
   }

   public int numberOfCells() {
      return 1;
   }

   public TableCell cellForIndex(GuiTable table, int index) {
      TableCellString cell = new TableCellString("connectorID", this.connector.id);
      cell.addListener((val) -> {
         this.connector.id = val;
      });
      return (new TitledCell(this.title, cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.maze.connector.tooltip", new Object[0]));
   }
}
