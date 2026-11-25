package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.client.rendering.SelectionQuadCache;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import java.util.Arrays;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceSelection extends TableDataSourceList<Selection.Area, Selection> {
   private int[] dimensions;
   protected boolean showIdentifier;
   protected MazeVisualizationContext visualizationContext;

   public TableDataSourceSelection(Selection list, int[] dimensions, TableDelegate tableDelegate, TableNavigator navigator, boolean showIdentifier) {
      super(list, tableDelegate, navigator);
      this.dimensions = dimensions;
      this.showIdentifier = showIdentifier;
      this.duplicateTitle = TextFormatting.GREEN + "D";
   }

   public TableDataSourceSelection visualizing(MazeVisualizationContext visualizationContext) {
      this.visualizationContext = visualizationContext;
      return this;
   }

   public String getDisplayString(Selection.Area area) {
      TextFormatting color = area.isAdditive() ? TextFormatting.GREEN : TextFormatting.RED;
      return String.format("%s%s%s - %s%s", color, Arrays.toString(area.getMinCoord()), TextFormatting.RESET, color, Arrays.toString(area.getMaxCoord()));
   }

   public Selection.Area newEntry(String actionID) {
      return new Selection.Area(true, new int[this.dimensions.length], new int[this.dimensions.length], this.showIdentifier ? "" : null);
   }

   public Selection.Area copyEntry(Selection.Area area) {
      return area.copy();
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, Selection.Area area) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return (new TableDataSourceSelectionArea(area, this.dimensions, this.showIdentifier)).visualizing(this.visualizationContext);
      });
   }

   @Nonnull
   public String title() {
      return "Area List";
   }

   public boolean canVisualize() {
      return this.visualizationContext != null;
   }

   public GuiHider.Visualizer visualizer() {
      return new SelectionQuadCache.Visualizer((Selection)this.list, this.visualizationContext);
   }
}
