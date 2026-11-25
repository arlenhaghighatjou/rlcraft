package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazePathConnection extends TableDataSourceSegmented {
   public TableDataSourceMazePathConnection(SavedMazePathConnection mazePath, Selection bounds, MazeVisualizationContext visualizationContext, TableDelegate tableDelegate, TableNavigator navigator) {
      this.addSegment(0, new TableDataSourceConnector(mazePath.connector, IvTranslations.get("reccomplex.maze.connector")));
      this.addSegment(1, TableCellMultiBuilder.create(navigator, tableDelegate).addNavigation(() -> {
         return new TableDataSourceMazePathConditionalConnectorList(mazePath.conditionalConnectors, tableDelegate, navigator);
      }, () -> {
         return IvTranslations.get("reccomplex.maze.conditional_connectors");
      }, () -> {
         return IvTranslations.getLines("reccomplex.maze.conditional_connectors.tooltip");
      }).withTitle("").buildDataSource());
      this.addSegment(2, (new TableDataSourceMazePath(mazePath.path, bounds, tableDelegate, navigator)).visualizing(visualizationContext));
   }

   @Nonnull
   public String title() {
      return "Path";
   }
}
