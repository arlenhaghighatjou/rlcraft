package ivorius.reccomplex.gui.worldscripts.mazegenerator.reachability;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourcePreloaded;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeReachability;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazeReachability extends TableDataSourceSegmented {
   protected SavedMazeReachability reachability;
   private TableDelegate tableDelegate;
   private TableNavigator tableNavigator;

   public TableDataSourceMazeReachability(SavedMazeReachability reachability, MazeVisualizationContext visualizationContext, TableDelegate tableDelegate, TableNavigator tableNavigator, Set<SavedMazePath> expected, Selection bounds) {
      this.reachability = reachability;
      this.tableDelegate = tableDelegate;
      this.tableNavigator = tableNavigator;
      this.addSegment(0, new TableDataSourcePreloaded(new TableCell[]{(new TitledCell(new TableCellTitle("", IvTranslations.get("reccomplex.reachability.groups")))).withTitleTooltip(IvTranslations.formatLines("reccomplex.reachability.groups.tooltip", new Object[0]))}));
      this.addSegment(1, new TableDataSourceMazeReachabilityGrouping(reachability, expected, tableDelegate, tableNavigator));
      this.addSegment(2, new TableDataSourcePreloaded(new TableCell[]{(new TitledCell(new TableCellTitle("", IvTranslations.get("reccomplex.reachability.crossconnections")))).withTitleTooltip(IvTranslations.formatLines("reccomplex.reachability.crossconnections.tooltip", new Object[0]))}));
      this.addSegment(3, (new TableDataSourceMazePathPairList(reachability.crossConnections, tableDelegate, tableNavigator, bounds)).visualizing(visualizationContext));
   }

   public SavedMazeReachability getReachability() {
      return this.reachability;
   }

   public void setReachability(SavedMazeReachability reachability) {
      this.reachability = reachability;
   }

   public TableDelegate getTableDelegate() {
      return this.tableDelegate;
   }

   public void setTableDelegate(TableDelegate tableDelegate) {
      this.tableDelegate = tableDelegate;
   }

   public TableNavigator getTableNavigator() {
      return this.tableNavigator;
   }

   public void setTableNavigator(TableNavigator tableNavigator) {
      this.tableNavigator = tableNavigator;
   }

   @Nonnull
   public String title() {
      return "Reachability";
   }
}
