package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.client.rendering.SelectionQuadCache;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellIntegerRange;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceSelectionArea extends TableDataSourceSegmented {
   public static final String[] COORD_NAMES = new String[]{"x", "y", "z"};
   private Selection.Area area;
   private int[] dimensions;
   private boolean showIdentifier;
   protected MazeVisualizationContext visualizationContext;

   public TableDataSourceSelectionArea(Selection.Area area, int[] dimensions, boolean showIdentifier) {
      this.area = area;
      this.dimensions = dimensions;
      this.showIdentifier = showIdentifier;
   }

   public TableDataSourceSelectionArea visualizing(MazeVisualizationContext visualizationContext) {
      this.visualizationContext = visualizationContext;
      return this;
   }

   @Nonnull
   public String title() {
      return "Area";
   }

   public int numberOfSegments() {
      return this.showIdentifier ? 3 : 2;
   }

   public int sizeOfSegment(int segment) {
      switch(segment) {
      case 0:
         return 1;
      case 1:
         return COORD_NAMES.length;
      case 2:
         return 1;
      default:
         return super.sizeOfSegment(segment);
      }
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      if (segment == 0) {
         TableCellBoolean cell = new TableCellBoolean("additive", this.area.isAdditive(), TextFormatting.GREEN + IvTranslations.get("reccomplex.selection.area.additive"), TextFormatting.GOLD + IvTranslations.get("reccomplex.selection.area.subtractive"));
         Selection.Area var9 = this.area;
         this.area.getClass();
         cell.addListener(var9::setAdditive);
         return new TitledCell(cell);
      } else if (segment == 1) {
         String title = IvTranslations.get("reccomplex.selection.area.range." + COORD_NAMES[index]);
         IntegerRange intRange = new IntegerRange(this.area.getMinCoord()[index], this.area.getMaxCoord()[index]);
         TableCellIntegerRange cell = new TableCellIntegerRange("area" + index, intRange, 0, this.dimensions[index] - 1);
         cell.addListener((val) -> {
            this.area.setCoord(index, val.getMin(), val.getMax());
         });
         return (new TitledCell(title, cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.selection.area.range." + COORD_NAMES[index] + ".tooltip"));
      } else if (segment == 2) {
         TableCellString cell = new TableCellString("", this.area.getIdentifier() != null ? this.area.getIdentifier() : "");
         Selection.Area var10001 = this.area;
         this.area.getClass();
         cell.addListener(var10001::setIdentifier);
         return new TitledCell(IvTranslations.get("reccomplex.selection.area.identifier"), cell);
      } else {
         return super.cellForIndexInSegment(table, index, segment);
      }
   }

   public boolean canVisualize() {
      return this.visualizationContext != null;
   }

   public GuiHider.Visualizer visualizer() {
      Selection selection = new Selection(this.dimensions.length);
      selection.add(this.area);
      return new SelectionQuadCache.Visualizer(selection, this.visualizationContext);
   }
}
