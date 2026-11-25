package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazePathConnectionList extends TableDataSourceList<SavedMazePathConnection, List<SavedMazePathConnection>> {
   private Selection bounds;
   protected MazeVisualizationContext visualizationContext;

   public TableDataSourceMazePathConnectionList(List<SavedMazePathConnection> list, TableDelegate tableDelegate, TableNavigator navigator, Selection bounds) {
      super(list, tableDelegate, navigator);
      this.bounds = bounds;
      this.duplicateTitle = TextFormatting.GREEN + "D";
   }

   public TableDataSourceMazePathConnectionList visualizing(MazeVisualizationContext context) {
      this.visualizationContext = context;
      return this;
   }

   public String getDisplayString(SavedMazePathConnection mazePath) {
      return String.format("%s %s%s%s", Arrays.toString(mazePath.path.sourceRoom.getCoordinates()), TextFormatting.BLUE, TableDataSourceMazePath.directionFromPath(mazePath.path).toString(), TextFormatting.RESET);
   }

   public SavedMazePathConnection newEntry(String actionID) {
      return new SavedMazePathConnection(2, new MazeRoom(new int[this.bounds.dimensions]), false, "Path", Collections.emptyList());
   }

   public SavedMazePathConnection copyEntry(SavedMazePathConnection mazePath) {
      return mazePath.copy();
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, SavedMazePathConnection savedMazePathConnection) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return new TableDataSourceMazePathConnection(savedMazePathConnection, this.bounds, this.visualizationContext, this.tableDelegate, this.navigator);
      });
   }

   @Nonnull
   public String title() {
      return "Paths";
   }

   public boolean canVisualize() {
      return this.visualizationContext != null;
   }

   public GuiHider.Visualizer visualizer() {
      return TableDataSourceMazePath.visualizePaths(this.visualizationContext, Lists.transform(this.list, (p) -> {
         return p.path;
      }));
   }
}
