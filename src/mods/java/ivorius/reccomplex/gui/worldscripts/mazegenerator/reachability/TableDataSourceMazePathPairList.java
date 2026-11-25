package ivorius.reccomplex.gui.worldscripts.mazegenerator.reachability;

import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceMulti;
import ivorius.reccomplex.gui.table.datasource.TableDataSourcePreloaded;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.TableDataSourceMazePath;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazePathPairList extends TableDataSourceList<ImmutablePair<SavedMazePath, SavedMazePath>, List<ImmutablePair<SavedMazePath, SavedMazePath>>> {
   private Selection bounds;
   protected MazeVisualizationContext visualizationContext;

   public TableDataSourceMazePathPairList(List<ImmutablePair<SavedMazePath, SavedMazePath>> list, TableDelegate tableDelegate, TableNavigator navigator, Selection bounds) {
      super(list, tableDelegate, navigator);
      this.bounds = bounds;
   }

   public TableDataSourceMazePathPairList visualizing(MazeVisualizationContext context) {
      this.visualizationContext = context;
      return this;
   }

   public TableDelegate getTableDelegate() {
      return this.tableDelegate;
   }

   public void setTableDelegate(TableDelegate tableDelegate) {
      this.tableDelegate = tableDelegate;
   }

   public String getDisplayString(ImmutablePair<SavedMazePath, SavedMazePath> pair) {
      return pair.toString();
   }

   public ImmutablePair<SavedMazePath, SavedMazePath> newEntry(String actionID) {
      return ImmutablePair.of(new SavedMazePath(0, new MazeRoom(new int[]{0, 0, 0}), true), new SavedMazePath(0, new MazeRoom(new int[]{0, 0, 0}), false));
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, ImmutablePair<SavedMazePath, SavedMazePath> pair) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return new TableDataSourceMulti(new TableDataSource[]{new TableDataSourcePreloaded(new TableCell[]{new TitledCell(new TableCellTitle("", IvTranslations.get("reccomplex.gui.source")))}), new TableDataSourceMazePath((SavedMazePath)pair.getLeft(), this.bounds, this.tableDelegate, this.navigator), new TableDataSourcePreloaded(new TableCell[]{new TitledCell(new TableCellTitle("", IvTranslations.get("reccomplex.gui.destination")))}), new TableDataSourceMazePath((SavedMazePath)pair.getRight(), this.bounds, this.tableDelegate, this.navigator)}) {
            public boolean canVisualize() {
               return TableDataSourceMazePathPairList.this.visualizationContext != null;
            }

            public GuiHider.Visualizer visualizer() {
               return TableDataSourceMazePath.visualizePaths(TableDataSourceMazePathPairList.this.visualizationContext, Arrays.asList((SavedMazePath)pair.left, (SavedMazePath)pair.right));
            }
         };
      });
   }

   @Nonnull
   public String title() {
      return "Connections";
   }

   public boolean canVisualize() {
      return this.visualizationContext != null;
   }

   public GuiHider.Visualizer visualizer() {
      return TableDataSourceMazePath.visualizePaths(this.visualizationContext, (Collection)this.list.stream().flatMap((p) -> {
         return Stream.of((SavedMazePath)p.left, (SavedMazePath)p.right);
      }).collect(Collectors.toSet()));
   }
}
