package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazePathConditionalConnectorList extends TableDataSourceList<SavedMazePathConnection.ConditionalConnector, List<SavedMazePathConnection.ConditionalConnector>> {
   public TableDataSourceMazePathConditionalConnectorList(List<SavedMazePathConnection.ConditionalConnector> list, TableDelegate tableDelegate, TableNavigator navigator) {
      super(list, tableDelegate, navigator);
   }

   public String getDisplayString(SavedMazePathConnection.ConditionalConnector conditionalConnector) {
      return conditionalConnector.connector.id;
   }

   public SavedMazePathConnection.ConditionalConnector newEntry(String actionID) {
      return new SavedMazePathConnection.ConditionalConnector("", "Path");
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, SavedMazePathConnection.ConditionalConnector conditionalConnector) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return new TableDataSourceConditionalConnector(conditionalConnector);
      });
   }

   @Nonnull
   public String title() {
      return "Conditional Connectors";
   }
}
