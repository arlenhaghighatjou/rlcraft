package ivorius.reccomplex.gui.editstructure.pattern;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.TableDataSourceSelection;
import ivorius.reccomplex.world.gen.feature.structure.generic.BlockPattern;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBlockPattern extends TableDataSourceSegmented {
   protected BlockPattern pattern;

   public TableDataSourceBlockPattern(BlockPattern pattern, MazeVisualizationContext visualizationContext, TableDelegate delegate, TableNavigator navigator) {
      this.pattern = pattern;
      this.addSegment(0, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return (new TableDataSourceSelection(pattern.pattern, new int[]{50, 50, 50}, delegate, navigator, true)).visualizing(visualizationContext);
      }, () -> {
         return IvTranslations.get("reccomplex.blockpattern.pattern");
      }, () -> {
         return IvTranslations.getLines("reccomplex.blockpattern.pattern.tooltip");
      }).withTitle("").buildDataSource());
      this.addSegment(1, new TableDataSourceSupplied(new Supplier[]{() -> {
         return new TitledCell((new TableCellTitle((String)null, IvTranslations.get("reccomplex.blockpattern.ingredients"))).withTooltip(IvTranslations.formatLines("reccomplex.blockpattern.ingredients.tooltip", new Object[0])));
      }}));
      this.addSegment(2, new TableDataSourceBlockPatternIngredientList(pattern.ingredients, delegate, navigator));
   }
}
