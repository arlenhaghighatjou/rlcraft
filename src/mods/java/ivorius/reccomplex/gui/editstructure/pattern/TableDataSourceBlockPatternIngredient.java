package ivorius.reccomplex.gui.editstructure.pattern;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.BlockPattern;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBlockPatternIngredient extends TableDataSourceSegmented {
   private BlockPattern.Ingredient ingredient;
   private TableDelegate tableDelegate;

   public TableDataSourceBlockPatternIngredient(BlockPattern.Ingredient ingredient, TableDelegate tableDelegate) {
      this.ingredient = ingredient;
      this.tableDelegate = tableDelegate;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellString cell = new TableCellString("", ingredient.identifier);
         cell.addListener((s) -> {
            ingredient.identifier = s;
         });
         return new TitledCell(IvTranslations.get("reccomplex.blockpattern.ingredient.identifier"), cell);
      }});
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.blocks"), ingredient.matcher, (Object)null));
      this.addSegment(2, new Supplier[]{() -> {
         TableCellBoolean cell = new TableCellBoolean("", ingredient.delete);
         cell.addListener((d) -> {
            ingredient.delete = d.booleanValue();
         });
         return new TitledCell(IvTranslations.get("reccomplex.blockpattern.ingredient.delete"), cell);
      }});
   }

   @Nonnull
   public String title() {
      return "Ingredient";
   }
}
